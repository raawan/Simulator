package com.cmsmock;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class ResponseTransformerSpecificUrl extends ResponseDefinitionTransformer {

    private static final String dir = "/tmp/TWIFMessages";
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());

    @Override
    public ResponseDefinition transform(final Request request, final ResponseDefinition responseDefinition,
                                        final FileSource fileSource, final Parameters parameters) {
        System.out.println("**************************************************************");
        System.out.println(request.getAbsoluteUrl());
        //System.out.println(request.getUrl());
        System.out.println("**************************************************************");
        return new ResponseDefinitionBuilder()
                .withHeader("Content-Type", "text/xml")
                .withStatus(200)
                .withBody("<response>1</response>")
                .build();
    }

    @Override
    public String getName() {
         return "response-transformer-specific-url";
    }
}
