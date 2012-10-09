/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * Parser for the rats tool output.
 * 
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 * 
 */
public class RatsParser extends DefaultHandler
{
    
    private FindingCreator findingCreator;
    
    private String id;
    
    private String type;
    
    private String message;
    
    private File file;
    
    private Integer line;
    
    private StringBuffer tmpValue = new StringBuffer();
    
    /**
     * create the new parser
     */
    public RatsParser(Properties props, File file, String name)
    {
        findingCreator = new FindingCreator(props, name);
        this.file = file;
    }
    
    /**
     * called for the starting element.
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    {
        tmpValue.setLength(0);
    }
    
    /**
     * 
     */
    public void characters(char[] ch, int start, int length)
    {
        tmpValue.append(ch, start, length);
    }
    
    /**
     * Tries to match the weakness to an id in the idConfig file. if there is
     * not weakness description, then we fall back on the type.
     */
    public void endElement(String uri, String localName, String qName)
    {
        if ("type".equals(qName))
        {
            type = tmpValue.toString().trim();
            type = type.replace(" ", "");
        }
        
        if ("message".equals(qName))
        {
            message = tmpValue.toString().trim();
            message = message.replace("\n", " ");
            message = message.replaceAll("\\s{2,}", " ");
            id = deriveId(message);
        }
        
        if ("name".equals(qName))
        {
            // file = tmpValue.toString().trim();
        }
        
        if ("line".equals(qName))
        {
            line = Integer.parseInt(tmpValue.toString().trim());
            
            if (id == null || id.isEmpty())
            {
                id = type;
            }
            
            findingCreator.create(message, id, line, null, null, file, null, null);
        }
    }
    
    public ArrayList<Element> getElements()
    {
        return findingCreator.getElements();
    }
    
    /**
     * derive the id from the description and the id config file.
     * 
     * @param description
     * @return
     */
    private String deriveId(String description)
    {
        try
        {
            Scanner scan = new Scanner(getClass().getResourceAsStream("/config/RatsAdaptorIdConfig"));
            
            String line = null;
            while (scan.hasNextLine())
            {
                
                line = scan.nextLine();
                String[] lineParts = line.split(";");
                if (line.startsWith("#"))
                {
                    continue;
                }
                if (lineParts.length != 2)
                {
                    continue;
                }
                if (line.isEmpty())
                {
                    continue;
                }
                
                String pattern = lineParts[0];
                String id = lineParts[1];
                
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(description);
                
                if (m.find())
                {
                    return id;
                    
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Could not access the idConfig file." + e);
            System.exit(1);
        }
        return null;
    }
    
}
