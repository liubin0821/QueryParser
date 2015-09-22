package com.myhexin.qparser.index;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class AmbiguityIndexAdd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String ruleFile = "index-data/stock_index_rule.xml";
        String indexFile = "index-data/stock_onto_temp.xml";
        String indexNewFile = "index-data/stock_onto_ambiguty.xml";
		try {
			Map<String, Map<String, List<String>>> rules = loadRules(ruleFile);
			System.out.println(rules.get("净利润").size());
			Document newDoc = updateDocument(indexFile, rules);
			writeXML(newDoc, indexNewFile);
		} catch (DataConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeXML(Document doc, String file) 
			throws TransformerException, FileNotFoundException
	{
		Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty("indent", "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
    }
	
	private static Document updateDocument(String sourceidxfile, 
			Map<String, Map<String, List<String>>> rulesMap) throws DataConfException
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
            Map<String, List<String>> rules = rulesMap.get(label);
            if (rules != null && rules.size() > 0)
            {
            	Element ambiguities = source.createElement("ambiguities");
            	NodeList children = xmlClass.getChildNodes();
            	Node firstPropNode = null;
                for (int k = 0; k < children.getLength(); k ++)
                {
                	Node temp = children.item(k);               	
                	if (firstPropNode == null 
                			&& temp.getNodeName().equalsIgnoreCase("prop"))
                	{
                		firstPropNode = temp;
                	}
                	if (firstPropNode != null)
                	{
                		break;
                	}
                }
                Text cloneText = (Text)spaceText.cloneNode(false);
            	xmlClass.insertBefore(cloneText, firstPropNode);
    			xmlClass.insertBefore(ambiguities, cloneText);
    			updateAmbiguNode(ambiguities, source, rules);
            }                  
        }
        return source;
	}
	
	private static void updateAmbiguNode(Element ambiguities, 
			Document doc, Map<String, List<String>> rules)
	{
		for (Entry<String, List<String>> entry : rules.entrySet())
		{
			Element ambiguty = doc.createElement("ambiguty");
			Text value = doc.createTextNode(entry.getKey());
			ambiguty.appendChild(value);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < entry.getValue().size(); i ++)
			{
				if (buffer.length() > 0)
				{
					buffer.append("|");
				}
				buffer.append(entry.getValue().get(i));
			}
			ambiguty.setAttribute("condition", buffer.toString());
			Text space1 = doc.createTextNode("\n\t\t\t\t");
			ambiguities.appendChild(space1);
			ambiguities.appendChild(ambiguty);
		}
		Text space2 = doc.createTextNode("\n\t\t");
		ambiguities.appendChild(space2);
	}
	
	public static Map<String, Map<String, List<String>>> loadRules(String ruleFile) 
			throws DataConfException
	{
		Map<String, Map<String, List<String>>> ambiguityMap 
		    = new HashMap<String, Map<String,List<String>>>();
		
		Document doc = Util.readXMLFile(ruleFile, true);
		Element root = doc.getDocumentElement();
		NodeList ruleItems = root.getChildNodes();
		for (int rdx = 0; rdx < ruleItems.getLength(); rdx++) {
			Node ruleItem = ruleItems.item(rdx);
			if (!ruleItem.getNodeName().equals("index-rule"))
			{
				continue;
			}
			NodeList ruleInfos = ruleItem.getChildNodes();
			String fromIndex = null;
			HashMap<String, List<String>> changeMap = new HashMap<String, List<String>>();
			for (int idx = 0; idx < ruleInfos.getLength(); idx++) {
				Node ruleInfo = ruleInfos.item(idx);
				String name = ruleInfo.getNodeName();
				if (name.equals("index-from")) {
					fromIndex = ruleInfo.getFirstChild().getNodeValue().trim();
					if (fromIndex != null && ambiguityMap.get(fromIndex) != null)
					{
						changeMap = (HashMap<String, List<String>>)ambiguityMap.get(fromIndex);
					}
				} else if (name.equals("index-change")) {
					NodeList pairList = ruleInfo.getChildNodes();
					String toIndex = null;
					String codition = null;
					for (int pdx = 0; pdx < pairList.getLength(); pdx++) {
						Node tmp = pairList.item(pdx);
						String tmpName = tmp.getNodeName();
						if (tmpName.equals("index-condition")) {
							codition = tmp.getFirstChild().getNodeValue().trim();
						} else if (tmpName.equals("index-to")) {
							toIndex = tmp.getFirstChild().getNodeValue().trim();
						}
					}
					if (changeMap.containsKey(toIndex))
					{
						List<String> conditions = changeMap.get(toIndex);
						boolean bexist = false;
						for (int i = 0; i < conditions.size(); i ++)
						{
							if (conditions.get(i).equals(codition))
							{
								bexist = true;
								break;
							}
						}
						if (!bexist)
						{
							conditions.add(codition);
							changeMap.put(toIndex, conditions);
						}
					}
					else {
						List<String> conditions = new ArrayList<String>();
						conditions.add(codition);
						changeMap.put(toIndex, conditions);
					}					
				}
			}
			if(fromIndex == null){
				continue ;
			}
			ambiguityMap.put(fromIndex, changeMap);

		}
		return ambiguityMap;
		
	}

}
