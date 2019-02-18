package com.cmsmock.response_transformer;

import static com.cmsmock.response_transformer.util.GlobalConstants.DATE_TODAY_WITH_HHMM;
import static com.cmsmock.response_transformer.util.GlobalConstants.dir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class MessageReceivedResponseTransformer extends ResponseDefinitionTransformer {

    public static final String MESSAGE_RECEIVED_RESPONSE_TRANSFORMER = "message-received-response-transformer";

    @Override
    public ResponseDefinition transform(final Request request, final ResponseDefinition responseDefinition, final FileSource fileSource, final Parameters parameters) {

        System.out.println("++++++++++++++++FILE_DUMPED++++++++++++++++");
        String requestId = getRequestId(request.getBodyAsString());
        createFile(request.getBodyAsString(), requestId);
        return new ResponseDefinitionBuilder()
                .withHeader("Content-Type", "text/xml")
                .withStatus(200)
                .withBody(generateSuccessResponse(requestId))
                .build();
    }

    @Override
    public String getName() {
        return MESSAGE_RECEIVED_RESPONSE_TRANSFORMER;
    }

    private String getRequestId(final String bodyAsString) {
        Pattern pattern = Pattern.compile("<RequestID>(.*?)</RequestID>");
        Matcher matcher = pattern.matcher(bodyAsString);
        String messageName = "";
        if (matcher.find()) {
            messageName = matcher.group(1);
        }
        return messageName;
    }


    @Override
    public boolean applyGlobally() {
        return false;
    }

    private void createFile(final String xmlValue, String requestId) {

        String fileName = dir + "/" + DATE_TODAY_WITH_HHMM + "_" + requestId + ".xml";
        Path path = Paths.get(fileName);
        try {
            Files.createDirectories(Paths.get(dir));
            Files.write(path, xmlValue.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateSuccessResponse(String requestId) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://schemas.cjse.gov.uk/endpoint/types/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <typ:SubmitResponseMes>\n" +
                "         <typ:RequestID>" + requestId + "</typ:RequestID>\n" +
                "         <typ:ResponseCode>1</typ:ResponseCode>\n" +
                "         <typ:ResponseText>SUCCESS_1</typ:ResponseText>\n" +
                "      </typ:SubmitResponseMes>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }


}
