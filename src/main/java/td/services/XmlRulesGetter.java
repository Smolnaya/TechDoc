package td.services;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlRulesGetter {
    public Map<String, Map<String, String>> getDocumentRules(Path xsdPath) {
        try {
            XmlObject xsdDocument = XmlObject.Factory.parse(xsdPath.toFile());

            //Get DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(new ByteArrayInputStream(xsdDocument.toString().getBytes()));

            //Get XPath
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();

            //ruleMap - map of document rules
            String allRules = (String) xpath.evaluate(".//element[@name='document']/annotation/appinfo/text()", xml, XPathConstants.STRING);
            Map<String, Map<String, String>> ruleMap = new HashMap<>();
            String[] subStr = allRules.split(";");
            for (int i = 0; i < subStr.length; i++) {
                String[] subKindRules = subStr[i].trim().split("rules: ");
                String ruleType = "";
                for (int j = 0; j < subKindRules.length; j++) {
                    if (j == 0) {
                        ruleType = subKindRules[j].trim();
                    }
                    if (j == 1) {
                        Map<String, String> currentMap = new HashMap<>();
                        String[] subRules = subKindRules[j].split(", ");
                        for (int k = 0; k < subRules.length; k++) {
                            String[] rule = subRules[k].split("=");
                            currentMap.put(rule[0], rule[1]);
                        }
                        ruleMap.put(ruleType, currentMap);
                    }
                }
            }

            return ruleMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public List<String> getSectionKind(Path xsdPath) {
        try {
            XmlObject xsdDocument = XmlObject.Factory.parse(xsdPath.toFile());

            //Get DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(new ByteArrayInputStream(xsdDocument.toString().getBytes()));

            //Get XPath
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();

            List<String> sectionKindList = new ArrayList<>();
            NodeList nodes = (NodeList) xpath.evaluate(".//element[@ref='section']/@kind", xml, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                sectionKindList.add(nodes.item(i).getNodeValue());
            }

            return sectionKindList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
