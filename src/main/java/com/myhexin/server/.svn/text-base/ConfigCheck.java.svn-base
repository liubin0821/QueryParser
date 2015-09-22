package com.myhexin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseInfo.WordsInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.PhraseXmlReaderSyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroup;
import com.myhexin.qparser.xmlreader.XmlReader;

public class ConfigCheck {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ConfigCheck.class.getName());
	
	public static Document checkConfig(Document doc) {
		ArrayList<SyntacticPattern> sps = getSyntacticPatterns(doc);
		if (sps == null || sps.size() == 0)
			return null;
		Document result = checkSyntacticPatterns(sps);
		return result;
	}
	
	public static List<Result> checkConfigList(Document doc) {
		ArrayList<SyntacticPattern> sps = getSyntacticPatterns(doc);
		if (sps == null || sps.size() == 0)
			return null;
		List<Result> results = checkSyntacticPatternsList(sps);
		return results;
	}
	
	private static ArrayList<SyntacticPattern> getSyntacticPatterns(Document doc) {
		ArrayList<SyntacticPattern> sps = new ArrayList<SyntacticPattern>();
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "SyntacticPattern");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null || id.equals("")) {
				id = "0";
			}
			SyntacticPattern syntacticPattern = new SyntacticPattern(id);
			PhraseXmlReaderSyntacticPattern.loadSyntacticPattern(node, syntacticPattern);
			sps.add(syntacticPattern);
		}
		return sps;
	}
	
	private static Document checkSyntacticPatterns(ArrayList<SyntacticPattern> sps) {
		if (sps == null || sps.size() == 0)
			return null;
		Document doc = null;
		Element root = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			root = doc.createElement("results");
			doc.appendChild(root);
		} catch (Exception e) {
			e.printStackTrace();
			return null;// 如果出现异常，则不再往下执行
		}
		  
		Iterator<SyntacticPattern> it = sps.iterator();
		
		while (it.hasNext()) {
			SyntacticPattern syntacticPattern = it.next();
			String id = syntacticPattern.getId();
			String type = "OK";
			String info = "";
			String result = checkSyntacticPattern(syntacticPattern);
			if (result == null || result.equals("")) {
				
			} else {
				type = "ERROR";
				info = result;
			}
			Element element = doc.createElement("result");
			Element elementId = doc.createElement("id");
			Element elementType = doc.createElement("type");
			Element elementInfo = doc.createElement("info");
			elementId.setTextContent(id);
			elementType.setTextContent(type);
			elementInfo.setTextContent(info);
			element.appendChild(elementId);
			element.appendChild(elementType);
			element.appendChild(elementInfo);
			root.appendChild(element);
		}
		return doc;
	}
	
	private static List<Result> checkSyntacticPatternsList(ArrayList<SyntacticPattern> sps) {
		if (sps == null || sps.size() == 0)
			return null;
		 
		List<Result> results = new ArrayList<Result>();
		
		Iterator<SyntacticPattern> it = sps.iterator();
		
		while (it.hasNext()) {
			SyntacticPattern syntacticPattern = it.next();
			String id = syntacticPattern.getId();
			String type = "OK";
			String info = "";
			String result = checkSyntacticPattern(syntacticPattern);
			if (result == null || result.equals("")) {
				
			} else {
				type = "ERROR";
				info = result;
			}
			Result r = new Result(id, type, info);
			results.add(r);
		}
		return results;
	}
	
	private static String checkSyntacticPattern(SyntacticPattern syntacticPattern) {
		StringBuilder msg = new StringBuilder();
		String id = syntacticPattern.getId();
		int syntacticElementSequenceMax = syntacticPattern.getSyntacticElementMax();
		// 旧配置句式与语义的映射：<SyntacticElement sequence="1" type="argument" SemanticBind="1">
		for (int i = 1; i < syntacticElementSequenceMax; i++) {
			SyntacticElement syntacticElement = syntacticPattern.getSyntacticElement(i);
			int argumentId = syntacticElement.getSemanticBind();
			SemanticPattern semanticPattern = PhraseInfo.getSemanticPattern(syntacticPattern.getSemanticBind().getId());
			SemanticArgument argument = semanticPattern==null? null : semanticPattern.getSemanticArgument(argumentId, false);
			buildSynaticElementMap(id, syntacticElement, argument);
		}

		// 新配置句式与语义的映射
		SemanticBind semanticBind = syntacticPattern.getSemanticBind();
		if (semanticBind.getSemanticBindTos() != null 
				&& semanticBind.getSemanticBindTos().size() > 0) {
			for (int i = 0; i < semanticBind.getSemanticBindTos().size(); i++) {
				SemanticBindTo semanticBindTo = semanticBind.getSemanticBindTo(i + 1);
				SemanticPattern semanticPattern = PhraseInfo.getSemanticPattern(semanticBindTo.getBindToId()+ "");
				if (semanticPattern == null) {
					//logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " not exist");
					msg.append("句式-" + id + ": 绑定的语义-" + semanticBindTo.getBindToId() + " 不存在");
					continue;
				}
				for (int j = 0; j < semanticBindTo.getSemanticBindToArguments().size(); j++) {
					SemanticBindToArgument semanticBindToArgument = semanticBindTo.getSemanticBindToArgument(j + 1);
					int argumentId = semanticBindToArgument.getArgumentId();
					Source source = semanticBindToArgument.getSource();
					BindToType bindToType = semanticBindToArgument.getBindToType();
					int elementId = semanticBindToArgument.getElementId();
					SyntacticElement syntacticElement = syntacticPattern.getSyntacticElement(elementId);
					if (source == Source.FIXED 
							&& (semanticBindToArgument.getIndex() == null && semanticBindToArgument.getValue() == null)) {
						//logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindToArgument fixed no index or no value");
						msg.append("句式-" + id + ": 绑定的语义-" + semanticBindTo.getBindToId() + " 下的元素-"+argumentId+" 未设置index或value");
					}
					else if (source == Source.ELEMENT && bindToType == BindToType.SYNTACTIC_ELEMENT && syntacticElement != null) {
						SemanticArgument argument = semanticPattern.getSemanticArgument(argumentId, false);
						semanticBindToArgument.setType(argument.getType()+"");
						String result = mergeSynaticElementMap(syntacticElement, argument, id, semanticBindTo.getBindToId()+"");
						msg.append(result);
					}
				}
			}
		}
		return msg.toString();
	}

	private static void buildSynaticElementMap(String id, SyntacticElement syntacticElement, SemanticArgument argument) {
		SyntacticElement.SyntElemType type = syntacticElement.getType();
		if (type == SyntacticElement.SyntElemType.KEYWORD) {
			// add keyword
			WordsInfo wordsInfo = createWordsInfo(WordsInfo.Type.KEYWORD, id,null);
			String keyword = syntacticElement.getKeyword();
			if (keyword != null && keyword.equals("") == false) {
				addWordsInfoToMap(keyword, wordsInfo);
				return;
			}

			// add keywords group
			String keywordGroupId = syntacticElement.getKeywordGroup();
			if (keywordGroupId == null)
				return;
			KeywordGroup keywordGroup = PhraseInfo.keywordGroupMap_.getKeywordGroup(keywordGroupId);
			if (keywordGroup == null)
				return;
			ArrayList<String> keywords = keywordGroup.getKeywords();
			for (int i = 0; i < keywords.size(); i++) {
				keyword = keywords.get(i);
				if (keyword != null && keyword.equals("") == false) {
					addWordsInfoToMap(keyword, wordsInfo);
				}
			}
		} else if (type == SyntacticElement.SyntElemType.ARGUMENT && argument != null) {
			// 对原有配置方式的处理
			syntacticElement.setArgument(argument);
		}
	}

	private static String mergeSynaticElementMap(SyntacticElement syntacticElement, SemanticArgument argument,
			String syntacticPatternId, String semanticPatternId) {
		StringBuilder msg = new StringBuilder();
		if (syntacticElement.getType() == SyntElemType.KEYWORD) {
			//logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SemanticBindToArgument SyntacticElement id wrong");
			msg.append("句式-" + syntacticPatternId + ": 绑定的语义-" + semanticPatternId + " 对应的句式中的语法元素序号-" + syntacticElement.getSequence() + "不对");
			return msg.toString();
		}
		if (syntacticElement.getType() == SyntacticElement.SyntElemType.ARGUMENT && argument != null) {
			boolean isRight = true;
			isRight = isRight == false ? isRight : syntacticElement.mergeArgumentType(argument.getType());
			isRight = isRight == false ? isRight : syntacticElement.mergeValueType(argument.getAllAcceptValueTypes());
			isRight = isRight == false ? isRight : syntacticElement.mergeListElementMinCount(argument.getListElementMinCount());
			isRight = isRight == false ? isRight : !syntacticElement.getCanAbsent() | syntacticElement.mergeDefaultIndex(argument.getDefaultIndex());
			isRight = isRight == false ? isRight : !syntacticElement.getCanAbsent() | syntacticElement.mergeDefaultValue(argument.getDefaultValue());
			isRight = isRight == false ? isRight : syntacticElement.mergeSpecificIndex(argument.getSpecificIndex());
			isRight = isRight == false ? isRight : syntacticElement.mergeSpecificIndexGroup(argument.getSpecificIndexGroup(), PhraseInfo.indexGroupMap_);
			if (isRight == false) {
				//logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SemanticBindToArgument SyntacticElement id wrong");
				//logger_.error(syntacticElement.getArgument().toString()+":"+syntacticElement.getCanAbsent()+":"+syntacticElement.getCanAbsent());
				//logger_.error(argument.toString());
				msg.append("句式-" + syntacticPatternId + ": 绑定的语义-" + semanticPatternId + " 语义元素(序号-" + argument.getId() + ")与对应的句式元素不匹配");
				msg.append("语义元素-" + argument.toString());
				msg.append("句式元素-" + syntacticElement.getArgument().toString()+":是否缺省"+syntacticElement.getCanAbsent());
			}
		}
		return msg.toString();
	}

	private static WordsInfo createWordsInfo(WordsInfo.Type type, String id, String index) {
		WordsInfo wordsInfo = new WordsInfo(type, id, index);
		return wordsInfo;
	}

	private static void addWordsInfoToMap(String word, WordsInfo wordsInfo) {
		HashMap<String, ArrayList<WordsInfo>> wordsInfoMap = PhraseInfo.wordsInfoMap_;
		ArrayList<WordsInfo> wordsInfoList = wordsInfoMap.get(word);
		if (wordsInfoList == null) {
			wordsInfoList = new ArrayList<WordsInfo>();
			wordsInfoMap.put(word, wordsInfoList);
		}
		for (int i = 0; i < wordsInfoList.size(); i++) {
			if (wordsInfoList.get(i).isSame(wordsInfo)) {
				return;
			}
		}
		wordsInfoList.add(wordsInfo);
	}
	
	public static class Result {
		private String id = "";
		private String type = "";
		private String info = "";
		
		public Result(String id, String type, String info) {
			this.id = id;
			this.type = type;
			this.info = info;
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getInfo() {
			return info;
		}
		
		public void setInfo(String info) {
			this.info = info;
		}
	}
}
