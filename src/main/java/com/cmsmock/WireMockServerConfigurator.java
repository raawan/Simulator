package com.cmsmock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.requestMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.seeOther;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;

public class WireMockServerConfigurator {

    private static final String dir = "/tmp/TWIFMessages";
    public static final String TWIF_DATE_FORMAT_WITH_HHMM = "dd_MM_yyyyHH_mm";
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY_WITH_HHMM = new SimpleDateFormat(TWIF_DATE_FORMAT_WITH_HHMM).format(new Date());
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());

    private static final String TWIF_MESSAGE_LIST_URL = "/message-list?date=today";
    private static final String TWIF_MESSAGE_FILE_URL = "/message-file/";

    public static void main(String[] args) {

        new WireMockServerConfigurator().initializeWireMockServer();

    }

    public void initializeWireMockServer() {

        WireMockServer wireMockServer = new WireMockServer(wireMockConfig()
                                                            .port(8091)
                                                            .extensions(ResponseTransformer.class,ResponseTransformerSpecificUrl.class));
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathMatching(TWIF_MESSAGE_FILE_URL+DATE_TODAY+"\\d{2}_\\d{2}_.*"))
                .willReturn(aResponse()
                        .withTransformer("response-transformer-specific-url",null,null)
                ));

        wireMockServer.stubFor(get(urlEqualTo(TWIF_MESSAGE_LIST_URL))
                .willReturn(aResponse()
                        .withTransformer("response-transformer",null,null)
                ));


        wireMockServer.stubFor(post(urlPathEqualTo("/TWIF/C2IOutbound.asmx"))
                .andMatching(this::someCustomMatcher)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody("SOAP response Body")));

    }


    public MatchResult someCustomMatcher(final Request request) {
        System.out.println("++++++++++++++++FILE_DUMPED++++++++++++++++");
        String requestId= getRequestId(request.getBodyAsString());
        createFile(request.getBodyAsString(),requestId);
        return MatchResult.of(true);

    }

    private String getRequestId(final String bodyAsString) {
        Pattern pattern = Pattern.compile("<RequestID>(.*?)</RequestID>");
        Matcher matcher = pattern.matcher(bodyAsString);
        String messageName="";
        if (matcher.find())
        {
            messageName = matcher.group(1);
        }
        return messageName;
    }

    private void createFile(final String xmlValue, String requestId) {

        final String timeInMillis = Long.toString(System.currentTimeMillis());
        String fileName =  dir + "/"+ DATE_TODAY_WITH_HHMM +"_"+requestId+".xml";
        Path path = Paths.get(fileName);
        try {
            Files.createDirectories(Paths.get(dir));
            Files.write(path, xmlValue.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


