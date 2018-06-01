package com.agileengine;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
public class XmlAnalyzerServiceTest {

    private XmlAnalyzerService xmlAnalyzerService;
    private File successOriginFile;
    private File successSampleFile;
    private File failFile;
    private Element sampleElement;
    private Element originElement;
    private String cssQuery;

    @Before
    public void before() {
        xmlAnalyzerService = new XmlAnalyzerService();
        successOriginFile = new File("samples/sample-0-origin.html");
        successSampleFile = new File("samples/sample-1-evil-gemini.html");
        failFile = new File("fileNotExisted");
        sampleElement = new Element("a");
        originElement = new Element("<a id=\"make-everything-ok-button\" class=\"btn btn-success\" href=\"#ok\" title=\"Make-Button\" rel=\"next\" onclick=\"javascript:window.okDone(); return false;\"> Make everything OK </a>");
        cssQuery = "a[id=\"make-everything-ok-button\"]";
    }


    @Test
    public void findElementByIdTestFail() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = XmlAnalyzerService.class.getDeclaredMethod("findElementById", File.class, String.class);
        method.setAccessible(true);

        Optional<Element> output = (Optional<Element>) method.invoke(xmlAnalyzerService, failFile, "Element_ID");

        assertFalse(output.isPresent());
        assertEquals(output, Optional.empty());
    }


    @Test
    public void findElementByIdTestSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = XmlAnalyzerService.class.getDeclaredMethod("findElementById", File.class, String.class);
        method.setAccessible(true);

        Optional<Element> output = (Optional<Element>) method.invoke(xmlAnalyzerService, successOriginFile, "make-everything-ok-button");

        assertTrue(output.isPresent());
        assertEquals(output.get().getClass(), Element.class);
    }

    @Test
    public void findElementsByQueryFail() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = XmlAnalyzerService.class.getDeclaredMethod("findElementsByQuery", File.class, String.class);
        method.setAccessible(true);

        Optional<Elements> output = (Optional<Elements>) method.invoke(xmlAnalyzerService, failFile, cssQuery);

        assertFalse(output.isPresent());
        assertEquals(output, Optional.empty());
    }

    @Test
    public void findElementsByQuerySucces() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = XmlAnalyzerService.class.getDeclaredMethod("findElementsByQuery", File.class, String.class);
        method.setAccessible(true);

        Optional<Elements> output = (Optional<Elements>) method.invoke(xmlAnalyzerService, successSampleFile, cssQuery);

        assertTrue(output.isPresent());
        assertEquals(output.get().getClass(), Elements.class);
    }

    @Test
    public void buildCSSQueryTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = XmlAnalyzerService.class.getDeclaredMethod("buildCSSQuery", Element.class, Attribute.class);
        method.setAccessible(true);

        String output = (String) method.invoke(xmlAnalyzerService, new Element("tag"), new Attribute("tes", "test"));

        assertEquals(String.class, output.getClass());

    }


    @Test
    public void createMapWithElementsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TreeMap<Integer, Element> elementMap = new TreeMap<>();

        Method method = XmlAnalyzerService.class.getDeclaredMethod("createMapWithElements", Map.class, List.class, Element.class);
        method.setAccessible(true);
        method.invoke(xmlAnalyzerService, elementMap, new Elements(sampleElement), originElement);

        assertEquals(new Integer(1), elementMap.lastKey());

    }
}