package com.myhexin.qparser.syntactic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.except.DataConfException;

public class SyntacticUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/* 
		Document doc =  readXMLFile("D:/MySoftwareBook/eclipse/workspace/QueryParser2_refactorplugin/data/stock/stock_phrase_syntactic.xml");
		 Element root = doc.getDocumentElement();
		 NodeList list = root.getChildNodes();
		 System.out.println("stock_phrase_syntactic size = " + list.getLength());
		 
		 Document doc1 =  readXMLFile("D:/MySoftwareBook/eclipse/workspace/QueryParser2_refactorplugin/data/stock/stock_phrase_semantic.xml");
		 Element root1 = doc1.getDocumentElement();
		 NodeList list1 = root1.getChildNodes();
		 System.out.println("stock_phrase_semantic size = " + list1.getLength());
		 */
		
		 Document doc =  readXMLFile("./src/test/java/com/myhexin/qparser/syntactic/stock_phrase_syntactic.xml");
		 getNodeList("stock_phrase_syntactic", doc, "SyntacticPattern");
		 
		 Document doc1 =  readXMLFile("./src/test/java/com/myhexin/qparser/syntactic/stock_phrase_semantic.xml");
		 getNodeList("stock_phrase_semantic", doc1, "SemanticPattern");
	}
	
	public static void getNodeList(String name, Document doc, String nodeName) {
		 Element root1 = doc.getDocumentElement();
		 NodeList list1 = root1.getChildNodes();
		 int count = 0;
		 for(int i=0;i<list1.getLength();i++) {
			 Node node = list1.item(i);
			 if( node.getNodeType() == Node.ELEMENT_NODE ) {
				 if(nodeName.equals(node.getNodeName()) ){
					 count++;
				 }
				
			 }
		 }
		 System.out.println(name + " size = " + list1.getLength());
		 System.out.println(nodeName + " size = " + count);
	}
	
	public static Document readXMLFile(String fileName) throws DataConfException {
        InputStream is = null;
        Document doc = null;
        try {
            DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder dombuilder;
            dombuilder = domfac.newDocumentBuilder();
            is = new FileInputStream(fileName);
            doc = dombuilder.parse(is);
        } catch (Exception e) {
            String errMsg = String.format("Exception while reading file: %s",fileName, e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    String errMsg = String.format("Exception while closing file [%s]: %s",fileName, e.getMessage());
                   e.printStackTrace();
                }
            }
        }
        return doc;
    }

}
