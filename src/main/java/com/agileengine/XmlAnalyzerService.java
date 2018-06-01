package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class XmlAnalyzerService {

    private Logger LOGGER = LoggerFactory.getLogger(XmlAnalyzerService.class);
    private String CHARSET_NAME = "utf8";

    public void beginParsing(String originalFile, String sampleFile, String elementID) {

        Optional<Element> originalElOpt = findElementById(new File(originalFile), elementID);

        if (originalElOpt.isPresent()) {
            Element originalEl = originalElOpt.get();
            Attributes attributes = originalEl.attributes();

            // Key - a number of matches beetween original file elements and sample file elements
            TreeMap<Integer, Element> elementMap = new TreeMap<>();

            for (Attribute attribute : attributes) {
                String cssQuery = buildCSSQuery(originalEl, attribute);
                Optional<Elements> sampleElementOpt = findElementsByQuery(new File(sampleFile), cssQuery);

                sampleElementOpt.ifPresent(elements -> createMapWithElements(elementMap, elements, originalEl));

            }

            if (!elementMap.isEmpty()) {
                showXMLPath(originalEl, "Original");

                //gets last element with the biggest number of matches
                Element element = elementMap.lastEntry().getValue();
                showXMLPath(element, "Sample");
            }
        }
    }

    private Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());
            return Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private Optional<Elements> findElementsByQuery(File htmlFile, String cssQuery) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());
            return Optional.of(doc.select(cssQuery));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private String buildCSSQuery(Element originalElement, Attribute originalAttribute) {
        return originalElement.tagName() + "[" + originalAttribute.getKey() + "=\"" + originalAttribute.getValue() + "\"]";
    }

    private void createMapWithElements(Map<Integer, Element> elementMap, List<Element> sampleElements, Element originalElement) {
        for (Element element : sampleElements) {
            if (!elementMap.containsValue(element)) {
                int matchesCounter = 0;
                if (element.text() != null && element.text().equals(originalElement.text())) {
                    matchesCounter++;
                }
                for (Attribute sampleAttribute : element.attributes()) {
                    String sampleAttributeKey = sampleAttribute.getKey();
                    if (originalElement.hasAttr(sampleAttributeKey) && originalElement.attr(sampleAttributeKey).equals(sampleAttribute.getValue())) {
                        matchesCounter++;
                    }
                }
                elementMap.put(matchesCounter, element);
            }
        }
    }

    private void showXMLPath(Element element, String elementName) {
        List<Element> parenttags = element.parents();
        Collections.reverse(parenttags);
        String xmlPath = parenttags.stream().map(Element::tagName).collect(Collectors.joining(" > "));
        LOGGER.info("{} element xmlPath: [{}]", elementName, xmlPath + " > " + element.tagName());
    }
}
