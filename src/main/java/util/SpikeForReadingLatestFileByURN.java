package util;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static java.lang.String.format;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jayway.restassured.path.xml.XmlPath;
import static util.TwifMessage.*;




public class SpikeForReadingLatestFileByURN {

    private static final String TWIF_ENDPOINT_URL = "http://someLocation";
    public static final String PTIURN = "C2IRequest.'**'.find {node -> node.name() == 'PTIURN'}";

    private static final int RETRY_COUNT = 15;
    private static final int RETRY_INTERVAL = 1000;
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";

    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());
    private static final Map<TwifMessage, String> messageRegExMap = new HashMap<>();

    private static final String TWIF_C2I05_FILE_REGEX = "(%s\\d{2}_\\d{2}_C2I05_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_C2I08_FILE_REGEX = "(%s\\d{2}_\\d{2}_C2I08_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_C2I01_FILE_REGEX = "(%s\\d{2}_\\d{2}_C2I01_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_C2I04_FILE_REGEX = "(%s\\d{2}_\\d{2}_C2I04_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_LM06_FILE_REGEX = "(%s\\d{2}_\\d{2}_LMID06_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_C2I09_FILE_REGEX = "(%s\\d{2}_\\d{2}_C2I09_.{8}-.{4}-.{4}-.{4}-.{12}.xml)";
    private static final String TWIF_C2I02_FILE_REGEX = "%s\\d{2}_\\d{2}_%s.xml";
    private static final String TWIF_SUBSEQUENT_CM01_FILE_REGEX = "(%s\\d{2}_\\d{2}_\\d*_SUBSEQUENT_CM01.xml)";
    private static final String TWIF_FIRST_CM01_FILE_REGEX = "(%s\\d{2}_\\d{2}_\\d*_FIRST_CM01.xml)";

    static {
        messageRegExMap.put(C2I01_CPP_TO_CMS, TWIF_C2I01_FILE_REGEX);
        messageRegExMap.put(C2I02, TWIF_C2I02_FILE_REGEX);
        messageRegExMap.put(C2I04, TWIF_C2I04_FILE_REGEX);
        messageRegExMap.put(C2I05, TWIF_C2I05_FILE_REGEX);
        messageRegExMap.put(C2I08, TWIF_C2I08_FILE_REGEX);
        messageRegExMap.put(C2I09, TWIF_C2I09_FILE_REGEX);
        messageRegExMap.put(LMID06, TWIF_LM06_FILE_REGEX);
        messageRegExMap.put(SUBSEQUENT_CM01, TWIF_SUBSEQUENT_CM01_FILE_REGEX);
        messageRegExMap.put(FIRST_CM01, TWIF_FIRST_CM01_FILE_REGEX);
    }

    public static void main(String[] args) {

        final String regex = format(getMessageRegEx(LMID06), DATE_TODAY, "LMID06");
        System.out.println(DATE_TODAY);
        System.out.println(regex);
        final List<String> matchingFiles = getMatchingFiles(regex);
        System.out.println(matchingFiles.size());
        matchingFiles.forEach(var -> System.out.println(var));
    }

    private static String getMessageRegEx(final TwifMessage twifMessage) {
        final String regEx = messageRegExMap.get(twifMessage);
        return regEx;
    }

    private static  List<String> getMatchingFiles(final String regex) {
        final String input = "08_02_201916_44_LMID06_abcdqwer-abcd-abcd-abcd-154955784864.xml " +
                "08_02_201917_27_1549566824689_LMID06.xml " +
                "08_02_201916_44_LMID06_abcdqwer-abcd-abcd-abcd-154955784224.xml";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(input);
        int from = 0;
        final List<String> xmlFiles = new ArrayList<>();
        while (matcher.find(from)) {
            if (!xmlFiles.contains(matcher.group(0)))
                xmlFiles.add(matcher.group(0));
            from = matcher.start() + 1;
        }
        return xmlFiles;
    }

}

enum TwifMessage {

    LMID06("CMS", "CPP", "LM06.xml"),
    C2I05("CPP", "CMS", "C2I05.xml"),
    C2I02("CPP", "CMS", "C2I02.xml"),
    C2I04("CPP", "CMS", "C2I04.xml"),
    C2I08("CPP", "CMS", "C2I08.xml"),
    C2I09("CPP", "CMS", "C2I09.xml"),
    FIRST_CM01("CPP", "CMS", "FIRST_CM01.xm"),
    SUBSEQUENT_CM01("CPP", "CMS", "SUBSEQUENT_CM01.xml"),
    C2I01_CPP_TO_CMS("CPP", "CMS", "C2I01_CPP_TO_CMS.xml");

    public final static String PATH = "GatewayMessages";
    private final String source;
    private final String destination;
    private final String fileName;

    TwifMessage(final String source, final String destination, final String fileName) {
        this.source = source;
        this.destination = destination;
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getFilePath() {
        return PATH + "/" + fileName;
    }
}