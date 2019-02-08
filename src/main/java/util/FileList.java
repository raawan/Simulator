package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FileList {

    private static final String dir = "/tmp/TWIFMessages";
    public static final String TWIF_DATE_FORMAT = "dd_MM_yyyy";

    private static final String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            final List<Path> paths = Files.list(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .filter((val) -> val.getFileName().toString().startsWith(DATE_TODAY))
                    .collect(Collectors.toList());
            //paths.forEach(val -> System.out.println(val.getFileName().toString()));

            for(Path path : paths) {
                stringBuilder.append(" ").append(path.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringBuilder.toString());
    }
}
