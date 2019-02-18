package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexForURLExtraction {

    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());
    private static final String dir = "/tmp/TWIFMessages";

    public static void main(String[] args) throws IOException {

        String url = "http://localhost:8091/message-file/14_02_201910_27_C2I09_8cfa1b28-9076-452c-ae89-11c255b8f11a.xml";
        String pattern1 = "(" + DATE_TODAY + "\\d{2}_\\d{2}_\\.*.xml" + ")";
        String pattern2 = "http://localhost:8091/message-file/(.*?)$";
        String pattern3 = "/message-file(.*?)$";

        Pattern pattern = Pattern.compile(pattern3);
        Matcher matcher = pattern.matcher(url);
        String messageName="";
        System.out.println(messageName);
        if (matcher.find())
        {
            messageName = matcher.group(1);
        }
        System.out.println(messageName);
        getFileContentAsString(messageName);
    }


    private static String getFileContentAsString(final String fileName) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(dir+fileName)));
        System.out.println(content);
        String contentOfMessageTag = getContentOfMessageTag(content);
        System.out.println("===============");
        System.out.println(contentOfMessageTag);
        System.out.println("===============");
        return content;
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
