package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import com.myhexin.qparser.xmlreader.XmlReader;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PhraseXmlReaderIndexGroup {
	/**
	 * parse config file to memory
	 * 
	 * @param doc XMLDom 文档
	 * @param indexGroupMap 将parse出来的内容存储在这里
	 * @return void
	 */
	public static IndexGroupMap loadIndexGroupMap(Document doc) {
		// System.out.println("loadIndexGroupMap:");
		IndexGroupMap indexGroupMap = new IndexGroupMap();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "IndexGroup");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null)
				continue;
			IndexGroup indexGroup = indexGroupMap.getIndexGroup(id);
			loadIndexGroup(node, indexGroup);
		}
		return indexGroupMap;
	}
	
	private static void loadIndexGroup(Node node, IndexGroup indexGroup) {
		String description = XmlReader.getChildElementValueByTagName(node, "Description");
		indexGroup.setDescription(description);
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "Index");
		for (int i = 0; i < nodes.size(); i++) {
			Node indexNode = nodes.get(i);
			String index = XmlReader.getElementValue(indexNode);
			indexGroup.addIndex(index);
		}
	}
}
