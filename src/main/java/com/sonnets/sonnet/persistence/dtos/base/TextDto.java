package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

/**
 * @author Josh Harkema
 */
public class TextDto {
    @NotEmpty
    private String text;
    private String[] customStopWords;
    private int numberOfTopics;
    private int numberOfTerms;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getCustomStopWords() {
        return customStopWords;
    }

    public void setCustomStopWords(String[] customStopWords) {
        this.customStopWords = customStopWords;
    }

    public int getNumberOfTopics() {
        return numberOfTopics;
    }

    public void setNumberOfTopics(int numberOfTopics) {
        this.numberOfTopics = numberOfTopics;
    }

    public int getNumberOfTerms() {
        return numberOfTerms;
    }

    public void setNumberOfTerms(int numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
    }

    @Override
    public String toString() {
        return "TextDto{" +
                "text='" + text + '\'' +
                ", customStopWords=" + Arrays.toString(customStopWords) +
                ", numberOfTopics=" + numberOfTopics +
                ", numberOfTerms=" + numberOfTerms +
                '}';
    }
}
