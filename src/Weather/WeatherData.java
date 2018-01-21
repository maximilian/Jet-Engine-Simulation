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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.time.temporal.TemporalAccessor;
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
    private float fieldTemperature;
    private String fieldName;
    
    private LocalDateTime date;
            
    public void collectWeather() throws MalformedURLException, IOException, SAXException, ParserConfigurationException{
        String url = "https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=EGPF&hoursBeforeNow=1&mostRecent=True";   
        
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document doc = b.parse(urlConnection.getInputStream());
        System.out.println("plsss");
        //if (doc != null)
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("METAR");
        
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;
        Element cElement =  (Element) eElement.getElementsByTagName("altim_in_hg").item(0);
        Element tempElement =  (Element) eElement.getElementsByTagName("temp_c").item(0);
        Element stationElement =  (Element) eElement.getElementsByTagName("station_id").item(0);
        Element timeElement =  (Element) eElement.getElementsByTagName("observation_time").item(0);
        
        
        fieldPressure = Float.valueOf(cElement.getTextContent());
        fieldTemperature = Float.valueOf(tempElement.getTextContent());
        fieldName = stationElement.getTextContent();
        
        String fieldTime = timeElement.getTextContent();
        
        System.out.println("Station id is: "+fieldName);
        System.out.println("Pressure in hg: " + cElement.getTextContent());
        System.out.println("Temperature in c: " + tempElement.getTextContent());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneId.of("UTC"));
        date = LocalDateTime.parse(fieldTime, formatter);

    }
    
    public float getPressure() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (fieldPressure == 0.0f){
            collectWeather();
        }
        
        return fieldPressure;
    }
    
    public float getTemperature() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (fieldTemperature == 0.0f){
            collectWeather();
        }
        
        return fieldTemperature;
    
    }
    
    public String getFieldName() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (fieldName == null){
            collectWeather();
        }
        
        return fieldName;
    }
    
    public LocalDateTime getDateTime() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (date == null){
            collectWeather();
        }
        
        return date;
    }
}
