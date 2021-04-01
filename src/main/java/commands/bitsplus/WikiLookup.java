package commands.bitsplus;

import commands.Command;
import dw.xmlrpc.DokuJClient;
import dw.xmlrpc.Page;
import dw.xmlrpc.SearchResult;
import dw.xmlrpc.exception.DokuException;
import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.http.client.utils.URIBuilder;
import util.WikiPage;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * A class to lookup queries on the Bits+ wiki
 */
public class WikiLookup extends Command {
    private static final String PAGE_NOT_FOUND_MSG = "Sorry! I couldn't find %s on the wiki! Try rewording it, or browsing for yourself here: https://wiki.plus.bits.team";
    private final DokuJClient client;
    private final List<Page> allWikiPages;

    /**
     * Constructs a WikiLookup object, loading the host, path, username, and password from properties, and establishing connection to the wiki, and collecting a List of Page objects of all the pages on the wiki
     */
    public WikiLookup() {
        super("lookup");

        final String host = Main.getProperties().getProperty("wikiHost");
        final String path = Main.getProperties().getProperty("wikiPath");
        final String username = Main.getProperties().getProperty("wikiUsername");
        final String password = Main.getProperties().getProperty("wikiPassword");

        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(host)
                    .setPath(path)
                    .build();

            this.client = new DokuJClient(uri.toString(), username, password);
            this.allWikiPages = this.client.getAllPages();

        } catch (URISyntaxException | DokuException | MalformedURLException ex) {
            throw new RuntimeException("Unable to establish connection to the wiki", ex);
        }
    }

    /**
     * A method to return the most relevant WikiPage from the supplied query
     *
     * @param query The query to search the wiki with
     * @return WikiPage, The page of the top result found
     */
    public WikiPage findWikiPage(String query) {
        try {

            // Search for an exact match, and return if found
            String queriedPageId = query.toLowerCase().replaceAll(" ", "_");

            for (Page page : allWikiPages) {
                String[] pageIdArr = page.id().split(":");

                // If the queried page id matches the page id of the current page
                if (queriedPageId.equals(pageIdArr[pageIdArr.length - 1])) {
                    return new WikiPage(page.id(), this.client);
                }
            }

            // Else, search for the closest match
            List<SearchResult> results = this.client.search(query);

            SearchResult topResult = null;
            int topResultScore = 0;

            // Find result with the top score
            for (SearchResult result : results) {
                if (result.score() > topResultScore) {
                    topResultScore = result.score();
                    topResult = result;
                }
            }

            return new WikiPage(Objects.requireNonNull(topResult).id(), this.client);

        } catch (DokuException ex) {
            throw new RuntimeException("Unable to search the wiki", ex);
        }

    }

    /**
     * A method to generate an embed with a link to the most relevant wiki page, and a description of it
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String query = input.replace("lookup ", "");

        try {
            WikiPage page = findWikiPage(query);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(page.findTitle(), page.findUrl());
            embedBuilder.setColor(new Color(37, 171, 227));
            embedBuilder.setDescription(page.findDescription());
            embedBuilder.setAuthor("Is this not quite right? Browse for yourself here", "https://wiki.plus.bits.team");

            event.getChannel().sendMessage("This is the best match I could find: ").queue();
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        } catch (NullPointerException ex) {
            event.getChannel().sendMessage(String.format(PAGE_NOT_FOUND_MSG, query)).queue();
        }

    }
}
