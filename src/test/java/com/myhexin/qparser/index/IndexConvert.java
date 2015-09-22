package com.myhexin.qparser.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class IndexConvert {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        if (args.length < 3)
        {
        	System.err.println("usage: sourceindexfile aliasfile targetindexfile");
        	return;
        }
        String source = args[0];
        String alias = args[1];
        String target = args[2];
        try {
			convertIndex(source, alias, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Map<String, List<String>> getAliasMap(String aliasfile)
	    throws IOException
	{
		Map<String, List<String>> aliasMap = new HashMap<String, List<String>>();
		FileReader fReader = new FileReader(aliasfile);
		BufferedReader bReader = new BufferedReader(fReader);
		String line = null;
		while ((line = bReader.readLine()) != null)
		{
			line = line.trim();
			if (line.length() == 0)
			{
				continue;
			}
			String[] indexAlias = line.split(":");
			if (indexAlias.length <= 1)
			{
				continue;
			}
			String index = indexAlias[0].trim();
			String aliastr = indexAlias[1].trim();
			if (index.length() == 0)
			{
				continue;
			}
			int start = aliastr.indexOf("[");
			int end = aliastr.indexOf("]");
			aliastr = aliastr.substring(start+1, end).trim();
			if (aliastr.length() == 0)
			{
				continue;
			}
			List<String> arrAliases = new ArrayList<String>();
			String[] aliases = aliastr.split(",");
			for (String alias : aliases)
			{
				arrAliases.add(alias.trim());
			}
			aliasMap.put(index, arrAliases);
		}
		bReader.close();
		fReader.close();
		return aliasMap;
	}
	
	private static void writeXML(Document doc, String file) 
			throws TransformerException, FileNotFoundException
	{
		Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty("indent", "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
    }
	
	private static Document updateDocument(String sourceidxfile, 
			Map<String, List<String>> aliasMap) throws DataConfException
	{
		Document source = Util.readXMLFile(sourceidxfile, true);
		Element root = source.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        if (infonodes.getLength() == 0) {
            throw new DataConfException(sourceidxfile, -1, "未定义任何内容");
        }
        Text spaceText = source.createTextNode("\n\t\t");
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node xmlClass = infonodes.item(i);
            if (xmlClass.getNodeType() != Node.ELEMENT_NODE
            		|| !xmlClass.getNodeName().equals("class")) 
            {
                continue;
            }
            Element elemClass = (Element)xmlClass;
            NamedNodeMap nnm = elemClass.getAttributes();
            String label = nnm.getNamedItem("label").getNodeValue();
            Node dataSrcNode = nnm.getNamedItem("data_src");
            Node indexTypeNode = nnm.getNamedItem("index_type");
            if (dataSrcNode != null)
            {
            	nnm.removeNamedItem("data_src");
            }
            if (indexTypeNode != null)
            {
            	nnm.removeNamedItem("index_type");
            }
            Node supNode = null;
            Node subNode = null;
            Node firstPropNode = null;
            
            Element descElement = source.createElement("description");
            String desc = "";
            Text descText = source.createTextNode(desc);
            descElement.appendChild(descText);
            
            Element aliasElement = source.createElement("alias");
            List<String> aliases = aliasMap.get(label);
            StringBuffer buffer = new StringBuffer();
            if (aliases != null)
            {
            	for (int j = 0; j < aliases.size(); j ++)
                {
                	if (buffer.length() > 0)
                	{
                		buffer.append(",");
                	}
                	buffer.append(aliases.get(j));
                }
            }       
            Text aliasText = source.createTextNode(buffer.toString());
            aliasElement.appendChild(aliasText);
            
            NodeList children = xmlClass.getChildNodes();
            for (int k = 0; k < children.getLength(); k ++)
            {
            	Node temp = children.item(k);
            	if (temp.getNodeName().equalsIgnoreCase("subclass"))
            	{
            		subNode = temp;
            	}
            	else if (temp.getNodeName().equalsIgnoreCase("superclass"))
            	{
            		supNode = temp;
            	}
            	else if (firstPropNode == null 
            			&& temp.getNodeName().equalsIgnoreCase("prop"))
            	{
            		firstPropNode = temp;
            	}
            	if (subNode != null && supNode != null && firstPropNode != null)
            	{
            		break;
            	}
            }
            if (subNode != null && supNode != null)
            {
            	xmlClass.replaceChild(descElement, subNode);
            	xmlClass.replaceChild(aliasElement, supNode);
            }
            else if (subNode != null)
            {
            	xmlClass.replaceChild(aliasElement, subNode);
            	Text cloneText = (Text)spaceText.cloneNode(false);
            	xmlClass.appendChild(cloneText);
            	xmlClass.insertBefore(descElement, aliasElement);
            }
            else if (supNode != null)
            {
            	xmlClass.replaceChild(aliasElement, supNode);
            	Text cloneText = (Text)spaceText.cloneNode(false);
            	xmlClass.appendChild(cloneText);
            	xmlClass.insertBefore(descElement, aliasElement);
            }
            else {
            	if (firstPropNode != null)
        		{
            		Text cloneText = (Text)spaceText.cloneNode(false);
                	xmlClass.insertBefore(cloneText, firstPropNode);
        			xmlClass.insertBefore(aliasElement, cloneText);
        		}
        		else 
        		{
        			xmlClass.appendChild(aliasElement);
        		}
            	Text cloneText1 = (Text)spaceText.cloneNode(false);
            	xmlClass.insertBefore(cloneText1, aliasElement);
            	xmlClass.insertBefore(descElement, cloneText1);
			}
 /*           if (subNode != null)
            {
            	xmlClass.removeChild(subNode);           	
            }
            if (supNode != null)
            {
            	xmlClass.removeChild(supNode);
            }
    		if (firstPropNode != null)
    		{
    			xmlClass.insertBefore(aliasElement, firstPropNode);
    		}
    		else 
    		{
    			xmlClass.appendChild(aliasElement);
    		}
    		xmlClass.insertBefore(descElement, aliasElement);*/
        }
        return source;
	}
	
	public static void convertIndex(String sourceidxfile, String aliasfile, 
			String targetidxfile) 
			throws DataConfException, IOException, TransformerException
	{		
		Map<String, List<String>> aliasMap = getAliasMap(aliasfile);	
		Document newDoc = updateDocument(sourceidxfile, aliasMap);
		writeXML(newDoc, targetidxfile);
	}
}
