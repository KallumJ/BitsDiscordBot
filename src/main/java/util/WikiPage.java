package util;

import dw.xmlrpc.DokuJClient;
import dw.xmlrpc.exception.DokuException;
import org.jsoup.Jsoup;

/**
 * A class to model a page from the Bits+ wiki
 */
public class WikiPage {
    private static final String PAGE_URL_FORMAT = "https://wiki.plus.bits.team/doku.php/%s";

    private final String id;
    private final DokuJClient client;

    /**
     * Constructs a WikiPage object
     *
     * @param id     The id of the page, e.g technology:machines:controller
     * @param client The DokuJClient object used to interact with the wiki
     */
    public WikiPage(String id, DokuJClient client) {
        this.id = id;
        this.client = client;
    }

    /**
     * A method to generate and return the title of the wiki page
     *
     * @return String, The title of the page
     */
    public String findTitle() {
        String[] split = this.id.split(":");


        // Return the relevant part of the page id
        if (split.length == 1) {
            return formatIds(this.id);
        } else {
            return formatIds(split[split.length - 1]);
        }
    }


    /**
     * A method to format a given id, e.g lava_generator -> Lava Generator
     *
     * @param id The id to format
     * @return String, the formatted version of the id
     */
    private String formatIds(String id) {
        String words = id.replaceAll("_", " ");

        String[] wordsArray = words.split(" ");

        // Capitalise the first letter of every word
        for (int i = 0; i < wordsArray.length; i++) {
            wordsArray[i] = wordsArray[i].substring(0, 1).toUpperCase() + wordsArray[i].substring(1);
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String word : wordsArray) {
            stringBuilder.append(word).append(" ");
        }

        return stringBuilder.toString();
    }

    /**
     * A method to generate and return the URL for the page
     *
     * @return String, the URL
     */
    public String findUrl() {
        return String.format(PAGE_URL_FORMAT, this.id);
    }

    /**
     * A method to find a description for the page by searching for the first sentence in the HTML of the page
     *
     * @return String, the generated description
     */
    public String findDescription() {
        String html = findPageHtml();

        // Remove HTML tags
        html = Jsoup.parse(html).text();

        // Remove the title
        html = html.replaceFirst(this.findTitle().trim() + " ", "");

        // Only return the first sentence of the page
        return html.split("\\.")[0] + ".";
    }

    /**
     * A method to find the HTML for the wiki page
     *
     * @return String, the HTML
     */
    public String findPageHtml() {
        try {
            return this.client.getPageHTML(this.id);
        } catch (DokuException ex) {
            throw new RuntimeException("A problem occured getting wiki page html", ex);
        }
    }
}
