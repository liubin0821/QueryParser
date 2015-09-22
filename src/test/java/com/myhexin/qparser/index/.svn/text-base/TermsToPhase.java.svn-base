package com.myhexin.qparser.index;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class TermsToPhase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String syFile = "../temp/stock_term_onto_sy.xml";
        String syntacticFile = "../temp/syntactic_sy.xml";
        String semanticFile = "";
        
	}
	
	private static List<String> getTerms(String syfile) throws DataConfException
	{
		List<String> terms = new ArrayList<String>();
		Document source = Util.readXMLFile(syfile, true);
		Element root = source.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        for (int i = 0; i < infonodes.getLength(); i++) 
        {
        	Node node = infonodes.item(i);
        	if (!node.getNodeName().equals("class") || node.getAttributes() == null
        			|| node.getAttributes().getNamedItem("index_type") != null)
        	{
        		continue;
        	}
        	String idxtype = ((Attr)node.getAttributes().getNamedItem("index_type")).getValue();
        	if (idxtype != null && idxtype.trim().equalsIgnoreCase("TECH_SPECIAL")
        			&& node.getAttributes().getNamedItem("label") != null)
        	{
        		String lable = ((Attr)node.getAttributes().getNamedItem("label")).getValue();
        		if (lable != null && lable.trim().length() > 0)
        		{
        			terms.add(lable.trim());
        		}
        	}
        }
        return terms;
	}
	
	private static void formPhase(String term)
	{
		
	}
	
	private static void writeXML(Document doc, String file) 
			throws TransformerException, FileNotFoundException
	{
		Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty("indent", "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
    }

}
