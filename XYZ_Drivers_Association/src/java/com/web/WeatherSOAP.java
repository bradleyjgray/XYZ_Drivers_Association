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

    static public void weather() {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "http://api.openweathermap.org/data/2.5/weather?q=BristolUK&mode=xml&APPID=1b973735a4f5af5f645e10cfcdf7c0d9";
            SOAPMessage soapResponse = soapConnection.call(createSOAP(), url);

            // Process the SOAP Response
            printSOAP(soapResponse);

            //Close the SOAP Connection
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Cannot Connect");
            e.printStackTrace();
        }
    }

    private static SOAPMessage createSOAP() throws Exception {
        MessageFactory message = MessageFactory.newInstance();
        SOAPMessage soapMessage = message.createMessage();
        return soapMessage;
    }

    private static void printSOAP(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        // System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(new File("c:\\weather.xml"));
        transformer.transform(sourceContent, result);
    }

    static public void XMLread() {
        try {

            File fXmlFile = new File("c:\\weather.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList cityList = doc.getElementsByTagName("city");
            NodeList cloudsList = doc.getElementsByTagName("clouds");

            //for (int temp1 = 0; temp1 < cityList.getLength(); temp1++) {
            Node nNode = cityList.item(0);
            Node clNode = cloudsList.item(0);

            //System.out.println("\nCurrent Element :" + cNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                city = (eElement.getAttribute("name"));
                //System.out.println("City d : " + eElement.getAttribute("id") + eElement.getAttribute("name"));
                //System.out.println(" : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                //System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                //System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
                //System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

                //}
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
