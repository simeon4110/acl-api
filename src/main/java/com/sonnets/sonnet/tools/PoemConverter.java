package com.sonnets.sonnet.tools;

import com.sonnets.sonnet.persistence.models.poetry.Poem;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Here are the poem file type tools. Tool class, cannot instantiate.
 *
 * @author Josh Harkema
 */
public abstract class PoemConverter {
    private static final Logger LOGGER = Logger.getLogger(PoemConverter.class);
    private static final String SEPARATOR = ", ";

    private PoemConverter() {
    }

    /**
     * Converts a Poem object into an XML string.
     *
     * @param poem a Poem object.
     * @return and XML string.
     */
    public static String poemToXML(Poem poem) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("\n<poem>");
        sb.append("\n\t<title>").append(poem.getTitle()).append("</title>");
        sb.append("\n\t<author>");
        sb.append("\n\t\t<firstName>").append(poem.getAuthor().getFirstName()).append("</firstName>");
        sb.append("\n\t\t<lastName>").append(poem.getAuthor().getLastName()).append("</lastName>");
        sb.append("\n\t</author>");
        sb.append("\n\t<text>");
        int lineNumber = 1;

        for (String s : poem.getText()) {
            sb.append("\n\t\t<line").append(lineNumber)
                    .append(">").append(s.trim()).append("</line")
                    .append(lineNumber).append(">");
            lineNumber++;
        }

        sb.append("\n\t</text>");
        sb.append("\n</poem>");

        LOGGER.debug("Raw XML:\n" + sb.toString());
        return sb.toString();
    }

    /**
     * Outputs a list of poems to a csv file.
     *
     * @param poems a list of poems.
     * @return a string of csv separated poems (one poem per line).
     */
    public static String poemsToCSV(List<Poem> poems) {
        StringBuilder sb = new StringBuilder();

        for (Poem p : poems) {
            sb.append(writePoemToLine(p));
        }

        LOGGER.debug("Raw CSV:\n" + sb.toString());
        return sb.toString();
    }

    /**
     * Writes a poem to a line in CSV format. Prints new line after each poem.
     *
     * @param poem the poem to write.
     * @return a string of CSV separated poem values.
     */
    private static String writePoemToLine(Poem poem) {
        StringBuilder sb = new StringBuilder();

        sb.append(poem.getAuthor().getLastName()).append(SEPARATOR)
                .append(poem.getAuthor().getFirstName())
                .append(SEPARATOR).append(poem.getTitle()).append(SEPARATOR);
        for (String s : poem.getText()) {
            sb.append("\"").append(s.trim()).append("\"").append(SEPARATOR);
        }

        sb.append("\n");
        return sb.toString();
    }

    /**
     * Converts to TEI format. Validated by Josh Harkema on May 21, 2018.
     *
     * @param poem the poem to convert.
     * @return a TEI formatted poem.
     */
    public static String poemToTEI(Poem poem) {
        StringBuilder sb = new StringBuilder();

        // TEI header data.
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("\n<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">");
        sb.append("\n<teiHeader>");
        sb.append("\n\t<fileDesc>");
        sb.append("\n\t\t<titleStmt>");
        sb.append("\n\t\t\t<title>").append(poem.getTitle()).append("</title>");
        sb.append("\n\t\t\t<author>").append(poem.getAuthor().getFirstName())
                .append(" ").append(poem.getAuthor().getLastName())
                .append("</author>");
        sb.append("\n\t\t</titleStmt>");
        sb.append("\n\t\t<publicationStmt>");
        sb.append("\n\t\t\t<p>").append(poem.getPublicationStmt()).append("</p>");
        sb.append("\n\t\t</publicationStmt>");
        sb.append("\n\t\t<sourceDesc>");
        sb.append("\n\t\t\t<p>").append(poem.getSourceDesc()).append("</p>");
        sb.append("\n\t\t</sourceDesc>");
        sb.append("\n\t</fileDesc>");
        sb.append("\n</teiHeader>");

        // TEI text data.
        sb.append("\n<text>");
        sb.append("\n\t<body>");
        for (String s : poem.getText()) {
            sb.append("\n\t\t<p>").append(s.trim()).append("</p>");
        }
        sb.append("\n\t</body>");
        sb.append("\n</text>");
        sb.append("\n</TEI>");

        LOGGER.debug("Raw TEI:\n" + sb);

        return sb.toString();
    }

    /**
     * Converts a list of Sonnets into plain text version of only the lines of poetry. No metadata.
     *
     * @param poems the poems to convert.
     * @return a string with all the poems.
     */
    public static String poemsToText(List<Poem> poems) {
        StringBuilder sb = new StringBuilder();
        for (Poem p : poems) {
            for (String s : p.getText()) {
                sb.append(s.trim()).append("\n");
            }
        }
        return sb.toString();
    }
}
