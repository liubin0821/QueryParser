package com.myhexin.qparser.index;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.util.Util;

public class AmbigutyConvert {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String indexNewFile = "temp/stock_onto_ambiguty.xml";
		String indexFile = "temp/stock_onto.xml";
		
		// PhraseParser parser = new PhraseParser("./conf/qparser.conf", "./data");

		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");		adjustAliasAmbiguties();
		
		Document newDoc = updateDocument(indexFile);
		try {
			writeXML(newDoc, indexNewFile);
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
	
	private static Document updateDocument(String sourceidxfile)
	{
		Document source = null;
		try {
			source = Util.readXMLFile(sourceidxfile, true);
		} catch (DataConfException e) {
			e.printStackTrace();
		}
		Element root = source.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        Text spaceText = source.createTextNode("\n\t\t");
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node xmlClass = infonodes.item(i);
            if (xmlClass.getNodeType() != Node.ELEMENT_NODE
            		|| !xmlClass.getNodeName().equals("class")) 
            {
                continue;
            }
            NamedNodeMap nnm = xmlClass.getAttributes();
            String label = nnm.getNamedItem("label").getNodeValue();
            ClassNodeFacade cNode = null;
            try {
				cNode = MemOnto.getOnto(label, ClassNodeFacade.class, Query.Type.STOCK);
			} catch (UnexpectedException e) {
				cNode = null;
			}
            if (cNode == null)
            {
            	continue;
            }
            NodeList nList = xmlClass.getChildNodes();
            Node firstPropNode = null;
            Node prevProp = null;
            for (int j = 0; j < nList.getLength(); j ++)
            {
            	Node subNode = nList.item(j);
            	if (subNode.getNodeName().equals("aliases"))
            	{
            		xmlClass.removeChild(subNode);
            	}
            	if (subNode.getNodeName().equals("ambiguities"))
            	{
            		xmlClass.removeChild(subNode);
            	}
            	if (subNode.getNodeType() == Node.TEXT_NODE && subNode.getTextContent().indexOf("\n") != -1)
            	{
            		prevProp = subNode;
            	}
            	if (firstPropNode == null 
            			&& subNode.getNodeName().equalsIgnoreCase("prop"))
            	{
            		xmlClass.removeChild(prevProp);
            		firstPropNode = subNode;
            	}
            }
            Element aliases = source.createElement("aliases");
            Text cloneText = (Text)spaceText.cloneNode(false);
        	xmlClass.insertBefore(cloneText, firstPropNode);
			xmlClass.insertBefore(aliases, cloneText);
			if (cNode.getAlias() != null)
			{
				for (String strin : cNode.getAlias())
	            {
	            	Element element = source.createElement("alias");
	            	element.setAttribute("title", strin);
	            	Text space1 = source.createTextNode("\n\t\t\t\t");
	    			aliases.appendChild(space1);
	            	aliases.appendChild(element);
	            }
			}

			Text space2 = source.createTextNode("\n\t\t");
			aliases.appendChild(space2);
            
        }
        return source;
	}
	
	private static void adjustAliasAmbiguties()
    {
		Map<String, SemanticNode> memOnto = MemOnto.stkOnto_;
    	for (Entry<String, SemanticNode> entry : memOnto.entrySet())
    	{
    		if (entry.getValue().type != NodeType.CLASS)
    		{
    			continue;
    		}
    		ClassNodeFacade cNode = (ClassNodeFacade)entry.getValue();
    	}
    }

}
