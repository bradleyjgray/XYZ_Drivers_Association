package com.web;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 *
 * @author Marco Moreira
 */
public class WeatherSOAP {

    public static String city;
    public static String cloud;
    public static String temper;
    public static double temperDouble;
    public static int celsius;

    static public void weather() {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapCF = SOAPConnectionFactory.newInstance();
            SOAPConnection soapC = soapCF.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "http://api.openweathermap.org/data/2.5/weather?q=BristolUK&mode=xml&APPID=1b973735a4f5af5f645e10cfcdf7c0d9";
            SOAPMessage soapR = soapC.call(createSOAP(), url);

            // Process the SOAP Response
            printSOAP(soapR);

            //Close the SOAP Connection
            soapC.close();
        } catch (Exception e) {
            System.err.println("Cannot Connect");
            e.printStackTrace();
        }
    }

    private static SOAPMessage createSOAP() throws Exception {
        MessageFactory m = MessageFactory.newInstance();
        SOAPMessage soapM = m.createMessage();
        return soapM;
    }

    private static void printSOAP(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerF = TransformerFactory.newInstance();
        Transformer transformer = transformerF.newTransformer();
        Source sourceC = soapResponse.getSOAPPart().getContent();
        StreamResult result = new StreamResult(new File("weather.xml"));
        transformer.transform(sourceC, result);
    }

    static public void XMLread() {
        try {

            File fXmlFile = new File("weather.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList cityList = doc.getElementsByTagName("city");
            NodeList tempList = doc.getElementsByTagName("temperature");
            NodeList cloudsList = doc.getElementsByTagName("clouds");

            Node nNode = cityList.item(0);
            Node tNode = tempList.item(0);
            Node clNode = cloudsList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                city = (eElement.getAttribute("name"));
            }
            
            if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                Element tElement = (Element) tNode;
                temper = (tElement.getAttribute("value"));
                temperDouble = ((Double.parseDouble(temper)) - 273.15);
                celsius = (int) Math.round(temperDouble);
            }
            
            if (clNode.getNodeType() == Node.ELEMENT_NODE) {
                Element cElement = (Element) clNode;
                cloud = (cElement.getAttribute("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
