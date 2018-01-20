/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Collects weather data from AviationWeather's API
 * 
 * @author max
 */
public class WeatherData {
    
    private float fieldPressure;
    
    public void collectWeather() throws MalformedURLException, IOException, SAXException, ParserConfigurationException{
        String url = "https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=EGPF&hoursBeforeNow=1&mostRecent=True&fields=issue_time";   

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document doc = b.parse(urlConnection.getInputStream());
        
        //if (doc != null)
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("METAR");
        
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;
        Element cElement =  (Element) eElement.getElementsByTagName("altim_in_hg").item(0);
        
        fieldPressure = Float.valueOf(cElement.getTextContent());
        
        System.out.println("Pressure in hg: " + cElement.getTextContent());

    }
    
    public float getPressure(){
        return this.fieldPressure;
    }
}
