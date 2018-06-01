package com.agileengine;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRun {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationRun.class);

    private static String ELEMENT_ID = "make-everything-ok-button";

    public static void main(String[] args) {

        BasicConfigurator.configure();
        String originalPath;
        String samplePath;

        XmlAnalyzerService parser = new XmlAnalyzerService();
        if (args.length == 2) {
            originalPath = args[0];
            samplePath = args[1];
            parser.beginParsing(originalPath, samplePath, ELEMENT_ID);
        } else if (args.length == 3) {
            originalPath = args[0];
            samplePath = args[1];
            parser.beginParsing(originalPath, samplePath, args[2]);
        } else {
            LOGGER.info("Для успешной работы передайте 2 или 3 аргумента в программу");
        }
    }
}
