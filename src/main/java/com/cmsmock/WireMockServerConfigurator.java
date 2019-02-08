package com.cmsmock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
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
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyyHH_mm";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());

    public static void main(String[] args) {

        new WireMockServerConfigurator().initializeWireMockServer();

    }

    public void initializeWireMockServer() {

        WireMockServer wireMockServer = new WireMockServer(wireMockConfig()
                                                            .port(8091)
                                                            .extensions(ResponseTransformer.class));
        wireMockServer.start();

        wireMockServer.stubFor(post(urlEqualTo("/TWIF/C2IOutbound.asmx"))
                .andMatching(this::someCustomMatcher)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody("SOAP response Body")));

        wireMockServer.stubFor(get(urlEqualTo("/TWIFMessages/"))
                            .willReturn(aResponse()
                                    .withTransformer("response-transformer",null,null)
                ));
    }


    public MatchResult someCustomMatcher(final Request request) {
        String requestId= getRequestId(request.getBodyAsString());
        createFile(request.getBodyAsString(),requestId);
        return MatchResult.of(true);

    }

    private String getRequestId(final String bodyAsString) {
        Pattern pattern = Pattern.compile("<RequestID>REQUEST_ID_(.*?)</RequestID>");
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
        String fileName =  dir + "/"+DATE_TODAY+"_"+timeInMillis+"_"+requestId+".xml";
        Path path = Paths.get(fileName);
        try {
            Files.createDirectories(Paths.get(dir));
            Files.write(path, xmlValue.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


