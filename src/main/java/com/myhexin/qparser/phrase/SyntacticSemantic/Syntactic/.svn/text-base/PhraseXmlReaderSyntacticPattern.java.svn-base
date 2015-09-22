package com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic;

import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgumentDependency;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArguments;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.xmlreader.XmlReader;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PhraseXmlReaderSyntacticPattern {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseXmlReaderSyntacticPattern.class.getName());
	/**
	 * parse config file to memory
	 * 
	 * @param doc XMLDom 文档
	 * @param syntacticPatternMap 将parse出来的内容存储在这里
	 * @return void
	 */
	public static SyntacticPatternMap loadSyntacticPatternMap(Document doc) {
		// System.out.println("loadSyntaticPatternMap");
		SyntacticPatternMap syntacticPatternMap = new SyntacticPatternMap();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "SyntacticPattern");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null || id.equals("")) {
				logger_.error("stock_phrase_syntactic.xml: SyntacticPattern no id");
				continue;
			}
			SyntacticPattern syntacticPattern = syntacticPatternMap.getSyntacticPattern(id, true);
			loadSyntacticPattern(node, syntacticPattern);
		}
		return syntacticPatternMap;
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

	public static void loadSyntacticPattern(Node node,
			SyntacticPattern syntacticPattern) {
		String syntacticPatternId = XmlReader.getAttributeByName(node, "id");
		syntacticPattern.setDescription(XmlReader.getChildElementValueByTagName(node, "Description"));
		syntacticPattern.setSyntacticType(XmlReader.getChildElementValueByTagName(node, "SyntacticType"));
		SemanticBind semanticBind=null;
		Node semanticBindNode = XmlReader.getChildElementByTagName(node, "SemanticBind");
		if (semanticBindNode != null) {
			semanticBind = syntacticPattern.getSemanticBind();
			loadSemanticBind(semanticBindNode, semanticBind, syntacticPatternId);
		} else {
			logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SyntacticPattern no SemanticBind");
		}

		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "SyntacticElement");
		for (int i = 0; i < nodes.size(); i++) {
			Node syntacticElementNode = nodes.get(i);
			String sequence = XmlReader.getAttributeByName(syntacticElementNode, "sequence");
			if (sequence == null || sequence.equals("")) {
				logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SyntacticElement no sequence");
				continue;
			}
			SyntacticElement syntacticElement = syntacticPattern.getSyntacticElement(sequence);
			if (syntacticElement == null) {
				logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SyntacticElement id not in order");
				continue;
			}
			loadSyntacticElement(syntacticElementNode, syntacticElement);
		}

		nodes = XmlReader.getChildElementsByTagName(node, "Modifier");
		for (int i = 0; i < nodes.size(); i++) {
			node = nodes.get(i);
			String value = XmlReader.getElementValue(node);
			if (value != null) {
				syntacticPattern.addModifier(value);
			}
		}
	}

	private static void loadSemanticBind(Node node, SemanticBind semanticBind, String syntacticPatternId) {
		semanticBind.setId(XmlReader.getAttributeByName(node, "BindTo"));
		loadArguments(node, semanticBind.getArguments(), "Argument");
		loadArgumentDependency(
				XmlReader.getChildElementByTagName(node, "ArgumentDependency"),
				semanticBind.getArgumentDependency());
		
		// 新增
		semanticBind.setUiRepresentation(XmlReader.getChildElementValueByTagName(node, "UiRepresentation"));
		semanticBind.setChineseRepresentation(XmlReader.getChildElementValueByTagName(node, "ChineseRepresentation"));
		semanticBind.setDescription(XmlReader.getChildElementValueByTagName(node, "Description"));
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "SemanticBindTo");
		String semanticBindRoot = XmlReader.getChildElementValueByTagName(node, "ChineseRepresentation");
		for (int i = 0; i < nodes.size(); i++) {
			Node semanticBindToNode = nodes.get(i);
			String sequence = XmlReader.getAttributeByName(semanticBindToNode, "sequence");
			String id = XmlReader.getAttributeByName(semanticBindToNode, "id");
			if (sequence == null || id == null) {
				logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SemanticBindTo no sequence or id");
				continue;
			}
			semanticBindRoot = semanticBindRoot.replace("#"+sequence, id);
			SemanticBindTo semanticBindTo = semanticBind.getSemanticBindTo(sequence);
			if (semanticBindTo == null) {
				continue;
			}
			semanticBindTo.setBindToId(id);
			loadSemanticBindTo(semanticBindToNode, semanticBindTo);
		}
		semanticBind.setSemanticBindToIds(semanticBindRoot);
	}

	private static void loadSemanticBindTo(Node semanticBindToNode,
			SemanticBindTo semanticBindTo) {
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(semanticBindToNode, "SemanticArgument");
		for (int i = 0; i < nodes.size(); i++) {
			Node semanticBindToArgumentNode = nodes.get(i);
			String sequence = XmlReader.getAttributeByName(semanticBindToArgumentNode, "id");
			if (sequence == null)
				continue;
			SemanticBindToArgument semanticBindToArgument = semanticBindTo.getSemanticBindToArgument(sequence);
			loadSemanticBindToArgument(semanticBindToArgumentNode, semanticBindToArgument);
		}
	}

	private static void loadSemanticBindToArgument(
			Node semanticBindToArgumentNode,
			SemanticBindToArgument semanticBindToArgument) {
		semanticBindToArgument.setArgumentId(XmlReader.getAttributeByName(semanticBindToArgumentNode, "id"));
		semanticBindToArgument.setType(XmlReader.getAttributeByName(semanticBindToArgumentNode, "type"));
		semanticBindToArgument.setSource(XmlReader.getAttributeByName(semanticBindToArgumentNode, "source"));
		semanticBindToArgument.setElementId(XmlReader.getAttributeByName(semanticBindToArgumentNode, "SyntacticElement"));
		semanticBindToArgument.setFrom(XmlReader.getAttributeByName(semanticBindToArgumentNode, "from"));
		semanticBindToArgument.setTo(XmlReader.getAttributeByName(semanticBindToArgumentNode, "to"));
		semanticBindToArgument.setIndex(XmlReader.getAttributeByName(semanticBindToArgumentNode, "index"));
		semanticBindToArgument.setValue(XmlReader.getAttributeByName(semanticBindToArgumentNode, "value"));
		
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
		ArrayList<Node> nodes;

		nodes = XmlReader.getChildElementsByTagName(node, tagname);
		for (int i = 0; i < nodes.size(); i++) {
			Node child = nodes.get(i);
			String id = XmlReader.getAttributeByName(child, "id");
			if (id == null)
				continue;
			SemanticArgument argument = arguments.getSemanticArgument(id, true);
			if (argument == null)
				continue;
			loadArgument(child, argument);
		}
	}

	private static void loadSyntacticElement(Node node,
			SyntacticElement syntacticElement) {
		syntacticElement.setType(XmlReader.getAttributeByName(node, "type"));
		syntacticElement.setSemanticBind(XmlReader.getAttributeByName(node, "SemanticBind"));
		syntacticElement.setKeyword(XmlReader.getAttributeByName(node, "Keyword"));
		syntacticElement.setKeywordGroup(XmlReader.getAttributeByName(node, "KeywordGroup"));
		syntacticElement.setCanAbsent(XmlReader.getAttributeByName(node, "CanAbsent"));
		syntacticElement.setShouldIgnore(XmlReader.getAttributeByName(node, "ShouldIgnore"));
		//syntacticElement.setArgumentType(XmlReader.getAttributeByName(node, "ArgumentType"));
		syntacticElement.setArgumentType(XmlReader.getAttributeByName(node, "type"));
		syntacticElement.setSyntElemValueType(XmlReader.getAttributeByName(node, "ValueType"));
		syntacticElement.setSyntElemSubType(XmlReader.getAttributeByName(node, "SubType"));
		syntacticElement.setSpecificIndex(XmlReader.getAttributeByName(node, "SpecificIndex"));
		syntacticElement.setSpecificIndexGroup(XmlReader.getAttributeByName(node, "SpecificIndexGroup"));
		syntacticElement.setDefaultIndex(XmlReader.getAttributeByName(node, "DefaultIndex"));
		syntacticElement.setDefaultValue(XmlReader.getAttributeByName(node, "DefaultValue"));
	}
}
