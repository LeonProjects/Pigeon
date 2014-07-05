package com.leonproject.pigeon.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyReader {
	
	Properties prop;
	InputStream inputStream;
	
	public PropertyReader() throws FileNotFoundException{
	    prop = new Properties();
        String propFileName = "config.properties";
 
        inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
			prop.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
	}
	
	public String getConfigValue(String propName){
		
		return prop.getProperty(propName);
	}

}
