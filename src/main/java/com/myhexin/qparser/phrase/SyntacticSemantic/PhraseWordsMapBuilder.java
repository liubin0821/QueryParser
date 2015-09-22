package com.myhexin.qparser.phrase.SyntacticSemantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo.WordsInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPatternMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPatternMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.IndexGroupMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroup;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroupMap;

public class PhraseWordsMapBuilder {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseWordsMapBuilder.class.getName());

	public PhraseWordsMapBuilder() {
		
	}

	public static HashMap<String, ArrayList<WordsInfo>> build(IndexGroupMap indexGroupMap, KeywordGroupMap keywordGroupMap, SyntacticPatternMap syntacticPatternMap, SemanticPatternMap semanticPatternMap) {
		HashMap<String, ArrayList<WordsInfo>> wordsInfoMap = new HashMap<String, ArrayList<WordsInfo>>();
		
		Set<String> keyset = syntacticPatternMap.getAllSyntacticPatternIds();
		Iterator<String> it = keyset.iterator();
		while (it.hasNext()) {
			String id = it.next();
			SyntacticPattern syntacticPattern = syntacticPatternMap.getSyntacticPattern(id);
			int syntacticElementSequenceMax = syntacticPattern.getSyntacticElementMax();
			boolean isFirstKeyword = true;
			for (int i = 1; i < syntacticElementSequenceMax; i++) {
				SyntacticElement syntacticElement = syntacticPattern.getSyntacticElement(i);
				int argumentId = syntacticElement.getSemanticBind();
				//System.out.println("SY:"+ id + "SE:" + syntacticPattern.getSemanticBind().getId());
				SemanticPattern semanticPattern = semanticPatternMap.getSemanticPattern(syntacticPattern.getSemanticBind().getId(), false);
				SemanticArgument argument = semanticPattern==null? null : semanticPattern.getSemanticArgument(argumentId, false);
				buildSynaticElementMap(id, syntacticElement, argument, isFirstKeyword, keywordGroupMap, wordsInfoMap);
				if (syntacticElement.getType() == SyntacticElement.SyntElemType.KEYWORD && syntacticElement.getCanAbsent() == false)
					isFirstKeyword = false;
			}

			// 按照层次关系构建新配置句式与语义的映射
			buildSynaticSemanticsMap(id, syntacticPattern, semanticPatternMap, indexGroupMap);
		}
		return wordsInfoMap;
	}

	private static void buildSynaticSemanticsMap(String id, SyntacticPattern syntacticPattern, SemanticPatternMap semanticPatternMap, IndexGroupMap indexGroupMap) {
		SemanticBind semanticBind = syntacticPattern.getSemanticBind();
		if (semanticBind.getSemanticBindTos() == null || semanticBind.getSemanticBindTos().size() <= 0) {
			logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo not exist");
			return ;
		}
		String representation = semanticBind.getChineseRepresentation();
        int idx = representation.indexOf("#");
        while (idx != -1 && idx < representation.length() - 1) {
            String txt = "";
            for (int i = idx + 1; i < representation.length(); i++) {
                char c = representation.charAt(i);
                if (Character.isDigit(c)) {
                    txt += c;
                } else {
                    break;
                }
            }
            if (txt.length() == 0) {
                idx = representation.indexOf("#", idx + 1);
                continue;
            }
            int arg = Integer.parseInt(txt);
            buildSyntacticSemanticMap(id, syntacticPattern, arg, semanticPatternMap, indexGroupMap);
            idx = representation.indexOf("#", idx + 1);
        }
	}
	
	private static void buildSyntacticSemanticMap(String id, SyntacticPattern syntacticPattern, int sequence, SemanticPatternMap semanticPatternMap, IndexGroupMap indexGroupMap) {
		SemanticBind semanticBind = syntacticPattern.getSemanticBind();
		SemanticBindTo semanticBindTo = semanticBind.getSemanticBindTo(sequence);
		SemanticPattern semanticPattern = semanticPatternMap.getSemanticPattern(semanticBindTo.getBindToId()+ "", false);
		if (semanticPattern == null) {
			logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " not exist");
			return ;
		}
		ClassNodeFacade semanticPropsClassNode = semanticPattern.getPropsClassNode();
		if (semanticPropsClassNode.getAllProps().size() > 0) {
			int semanticPropsClassNodeId = syntacticPattern.getFixedArgumentMax();
			syntacticPattern.addSemanticPropsClassNode(semanticPropsClassNode);
			semanticBindTo.setSemanticPropsClassNodeId(semanticPropsClassNodeId);
		}
		for (int j = 0; j < semanticBindTo.getSemanticBindToArguments().size(); j++) {
			SemanticBindToArgument semanticBindToArgument = semanticBindTo.getSemanticBindToArgument(j + 1);
			int argumentId = semanticBindToArgument.getArgumentId();
			Source source = semanticBindToArgument.getSource();
			BindToType bindToType = semanticBindToArgument.getBindToType();
			int elementId = semanticBindToArgument.getElementId();
			SyntacticElement syntacticElement = syntacticPattern.getSyntacticElement(elementId);
			if (source == Source.FIXED && (semanticBindToArgument.getIndex() == null && semanticBindToArgument.getValue() == null)) {
				logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindToArgument fixed no index or no value");
			}
			else if (source == Source.FIXED) {
				if (semanticBindToArgument.getIndex() != null) {
					SyntacticElement fixedArgument = syntacticPattern.getFixedArgument();
					fixedArgument.setType(SyntElemType.ARGUMENT);
					fixedArgument.setDefaultIndex(semanticBindToArgument.getIndex());
					SemanticArgument argument = semanticPattern.getSemanticArgument(argumentId, false);
					if (argument == null) {
						logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " argument " + argumentId +  " not exist");
						continue;
					}
					semanticBindToArgument.setType(argument.getType()+"");
					semanticBindToArgument.setElementId(fixedArgument.getSequence());
					mergeSynaticElementMap(fixedArgument, argument, id, semanticBindTo.getBindToId()+"", indexGroupMap);
				}
				else if (semanticBindToArgument.getValue() != null) {
					SyntacticElement fixedArgument = syntacticPattern.getFixedArgument();
					fixedArgument.setType(SyntElemType.ARGUMENT);
					fixedArgument.setDefaultValue(semanticBindToArgument.getValue());
					SemanticArgument argument = semanticPattern.getSemanticArgument(argumentId, false);
					if (argument == null) {
						logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " argument " + argumentId +  " not exist");
						continue;
					}
					semanticBindToArgument.setType(argument.getType()+"");
					semanticBindToArgument.setElementId(fixedArgument.getSequence());
					mergeSynaticElementMap(fixedArgument, argument, id, semanticBindTo.getBindToId()+"", indexGroupMap);
				}
			}
			else if (source == Source.ELEMENT && bindToType == BindToType.SYNTACTIC_ELEMENT && syntacticElement != null) {
				SemanticArgument argument = semanticPattern.getSemanticArgument(argumentId, false);
				semanticBindToArgument.setType(argument.getType()+"");
				mergeSynaticElementMap(syntacticElement, argument, id, semanticBindTo.getBindToId()+"", indexGroupMap);
			}
			else if (source == Source.ELEMENT && bindToType == BindToType.SEMANTIC && elementId != -1) {
				SemanticArgument argument = semanticPattern.getSemanticArgument(argumentId, false);
				//System.out.println("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " argument " + argumentId);
				//System.out.println(argument);
				//System.out.println(semanticPattern.getResult());
				if (argument == null 
						|| argument.getType() != null && semanticPattern.getResult().getType() != null && argument.getType() != semanticPattern.getResult().getType()
						|| !argument.isContainsValueType(ValueType.UNDEFINED)
						&& !semanticPattern.getResult().isContainsValueType(ValueType.UNDEFINED)
						&& !semanticPattern.getResult().isSameValueType(argument.getAllAcceptValueTypes())) {
					logger_.error("stock_phrase_syntactic.xml-" + id + ": SemanticBindTo id " + semanticBindTo.getBindToId() + " argument " + argumentId +  " not match");
					continue;
				}
				buildSyntacticSemanticMap(id, syntacticPattern, elementId, semanticPatternMap, indexGroupMap);
			}
		}
	}

	private static void buildSynaticElementMap(String id, SyntacticElement syntacticElement, SemanticArgument argument, 
			boolean isFirstKeyword, KeywordGroupMap keywordGroupMap, HashMap<String, ArrayList<WordsInfo>> wordsInfoMap) {
		SyntacticElement.SyntElemType type = syntacticElement.getType();
		if (type == SyntacticElement.SyntElemType.KEYWORD) {
			// add keyword
			WordsInfo wordsInfo = createWordsInfo(WordsInfo.Type.KEYWORD, id, null);
			String keyword = syntacticElement.getKeyword();
			if (keyword != null && keyword.equals("") == false) {
				if (isFirstKeyword)
					addWordsInfoToMap(keyword, wordsInfo, wordsInfoMap);
				else 
					addWordsInfoToMap(keyword, wordsInfo, wordsInfoMap, false);
				return;
			}

			// add keywords group
			String keywordGroupId = syntacticElement.getKeywordGroup();
			if (keywordGroupId == null)
				return;
			KeywordGroup keywordGroup = keywordGroupMap.getKeywordGroup(keywordGroupId);
			if (keywordGroup == null)
				return;
			ArrayList<String> keywords = keywordGroup.getKeywords();
			for (int i = 0; i < keywords.size(); i++) {
				keyword = keywords.get(i);
				if (keyword != null && keyword.equals("") == false) {
					if (isFirstKeyword)
						addWordsInfoToMap(keyword, wordsInfo, wordsInfoMap);
					else
						addWordsInfoToMap(keyword, wordsInfo, wordsInfoMap, false);
				}
			}
		}
	}

	private static void mergeSynaticElementMap(SyntacticElement syntacticElement, SemanticArgument argument,
			String syntacticPatternId, String semanticPatternId, IndexGroupMap indexGroupMap) {
		if (syntacticElement.getType() == SyntElemType.KEYWORD) {
			logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SemanticBindToArgument SyntacticElement id wrong");
			return;
		}
		if (syntacticElement.getType() == SyntacticElement.SyntElemType.ARGUMENT && argument != null) {
			boolean isRight = true;
			isRight = isRight == false ? isRight : syntacticElement.mergeArgumentType(argument.getType());
			isRight = isRight == false ? isRight : syntacticElement.mergeValueType(argument.getAllAcceptValueTypes());
			isRight = isRight == false ? isRight : syntacticElement.mergeListElementMinCount(argument.getListElementMinCount());
			isRight = isRight == false ? isRight : !syntacticElement.getCanAbsent() | syntacticElement.mergeDefaultIndex(argument.getDefaultIndex());
			isRight = isRight == false ? isRight : !syntacticElement.getCanAbsent() | syntacticElement.mergeDefaultValue(argument.getDefaultValue());
			isRight = isRight == false ? isRight : syntacticElement.mergeSpecificIndex(argument.getSpecificIndex());
			isRight = isRight == false ? isRight : syntacticElement.mergeSpecificIndexGroup(argument.getSpecificIndexGroup(), indexGroupMap);
			if (isRight == false) {
				logger_.error("stock_phrase_syntactic.xml-" + syntacticPatternId + ": SemanticBindToArgument SyntacticElement id wrong");
				logger_.error(syntacticElement.getArgument().toString()+":"+syntacticElement.getCanAbsent()+":"+syntacticElement.getCanAbsent());
				logger_.error(argument.toString());
				return;
			}
		}
	}

	private static WordsInfo createWordsInfo(WordsInfo.Type type, String id, String index) {
		WordsInfo wordsInfo = new WordsInfo(type, id, index);
		return wordsInfo;
	}

	private static void addWordsInfoToMap(String word, WordsInfo wordsInfo, HashMap<String, ArrayList<WordsInfo>> wordsInfoMap) {
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
	
	private static void addWordsInfoToMap(String word, WordsInfo wordsInfo, HashMap<String, ArrayList<WordsInfo>> wordsInfoMap, boolean isFirstKeyword) {
		if(!isFirstKeyword)
			wordsInfo = createWordsInfo(WordsInfo.Type.KEYWORD, "0", null);
		addWordsInfoToMap(word, wordsInfo, wordsInfoMap);
	}
}
