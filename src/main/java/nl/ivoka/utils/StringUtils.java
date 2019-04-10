package nl.ivoka.utils;

public class StringUtils {

    private static String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};

    public static String getOrdinalSuffix(int n) {
        switch (n % 100) {
            case 11:
            case 12:
            case 13:
                return n + "th";
            default:
                return n + suffixes[n % 10];
        }
    }
}