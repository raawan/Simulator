package com.cmsmock.response_transformer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface GlobalConstants {

    String dir = "/tmp/TWIFMessages";
    String TWIF_DATE_FORMAT_WITH_HHMM = "dd_MM_yyyyHH_mm";
    String DATE_TODAY_WITH_HHMM = new SimpleDateFormat(TWIF_DATE_FORMAT_WITH_HHMM).format(new Date());
    String TWIF_DATE_FORMAT = "dd_MM_yyyy";
    String DATE_TODAY = new SimpleDateFormat(TWIF_DATE_FORMAT).format(new Date());
    String FILE_LIST_SEPARATOR = " ";

}
