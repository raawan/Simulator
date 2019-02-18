package util;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

public class JsonPathExampleForAlfresco {

    public static void main(String[] args) {
        String input = "{\n" +
                " \n" +
                "  \"nodeRef\": \"someiD\",\n" +
                "  \"fileMimeType\": \"application/pdf\"\n" +
                "}";

        FileData fileData = fileDataFrom(input);
        System.out.println(fileData.fileId());
        System.out.println(fileData.fileMimeType());

    }

    private static  FileData fileDataFrom(final String responseEntity) {
        Object responseDocument = Configuration.defaultConfiguration().jsonProvider().parse(responseEntity);
        String alfrescoAssetId = JsonPath.read(responseDocument, "$.nodeRef", new Predicate[0]).toString().replace("workspace://SpacesStore/", "");
        String mimeType = (String)JsonPath.read(responseDocument, "$.fileMimeType", new Predicate[0]);
        return new FileData(alfrescoAssetId, mimeType);
    }

}
