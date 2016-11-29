
package com.web;

import java.io.File;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 *
 * @author Marco Moreira
 */
public class WeatherSOAP {
    
    static public void weather(){
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
    
    
    
}
