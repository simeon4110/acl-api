package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.tools.SonnetConverter;
import com.sonnets.sonnet.tools.TestSonnetFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the output of the sonnet converter.
 *
 * @author Josh Harkema
 */
public class SonnetConverterTests {
    private static final Logger logger = Logger.getLogger(SonnetConverterTests.class);
    @Test
    public void testXMLConverter() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("\n<sonnet>");
        sb.append("\n\t<title>Auto Generated Test Sonnet</title>");
        sb.append("\n\t<author>");
        sb.append("\n\t\t<firstName>Bobby</firstName>");
        sb.append("\n\t\t<lastName>Brown</lastName>");
        sb.append("\n\t</author>");
        sb.append("\n\t<text>");
        sb.append("\n\t\t<line1>This is the sonnet that doesn't end...</line1>");
        sb.append("\n\t\t<line2>Yes it goes on and on MY FRIEND!</line2>");
        sb.append("\n\t</text>");
        sb.append("\n</sonnet>");
        SonnetDto toTestDto = TestSonnetFactory.specificSonnetGenerator();
        Sonnet toTest = new Sonnet(toTestDto);

        String output = SonnetConverter.sonnetToXML(toTest);

        System.out.println(sb.toString());
        Assert.assertEquals(output, sb.toString());
    }

    @Test
    public void testCSVConverter() {
        SonnetDto toTestDto = TestSonnetFactory.specificSonnetGenerator();
        Sonnet toTest = new Sonnet(toTestDto);
        Sonnet toTest1 = new Sonnet(toTestDto);

        List<Sonnet> sonnets = new ArrayList<>();
        sonnets.add(toTest);
        sonnets.add(toTest1);

        String output = SonnetConverter.sonnetsToCSV(sonnets);

        try {
            File outputFile = new File("src/test/resources/valid/validCSVTest.csv");
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(output);
            fileWriter.close();

            // File for comparison.
            File goodFile = new File("src/test/resources/valid/validCSV.csv");

            Assert.assertEquals(FileUtils.readFileToString(goodFile, "utf-8"),
                    FileUtils.readFileToString(outputFile, "utf-8"));

        } catch (IOException e) {
            logger.error(e);
        }

    }

    @Test
    public void testTEIConverter() {
        SonnetDto toTestDto = TestSonnetFactory.specificSonnetGenerator();
        Sonnet toTest = new Sonnet(toTestDto);

        String output = SonnetConverter.sonnetToTEI(toTest);

        try {
            File outputFile = new File("src/test/resources/valid/validTEITest.xml");
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(output);
            fileWriter.close();

            File goodFile = new File("src/test/resources/valid/validTEI.xml");

            Assert.assertEquals(FileUtils.readFileToString(goodFile, "utf-8"),
                    FileUtils.readFileToString(outputFile, "utf-8"));

        } catch (IOException e) {
            logger.error(e);
        }
    }

}
