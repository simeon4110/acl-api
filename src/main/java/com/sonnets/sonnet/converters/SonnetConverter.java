package com.sonnets.sonnet.converters;

import com.sonnets.sonnet.models.Sonnet;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Here are the sonnet file type converters.
 *
 * @author Josh Harkema
 */
public abstract class SonnetConverter {
    private static final Logger logger = Logger.getLogger(SonnetConverter.class);
    private static final String SEPARATOR = ", ";

    private SonnetConverter() {
    }

    /**
     * Converts a Sonnet object into an XML string.
     *
     * @param sonnet a Sonnet object.
     * @return and XML string.
     */
    public static String sonnetToXML(Sonnet sonnet) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("\n<sonnet>");
        sb.append("\n\t<title>").append(sonnet.getTitle()).append("</title>");
        sb.append("\n\t<author>");
        sb.append("\n\t\t<firstName>").append(sonnet.getFirstName()).append("</firstName>");
        sb.append("\n\t\t<lastName>").append(sonnet.getLastName()).append("</lastName>");
        sb.append("\n\t</author>");
        sb.append("\n\t<text>");
        int lineNumber = 1;

        for (String s : sonnet.getText()) {
            sb.append("\n\t\t<line").append(lineNumber)
                    .append(">").append(s.trim()).append("</line")
                    .append(lineNumber).append(">");
            lineNumber++;
        }

        sb.append("\n\t</text>");
        sb.append("\n</sonnet>");

        logger.debug("Raw XML:\n" + sb.toString());

        return sb.toString();
    }

    /**
     * Outputs a list of sonnets to a csv file.
     *
     * @param sonnets a list of sonnets.
     * @return a string of csv separated sonnets (one sonnet per line).
     */
    public static String sonnetsToCSV(List<Sonnet> sonnets) {
        StringBuilder sb = new StringBuilder();

        for (Sonnet sonnet : sonnets) {
            sb.append(writeSonnetToLine(sonnet));
        }

        logger.debug("Raw CSV:\n" + sb.toString());

        return sb.toString();
    }

    /**
     * Writes a sonnet to a line in CSV format. Prints new line after each sonnet.
     *
     * @param sonnet the sonnet to write.
     * @return a string of CSV separated sonnet values.
     */
    private static String writeSonnetToLine(Sonnet sonnet) {
        StringBuilder sb = new StringBuilder();

        sb.append(sonnet.getLastName()).append(SEPARATOR).append(sonnet.getFirstName())
                .append(SEPARATOR).append(sonnet.getTitle()).append(SEPARATOR);
        for (String s : sonnet.getText()) {
            sb.append("\"").append(s.trim()).append("\"").append(SEPARATOR);
        }

        sb.append("\n");
        return sb.toString();
    }

    /**
     * Converts to TEI format. Validated by Josh Harkema on May 21, 2018.
     *
     * @param sonnet the sonnet to convert.
     * @return a TEI formatted sonnet.
     */
    public static String sonnetToTEI(Sonnet sonnet) {
        StringBuilder sb = new StringBuilder();

        // TEI header data.
        sb.append("<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">");
        sb.append("\n<teiHeader>");
        sb.append("\n\t<fileDesc>");
        sb.append("\n\t\t<titleStmt>");
        sb.append("\n\t\t\t<title>").append(sonnet.getTitle()).append("</title>");
        sb.append("\n\t\t\t<author>").append(sonnet.getFirstName()).append(" ").append(sonnet.getLastName())
                .append("</author>");
        sb.append("\n\t\t</titleStmt>");
        sb.append("\n\t\t<publicationStmt>");
        sb.append("\n\t\t\t<p>Public Domain</p>");
        sb.append("\n\t\t</publicationStmt>");
        sb.append("\n\t\t<sourceDesc>");
        sb.append("\n\t\t\t<p>Taken from Shakespeare's published works</p>");
        sb.append("\n\t\t</sourceDesc>");
        sb.append("\n\t</fileDesc>");
        sb.append("\n</teiHeader>");

        // TEI text data.
        sb.append("\n<text>");
        sb.append("\n\t<body>");
        for (String s : sonnet.getText()) {
            sb.append("\n\t\t<p>").append(s.trim()).append("</p>");
        }
        sb.append("\n\t</body>");
        sb.append("\n</text>");
        sb.append("\n</TEI>");

        logger.debug("Raw TEI:\n" + sb);

        return sb.toString();
    }
}
