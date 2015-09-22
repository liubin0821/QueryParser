package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import com.myhexin.qparser.xmlreader.XmlReader;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PhraseXmlReaderKeywordGroup {
	/**
	 * parse config file to memory
	 * 
	 * @param doc XMLDom 文档
	 * @param keywordGroupMap 将parse出来的内容存储在这里
	 * @return void
	 */
	public static KeywordGroupMap loadKeywordGroupMap(Document doc) {
		// System.out.println("loadKeywordGroupMap:");
		KeywordGroupMap keywordGroupMap = new KeywordGroupMap();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "KeywordGroup");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null)
				continue;
			KeywordGroup keywordGroup = keywordGroupMap.getKeywordGroup(id);
			loadKeywordGroup(node, keywordGroup);
		}
		return keywordGroupMap;
	}

	private static void loadKeywordGroup(Node node, KeywordGroup keywordGroup) {
		String description = XmlReader.getChildElementValueByTagName(node, "Description");
		keywordGroup.setDescription(description);
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "Keyword");
		for (int i = 0; i < nodes.size(); i++) {
			Node keywordNode = nodes.get(i);
			String keyword = XmlReader.getElementValue(keywordNode);
			keywordGroup.addKeyword(keyword);
		}
	}
}
