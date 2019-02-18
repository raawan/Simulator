package com.cmsmock.response_transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class TodaysMessageListResponseTransformer extends ResponseDefinitionTransformer {

    private static final String dir = "/tmp/TWIFMessages";
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {

        final String concatenatedStringOfTodaysFiles = getTodaysFileList();

        System.out.println("============================================");
        System.out.println(concatenatedStringOfTodaysFiles);
        System.out.println("============================================");
        return new ResponseDefinitionBuilder()
                .withHeader("Content-Type", "text/xml")
                .withStatus(200)
                .withBody(concatenatedStringOfTodaysFiles)
                .build();
    }

    @Override
    public String getName() {
        return "todays-message-list-response-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getTodaysFileList() {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            final List<Path> paths = Files.list(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .filter((val) -> val.getFileName().toString().startsWith(DATE_TODAY))
                    .collect(Collectors.toList());
            for (Path path : paths) {
                stringBuilder.append(" ").append(path.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
