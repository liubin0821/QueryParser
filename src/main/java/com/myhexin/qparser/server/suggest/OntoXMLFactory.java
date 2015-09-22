package com.myhexin.qparser.server.suggest;



import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OntoXMLFactory {
	// XML document
	public Document xmlDocument_;
	private String xmlDocFileName_;
	private XPathFactory xFactory_;
	private XPath xpath_;
	private XPathExpression expr_;
	public Object result_;
    public static double minNum = -100000000000000.00;
    public static double maxNum = 10000000000000.00;
    
	public OntoXMLFactory(String xmlDocFileName) {
		this.xmlDocFileName_ = xmlDocFileName;
		this.init();
	}

	public OntoXMLFactory(String aimXMLFileName, OntoXMLFactory sourceXMLFactory) {
		xmlDocFileName_ = aimXMLFileName;

	}

	public boolean init() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			xmlDocument_ = docBuilder.parse(xmlDocFileName_);
			xFactory_ = XPathFactory.newInstance();
			xpath_ = xFactory_.newXPath();
			expr_ = null;
			result_ = null;
			return true;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return false;
		}
	}

	public Object exectXPathStr(String aXPathStr) {
		try {
			expr_ = xpath_.compile(aXPathStr);
			result_ = expr_.evaluate(xmlDocument_, XPathConstants.NODESET);
			return result_;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return false;
		}
	}

	public Element createNewElement( String elementName ){
		return xmlDocument_.createElement(elementName);
	}
	
	public boolean writeXMLFile(String outFileName) {
		boolean flag = true;
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			// transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
			DOMSource source = new DOMSource(xmlDocument_);
			StreamResult result = new StreamResult(new File(outFileName));
			transformer.transform(source, result);
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}

	public boolean readConfigFile() {
		try {
			return true;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return false;
		}
	}
}
