package com.myhexin.qparser.util.lightparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.myhexin.qparser.xmlreader.XmlReader;

/**
 * parse config file to memory
 * 
 * @param doc XMLDom 文档
 * @param lightParserTypeMap 将parse出来的内容存储在这里
 * @return void
 */
public class LightParserTypeXMLReader {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LightParserTypeXMLReader.class.getName());
	
	public static List<String> loadLightParserTypes(Document doc) {
		List<String> lightParserTypes = new ArrayList<String>();
		Element root = doc.getDocumentElement();
		Node node = XmlReader.getChildElementByTagName(root, "Types");
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "Type");
		for (int i = 0; i < nodes.size(); i++) {
			Node typeNode = nodes.get(i);
			String type = XmlReader.getElementValue(typeNode);
			if (type != null && !type.equals(""))
				lightParserTypes.add(type);
		}
		return lightParserTypes;
	}
	
	public static HashMap<String, LightParserType> loadLightParserTypeMap(Document doc) {
		HashMap<String, LightParserType> lightParserTypeMap = new HashMap<String, LightParserType>();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "Type");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String label = XmlReader.getAttributeByName(node, "label");
			if (label == null || label.equals("")) {
				logger_.error("lightparser_type.xml: Type no label");
				continue;
			}
			LightParserType lightParserType = new LightParserType(label);
			loadLightParserType(node, lightParserType);
			lightParserTypeMap.put(label, lightParserType);
		}
		return lightParserTypeMap;
	}

	private static void loadLightParserType(Node type,
			LightParserType lightParserType) {
		lightParserType.setDesc(XmlReader.getChildElementValueByTagName(type, "Description"));
		ArrayList<Node> subTypes = XmlReader.getChildElementsByTagName(type, "SubType");
		for (int i = 0; i < subTypes.size(); i++) {
			Node node = subTypes.get(i);
			String label = XmlReader.getAttributeByName(node, "label");
			String value = XmlReader.getElementValue(node);
			if (label == null || label.equals("")) {
				logger_.error("lightparser_type.xml: Type SubType no label");
				continue;
			}
			SubType subType = new SubType(label, value);
			lightParserType.addSubType(subType);
		}
		
		ArrayList<Node> channels = XmlReader.getChildElementsByTagName(type, "Channel");
		for (int i = 0; i < channels.size(); i++) {
			Node node = channels.get(i);
			String label = XmlReader.getAttributeByName(node, "label");
			String value = XmlReader.getElementValue(node);
			if (label == null || label.equals("")) {
				logger_.error("lightparser_type.xml: Type Channel no label");
				continue;
			}
			Channel channel = new Channel(label, value);
			lightParserType.addChannel(channel);
		}
	}
}
