package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.WordTools.KWIC;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class KWICTest {
    @Test
    public void kwicTest() {
        String text = "I am already far north of London, and as I walk in the streets of\n" +
                "Petersburgh, I feel a cold northern breeze play upon my cheeks, which\n" +
                "braces my nerves and fills me with delight.  Do you understand this\n" +
                "feeling?  This breeze, which has travelled from the regions towards\n" +
                "which I am advancing, gives me a foretaste of those icy climes.\n" +
                "Inspirited by this wind of promise, my daydreams become more fervent\n" +
                "and vivid.  I try in vain to be persuaded that the pole is the seat of\n" +
                "frost and desolation; it ever presents itself to my imagination as the\n" +
                "region of beauty and delight.  There, Margaret, the sun is for ever\n" +
                "visible, its broad disk just skirting the horizon and diffusing a\n" +
                "perpetual splendour.  There—for with your leave, my sister, I will put\n" +
                "some trust in preceding navigators—there snow and frost are banished;\n" +
                "and, sailing over a calm sea, we may be wafted to a land surpassing in\n" +
                "wonders and in beauty every region hitherto discovered on the habitable\n" +
                "globe.  Its productions and features may be without example, as the\n" +
                "phenomena of the heavenly bodies undoubtedly are in those undiscovered\n" +
                "solitudes.  What may not be expected in a country of eternal light?  I\n" +
                "may there discover the wondrous power which attracts the needle and may\n" +
                "regulate a thousand celestial observations that require only this\n" +
                "voyage to render their seeming eccentricities consistent for ever.  I\n" +
                "shall satiate my ardent curiosity with the sight of a part of the world\n" +
                "never before visited, and may tread a land never before imprinted by\n" +
                "the foot of man. These are my enticements, and they are sufficient to\n" +
                "conquer all fear of danger or death and to induce me to commence this\n" +
                "laborious voyage with the joy a child feels when he embarks in a little\n" +
                "boat, with his holiday mates, on an expedition of discovery up his\n" +
                "native river. But supposing all these conjectures to be false, you\n" +
                "cannot contest the inestimable benefit which I shall confer on all\n" +
                "mankind, to the last generation, by discovering a passage near the pole\n" +
                "to those countries, to reach which at present so many months are\n" +
                "requisite; or by ascertaining the secret of the magnet, which, if at\n" +
                "all possible, can only be effected by an undertaking such as mine.";

        List<Map.Entry<String, String>> contexts = KWIC.searchByWord(text, "me", 3);
        for (Map.Entry<String, String> i : contexts) {
            System.out.println(i);
        }
    }
}
