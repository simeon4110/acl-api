package com.sonnets.sonnet.converters;

import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDetailsService;
import org.apache.log4j.Logger;

/**
 * Here are the sonnet file type converters.
 *
 * @author Josh Harkema
 */
public abstract class SonnetConverter {
    private static final Logger logger = Logger.getLogger(SonnetDetailsService.class);

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
}
