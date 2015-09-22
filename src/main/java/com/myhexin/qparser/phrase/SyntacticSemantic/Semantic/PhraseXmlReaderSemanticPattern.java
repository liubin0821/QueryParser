package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.xmlreader.XmlReader;

public class PhraseXmlReaderSemanticPattern {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseXmlReaderSemanticPattern.class.getName());
	/**
	 * parse config file to memory
	 * 
	 * @param doc XMLDom 文档
	 * @param semanticPatternMap 将parse出来的内容存储在这里
	 * @return void
	 */
	public static SemanticPatternMap loadSemanticPatternMap(Document doc) {
		// System.out.println("loadSemanticPatternMap");
		SemanticPatternMap semanticPatternMap = new SemanticPatternMap();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "SemanticPattern");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null || id.equals("")) {
				logger_.error("stock_phrase_semantic.xml: SemanticPattern no id");
				continue;
			}
			SemanticPattern semanticPattern = semanticPatternMap.getSemanticPattern(id, true);
			loadSemanticPattern(node, semanticPattern);
		}
		return semanticPatternMap;
	}

	private static void loadArgument(Node node, SemanticArgument argument) {
		// System.out.println("loadArgument"+node.toString());
		argument.setType(XmlReader.getAttributeByName(node, "type"));
		argument.setSpecificIndex(XmlReader.getAttributeByName(node, "SpecificIndex"));
		argument.setSpecificIndexGroup(XmlReader.getAttributeByName(node, "SpecificIndexGroup"));
		argument.setSuperClass(XmlReader.getAttributeByName(node, "SuperClass"));
		argument.setListElementMinCount(XmlReader.getAttributeByName(node, "ListElementMinCount"));
		argument.setValueType(XmlReader.getAttributeByName(node, "ValueType"));
		argument.setDefaultIndex(XmlReader.getAttributeByName(node, "DefaultIndex"));
		argument.setDefaultValue(XmlReader.getAttributeByName(node, "DefaultValue"));
	}
	
	private static void loadSemanticPattern(Node node,
			SemanticPattern semanticPattern) {
		semanticPattern.setKeyValue(XmlReader.getChildElementValueByTagName(node, "IsKeyValue"));
		semanticPattern.setUiRepresentation(XmlReader.getChildElementValueByTagName(node, "UIRepresentation"));
		semanticPattern.setChineseRepresentation(XmlReader.getChildElementValueByTagName(node, "ChineseRepresentation"));
		semanticPattern.setDescription(XmlReader.getChildElementValueByTagName(node, "Description"));

		loadArguments(node, semanticPattern.getSemanticArguments(), "Argument");
		loadArgumentDependency(
				XmlReader.getChildElementByTagName(node, "ArgumentDependency"),
				semanticPattern.getSemanticArgumentDependency());
		loadResult(node, semanticPattern.getResult(), "result");
		loadProps(node, semanticPattern.getPropsClassNode(), "prop");
	}

	private static void loadArgumentDependency(Node node,
			SemanticArgumentDependency argumentDependency) {
		if (node == null)
			return;
		loadArguments(node, argumentDependency.getIndependentArguments(), "IndependentArgument");
		loadArguments(node, argumentDependency.getDependentArguments(), "DependentArgument");
	}

	private static void loadArguments(Node node, SemanticArguments arguments,
			String tagname) {
		String semanticPatternId = XmlReader.getAttributeByName(node, "id"); // 语义编号
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, tagname);
		for (int i = 0; i < nodes.size(); i++) {
			Node child = nodes.get(i);
			String id = XmlReader.getAttributeByName(child, "id");
			if (id == null || id.equals("")) {
				logger_.error("stock_phrase_semantic.xml-" + semanticPatternId + ": SemanticArgument no id");
				continue;
			}
			SemanticArgument argument = arguments.getSemanticArgument(id, true);
			if (argument == null) {
				logger_.error("stock_phrase_semantic.xml-" + semanticPatternId + ": SemanticArgument id not in order");
				continue;
			}
			loadArgument(child, argument);
		}
	}
	
	private static void loadResult(Node node, SemanticArgument result,
			String tagname) {
		String semanticPatternId = XmlReader.getAttributeByName(node, "id"); // 语义编号
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, tagname);
		for (int i = 0; i < nodes.size(); i++) {
			if (i >= 1) {
				logger_.error("stock_phrase_semantic.xml-" + semanticPatternId + ": too many result");
				break;
			}
			Node child = nodes.get(i);
			result.setId(0);
			result.setType(XmlReader.getAttributeByName(child, "type"));
			result.setValueType(XmlReader.getAttributeByName(child, "ValueType"));
		}
	}
	
	private static void loadProps(Node node, ClassNodeFacade propsClassNode, String tagname) {
		String semanticPatternId = XmlReader.getAttributeByName(node, "id"); // 语义编号
		//propsClassNode.text = semanticPatternId;
        propsClassNode.setText("");
        ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, tagname);
        for (int i = 0; i < nodes.size(); i++) {
        	Node child = nodes.get(i);
        	NamedNodeMap nnm = child.getAttributes();
            if (nnm.getLength() == 0 
            		|| nnm.getNamedItem("label") == null 
            		|| nnm.getNamedItem("type") == null
            		|| nnm.getNamedItem("unit") == null) {
            	continue;
            }
            String propLabel = nnm.getNamedItem("label").getNodeValue().toLowerCase();
            String type = nnm.getNamedItem("type").getNodeValue().toLowerCase();
            String unit = nnm.getNamedItem("unit").getNodeValue().toLowerCase();
            PropNodeFacade pn = new PropNodeFacade(propLabel);
            pn.setValueType_forPhraseXmlReaderOnly(OntoXmlReader.parseValueType_(type));
            OntoXmlReader.parseValueUnit_(unit, pn, propsClassNode);
            pn.addOfWhat_forPhraseXmlReaderOnly(propsClassNode);
            propsClassNode.addProp_forPhraseXmlReaderSemanticPatternUseOnly(pn);
        }
	}
}
