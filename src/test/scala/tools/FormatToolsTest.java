package tools;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FormatToolsTest {

    @Test
    public void tokenizeWords() {
        String toTokenize = "a HelLo world!";
        String[] tokens = FormatTools.tokenizeWords(toTokenize, true, true, true);
        String[] expected = {"a", "hello", "world"};
        assertArrayEquals(tokens, expected);
    }

    @Test
    public void arrayToString() {
        String[] toFormat = {"a", "hello", "world"};
        String output = FormatTools.arrayToString(toFormat);
        assertEquals("a hello world", output);
    }

    @Test
    public void removePunctuation() {
        String start = "this-is-a-test.!,";
        String result = FormatTools.removePunctuation(start);
        assertEquals("this is a test.", result);
    }
}