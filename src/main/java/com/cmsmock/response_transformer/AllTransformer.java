package com.cmsmock.response_transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class AllTransformer extends ResponseDefinitionTransformer {

    private static final String dir = "/tmp/TWIFMessages";
    public static final String TWIF_DATE_FORMAT_WITH_HHMM = "dd_MM_yyyyHH_mm";
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());
    private static final String DATE_TODAY_WITH_HHMM = new SimpleDateFormat(TWIF_DATE_FORMAT_WITH_HHMM).format(new Date());

    @Override
    public ResponseDefinition transform(final Request request, final ResponseDefinition responseDefinition,
                                        final FileSource fileSource, final Parameters parameters) {
        if (request.getMethod().getName().equalsIgnoreCase("POST") &&
                request.getUrl().equalsIgnoreCase("/TWIF/C2IOutbound.asmx")) {

            System.out.println("++++++++++++++++FILE_DUMPED++++++++++++++++");
            String requestId = getRequestId(request.getBodyAsString());
            createFile(request.getBodyAsString(), requestId);
            return new ResponseDefinitionBuilder()
                    .withHeader("Content-Type", "text/xml")
                    .withStatus(200)
                    .withBody(generateSuccessResponse(requestId))
                    .build();

        } else if (request.getMethod().getName().equalsIgnoreCase("GET") &&
                request.getUrl().equalsIgnoreCase("/message-list?date=today")) {
            final String concatenatedStringOfTodaysFiles = getTodaysFileList();

            System.out.println("============================================");
            System.out.println(concatenatedStringOfTodaysFiles);
            System.out.println("============================================");
            return new ResponseDefinitionBuilder()
                    .withHeader("Content-Type", "text/xml")
                    .withStatus(200)
                    .withBody(concatenatedStringOfTodaysFiles)
                    .build();

        } else if (request.getMethod().getName().equalsIgnoreCase("GET") &&
                request.getUrl().contains("/message-file/")) {
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
        } else {
            return new ResponseDefinitionBuilder()
                    .withHeader("Content-Type", "text/xml")
                    .withStatus(400)
                    .withBody("<response>BAD REQUEST</response>")
                    .build();

        }
    }


    @Override
    public String getName() {
        return "all-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
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

    private void createFile(final String xmlValue, String requestId) {

        final String timeInMillis = Long.toString(System.currentTimeMillis());
        String fileName = dir + "/" + DATE_TODAY_WITH_HHMM + "_" + requestId + ".xml";
        Path path = Paths.get(fileName);
        try {
            Files.createDirectories(Paths.get(dir));
            Files.write(path, xmlValue.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String fileContentWithSOAPEnvelope =  new String(Files.readAllBytes(Paths.get(dir + fileName)));
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

    private static String generateSuccessResponse(String requestId) {
        return  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://schemas.cjse.gov.uk/endpoint/types/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <typ:SubmitResponseMes>\n" +
                "         <typ:RequestID>" +requestId+"</typ:RequestID>\n" +
                "         <typ:ResponseCode>1</typ:ResponseCode>\n" +
                "         <typ:ResponseText>SUCCESS_1</typ:ResponseText>\n" +
                "      </typ:SubmitResponseMes>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}
