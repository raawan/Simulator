package com.cmsmock;

import static com.cmsmock.response_transformer.GetMessageResponseTransformer.GET_FILE_RESPONSE_TRANSFORMER;
import static com.cmsmock.response_transformer.MessageReceivedResponseTransformer.MESSAGE_RECEIVED_RESPONSE_TRANSFORMER;
import static com.cmsmock.response_transformer.TodaysMessageListResponseTransformer.TODAYS_MESSAGE_LIST_RESPONSE_TRANSFORMER;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmsmock.response_transformer.GetMessageResponseTransformer;
import com.cmsmock.response_transformer.MessageReceivedResponseTransformer;
import com.cmsmock.response_transformer.TodaysMessageListResponseTransformer;
import com.github.tomakehurst.wiremock.WireMockServer;

public class WireMockServerConfigurator {

    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());


    public static void main(String[] args) {

        new WireMockServerConfigurator().initializeWireMockServer();
    }

    public void initializeWireMockServer() {

        WireMockServer wireMockServer = new WireMockServer(wireMockConfig()
                .port(8091)
                .extensions(GetMessageResponseTransformer.class,
                        MessageReceivedResponseTransformer.class,
                        TodaysMessageListResponseTransformer.class));

        wireMockServer.start();

        wireMockServer.stubFor(post(urlPathEqualTo("/TWIF/C2IOutbound.asmx"))
                .willReturn(aResponse()
                        .withTransformer(MESSAGE_RECEIVED_RESPONSE_TRANSFORMER,
                                null, null)
                ));

        wireMockServer.stubFor(get(urlEqualTo("/message-list?date=today"))
                .willReturn(aResponse()
                        .withTransformer(TODAYS_MESSAGE_LIST_RESPONSE_TRANSFORMER,
                                null, null)
                ));
        wireMockServer.stubFor(get(urlPathMatching("/message-file/" + DATE_TODAY + "\\d{2}_\\d{2}_.*"))
                .willReturn(aResponse()
                        .withTransformer(GET_FILE_RESPONSE_TRANSFORMER,
                                null, null)
                ));

    }

}


