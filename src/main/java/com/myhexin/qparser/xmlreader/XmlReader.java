package com.myhexin.qparser.xmlreader;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlReader {
	/**
	 * 取符合条件的子节点列表
	 * 通用的xml处理函数
	 * @param parent 父节点
	 * @param tagName 子节点的名称
	 * @param seondTagName 第二级子节点名称
	 * @return 符合条件的节点列表
	 */
	public static ArrayList<Node> getChildElementsByTagName(Node parent,
			String tagName, String secondTagName) {
		ArrayList<Node> results = new ArrayList<Node>();

		NodeList childs = parent.getChildNodes();
		if (childs.getLength() == 0) {
			return results;
		}

		for (int i = 0; i < childs.getLength(); i++) {
			Node indexI = childs.item(i);
			// System.out.println("node name:"+indexI.getNodeName());
			if (indexI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!indexI.getNodeName().equals(tagName)) {
				continue;
			}

			return getChildElementsByTagName(indexI, secondTagName);
		}
		return results;
	}

	/**
	 * 取符合条件的子节点列表
	 * 通用的xml处理函数
	 * @param parent 父节点
	 * @param tagName 子节点的名称
	 * @return 符合条件的节点列表
	 */
	public static ArrayList<Node> getChildElementsByTagName(Node parent,
			String tagName) {
		ArrayList<Node> results = new ArrayList<Node>();

		NodeList childs = parent.getChildNodes();
		if (childs.getLength() == 0) {
			return results;
		}

		for (int i = 0; i < childs.getLength(); i++) {
			Node indexI = childs.item(i);
			if (indexI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!indexI.getNodeName().equals(tagName)) {
				continue;
			}

			results.add(indexI);
		}
		return results;
	}

	/**
	 * 获得节点的值
	 * @param element 节点
	 * @return 节点的值
	 */
	public static String getElementValue(Node element) {
		Node child = element.getFirstChild();
		if (child != null) {
			return child.getNodeValue();
		}
		return null;
	}

	/**
	 * 获得子节点的值
	 * @param parent 父节点
	 * @param tagName 子节点的名称
	 * @return 子节点的值
	 */
	public static String getChildElementValueByTagName(Node parent,
			String tagName) {
		// System.out.println("getChildElementValueByTagName:"+tagName+"\t"+parent.toString());
		Node node = getChildElementByTagName(parent, tagName);
		if (node != null) {
			Node child = node.getFirstChild();
			if (child != null) {
				return child.getNodeValue();
			}
		}
		return "";
	}

	/**
	 * 获得子节点
	 * @param parent 父节点
	 * @param tagName 子节点的名称
	 * @return 子节点
	 */
	public static Node getChildElementByTagName(Node parent, String tagName) {
		NodeList childs = parent.getChildNodes();
		if (childs.getLength() == 0) {
			return null;
		}

		for (int i = 0; i < childs.getLength(); i++) {
			Node indexI = childs.item(i);
			if (indexI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!indexI.getNodeName().equals(tagName)) {
				continue;
			}

			return indexI;
		}
		return null;
	}

	/**
	 * 获得节点属性值
	 * @param node 节点
	 * @param attributeName 属性名称
	 * @return 属性值
	 */
	public static String getAttributeByName(Node node, String attributeName) {
		// System.out.println(node.toString()+" @"+attributeName);
		Node attr = node.getAttributes().getNamedItem(attributeName);
		return attr != null ? attr.getNodeValue() : null;
	}
}
