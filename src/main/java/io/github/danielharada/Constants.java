package io.github.danielharada;

/**
 * Created by daniel.harada on 17-07-28.
 */
public class Constants {

    public final static String dataStoreDirectory = "./datastore";
    public final static String inputDelimiter = "|";  // For use when not going through String.split()
    public final static String escapedInputDelimiter = "\\|";  // String.split() requires escaped pipe due to regex
    public final static String outputDelimiter = ",";  // We want to print query results comma delimited
    public final static int stbMaxLength = 64;
    public final static int titleMaxLength = 64;
    public final static int providerMaxLength = 64;
    public final static String dateFormat = "YYYY-MM-DD";  // Must update validateEntryFormat method if this changes
    public final static String revFormat = "1.00"; // Must update validateEntryFormat method if this changes
    public final static String viewtimeFormat = "H:mm";  // Must update validateEntryFormat if this changes
}
