package com.sonnets.sonnet.converters;

import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDetailsService;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Here are the sonnet file type converters.
 *
 * @author Josh Harkema
 */
public abstract class SonnetConverter {
    private static final Logger logger = Logger.getLogger(SonnetDetailsService.class);
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
                    .append(">").append(s).append("</line")
                    .append(lineNumber).append(">");
            lineNumber++;
        }

        sb.append("\n\t</text>");
        sb.append("\n</sonnet>");

        logger.debug("Raw XML: " + sb.toString());

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

        logger.debug("Raw CSV: " + sb.toString());

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
            sb.append(s).append(SEPARATOR);
        }

        sb.append("\n");
        return sb.toString();
    }
}
