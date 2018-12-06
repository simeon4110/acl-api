package com.sonnets.sonnet.wordtools;

import org.apache.log4j.Logger;
import vardwrapper.VARD;

import java.io.File;
import java.io.IOException;

/**
 * Class to interface with vard-wrapper API.
 *
 * @author Josh Harkema
 */
public class Normalization {
    private static final Logger LOGGER = Logger.getLogger(Normalization.class);
    private static final String filePath = "setup/";
    private static VARD vard;

    /**
     * An instance of this class should be treated like a singleton.
     */
    public Normalization() {
        try {
            File setupFile = new File(filePath);
            vard = new VARD(setupFile, 50.0, 1.0, true);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Runs text through the VARD normalizer.
     *
     * @param text the text to normalize.
     * @return normalized text (Null is returned if the word cannot/should not be normalized.
     */
    public String normalizeText(final String text) {
        StringBuilder sb = new StringBuilder();
        for (String w : text.split(" ")) {
            sb.append(vard.normalise(w).getNormalised());
            sb.append(" ");
        }
        return sb.toString();
    }
}
