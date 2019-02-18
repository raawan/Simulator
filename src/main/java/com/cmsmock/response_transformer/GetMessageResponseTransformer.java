package com.cmsmock.response_transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmsmock.response_transformer.util.GlobalConstants;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class GetMessageResponseTransformer extends ResponseDefinitionTransformer {

    public  static final String GET_FILE_RESPONSE_TRANSFORMER = "get-file-response-transformer";

    @Override
    public ResponseDefinition transform(final Request request, final ResponseDefinition responseDefinition,
                                        final FileSource fileSource, final Parameters parameters) {
        System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.out.println(request.getUrl());

        String fileName = getFileName(request.getUrl());
        try {
            String fileContent = getFileContentAsString(fileName);
            System.out.println(fileContent);
            System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            return new ResponseDefinitionBuilder()
                    .withHeader("Content-Type", "text/xml")
                    .withStatus(200)
                    .withBody(fileContent)
                    .build();
        } catch (IOException e) {
            System.out.println("RETURNING 400 ON FILE READ CAUSE IOEXCEPTION");
            return new ResponseDefinitionBuilder()
                    .withHeader("Content-Type", "text/xml")
                    .withStatus(400)
                    .withBody("FileNotPresent")
                    .build();
        }
    }

    @Override
    public String getName() {
        return GET_FILE_RESPONSE_TRANSFORMER;
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getFileName(String url) {
        String pattern3 = "/message-file(.*?)$";
        Pattern pattern = Pattern.compile(pattern3);
        Matcher matcher = pattern.matcher(url);
        String messageName = "";
        if (matcher.find()) {
            messageName = matcher.group(1);
        }
        System.out.println(messageName);
        return messageName;
    }

    private static String getFileContentAsString(final String fileName) throws IOException {

        String fileContentWithSOAPEnvelope = new String(Files.readAllBytes(Paths.get(GlobalConstants.dir + fileName)));
        String contentOfMessageTag = getContentOfMessageTag(fileContentWithSOAPEnvelope);
        return contentOfMessageTag;
    }

    private static String getContentOfMessageTag(final String fileContentWithSOAPEnvelope) {

        String pattern3 = "<Message><!\\[CDATA\\[(.*?)\\]\\]></Message>";
        Pattern pattern = Pattern.compile(pattern3);
        Matcher matcher = pattern.matcher(fileContentWithSOAPEnvelope);
        String messageName = "";
        if (matcher.find()) {
            messageName = matcher.group(1);
        }
        return messageName;
    }

}