package com.sonnets.sonnet.wordtools;

import org.apache.log4j.Logger;
import tools.FormatTools;
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
    private static final Normalization thisInstance = new Normalization();
    private static VARD vard;

    public static Normalization getInstance() {
        return thisInstance;
    }


    private Normalization() {
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
     * @return normalized text.
     */
    public String normalizeText(String text) {
        StringBuilder sb = new StringBuilder();
        for (String w : FormatTools.tokenizeWords(text, true, true, true)) {
            String word = vard.normalise(w).getNormalised();
            if (word != null) {
                sb.append(word);
            } else {
                sb.append(w);
            }
            sb.append(" ");
        }
        return sb.toString();
    }
}
