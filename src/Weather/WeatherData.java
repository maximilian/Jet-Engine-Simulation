package Weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Object for the system's weather
 * 
 * @author max
 */
public class WeatherData {
    private String fieldIdentifier;
    
    private float fieldPressure;
    private float fieldTemperature;
    private String fieldName;
    
    private LocalDateTime date;
        
    /**
     * Constructor for live weather
     * 
     * @param fieldIdentifier airport ICAO code
     */
    public WeatherData(String fieldIdentifier){
        this.fieldIdentifier = fieldIdentifier;
    }
    
    /**
     * Constructor for ISA or custom weather
     * 
     * @param fieldIdentifier airport ICAO code
     * @param temp specified temperature, Celsius
     * @param pressure specified pressure, Pascals
     */
    public WeatherData(String fieldIdentifier, float temp, float pressure){
        this.fieldIdentifier = fieldIdentifier;
        
        this.fieldName = fieldIdentifier;
        
        this.fieldPressure = pressure;
        this.fieldTemperature = temp;
       
    }

    /**
     * Produces an API call to AviationWeather in order
     * to get a decoded version of the specified METAR report
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public void collectWeather() throws MalformedURLException, IOException, SAXException, ParserConfigurationException{
        String url = "https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=" + fieldIdentifier + "&hoursBeforeNow=4&mostRecent=True";   
        System.out.println("downloading weather for"+fieldIdentifier);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document doc = b.parse(urlConnection.getInputStream());

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
    
    /**
     * Get the airport pressure, Pascals
     * 
     * @return fieldPressure airport pressure, Pascals
     * @throws IOException
     * @throws MalformedURLException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public float getPressure() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{

        if (fieldPressure == 0.0f){
            collectWeather();
        }
        
        return fieldPressure;
    }
    
    /**
     * Get the airport temperature, Celsius
     * 
     * @return fieldTemperature temperature at specified airport, Celsius
     * @throws IOException
     * @throws MalformedURLException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    
    public float getTemperature() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        
        if (fieldTemperature == 0.0f){
            collectWeather();
        }
        
        return fieldTemperature;
    
    }
    
    /**
     * Get airport identifier
     * 
     * @return fieldName airport ICAO code
     * 
     * @throws IOException
     * @throws MalformedURLException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public String getFieldName() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (fieldName == null){
            collectWeather();
        }
        
        return fieldName;
    }
    
    /**
     * Get the date and time the METAR report was issued
     * 
     * @return date Date and time of issue
     * 
     * @throws IOException
     * @throws MalformedURLException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public LocalDateTime getDateTime() throws IOException, MalformedURLException, SAXException, ParserConfigurationException{
        if (date == null){
            collectWeather();
        }
        
        return date;
    }
    
    /**
     * Set the airport identifier
     * 
     * @param ident airport ICAO identifier
     */
    public void setIdent(String ident){
        this.fieldIdentifier = ident;
    }
    
    
}
