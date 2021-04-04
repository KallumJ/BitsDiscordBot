package util;

/**
 * A class to assist with the handling of text
 */
public class TextUtils {
    /**
     * Returns the provided string but with whitespace between every capitalised word
     *
     * @param string the string to capitalise
     * @return The capitalised string
     */
    public static String capitaliseEachWord(String string) {
        String[] arr = string.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : arr) {
            stringBuilder.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
