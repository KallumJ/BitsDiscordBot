package commands.bitsplus;

import commands.Command;
import dw.xmlrpc.DokuJClient;
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

/**
 * A class to lookup queries on the Bits+ wiki
 */
public class WikiLookup extends Command {
    private static final String PAGE_NOT_FOUND_MSG = "Sorry! I couldn't find %s on the wiki! Try rewording it, or browsing for yourself here: https://wiki.plus.bits.team";
    public final DokuJClient client;

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

        } catch (URISyntaxException | DokuException | MalformedURLException ex) {
            throw new RuntimeException("Unable to establish connection to the wiki", ex);
        }
    }

    /**
     * A method to return the top SearchResult from the supplied query
     * @param query The query to search the wiki with
     * @return SearchResult, The top result found
     */
    public SearchResult findWikiPage(String query) {
        try {
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

            return topResult;

        } catch (DokuException ex) {
            throw new RuntimeException("Unable to search the wiki", ex);
        }

    }

    /**
     * A method to generate an embed with a link to the most relevant wiki page, and a description of it
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String query = input.replace("lookup ", "");

        try {
            SearchResult result = findWikiPage(query);
            WikiPage page = new WikiPage(result.id(), this.client);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(page.findTitle(), page.findUrl());
            embedBuilder.setColor(new Color(37, 171, 227));
            embedBuilder.setDescription(page.findDescription());


            event.getChannel().sendMessage("This is the best match I could find: ").queue();
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        } catch (NullPointerException ex) {
            event.getChannel().sendMessage(String.format(PAGE_NOT_FOUND_MSG, query)).queue();
        }

    }
}
