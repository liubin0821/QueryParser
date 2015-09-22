package com.myhexin.qparser.phrase;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

import com.myhexin.qparser.phrase.SyntacticSemantic.PhraseWordsMapBuilder;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.PhraseXmlReaderSemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPatternMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.PhraseXmlReaderSyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPatternMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.IndexGroup;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.IndexGroupMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroup;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroupMap;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.PhraseXmlReaderIndexGroup;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.PhraseXmlReaderKeywordGroup;
import com.myhexin.qparser.util.Util;

public class PhraseInfo {
	public static IndexGroupMap indexGroupMap_ = new IndexGroupMap();
	public static KeywordGroupMap keywordGroupMap_ = new KeywordGroupMap();
	public static SyntacticPatternMap syntacticPatternMap_ = new SyntacticPatternMap();
	public static SemanticPatternMap semanticPatternMap_ = new SemanticPatternMap();
	public static HashMap<String, ArrayList<WordsInfo>> wordsInfoMap_ = new HashMap<String, ArrayList<WordsInfo>>();

	public static void init() {
		indexGroupMap_ = new IndexGroupMap();
		keywordGroupMap_ = new KeywordGroupMap();
		syntacticPatternMap_ = new SyntacticPatternMap();
		semanticPatternMap_ = new SemanticPatternMap();
		wordsInfoMap_ = new HashMap<String, ArrayList<WordsInfo>>();
	}
	
	public static void reload(IndexGroupMap indexGroupMap, KeywordGroupMap keywordGroupMap, SyntacticPatternMap syntacticPatternMap, SemanticPatternMap semanticPatternMap, HashMap<String, ArrayList<WordsInfo>> wordsInfoMap) {
		indexGroupMap_ = indexGroupMap;
		keywordGroupMap_ = keywordGroupMap;
		syntacticPatternMap_ = syntacticPatternMap;
		semanticPatternMap_ = semanticPatternMap;
		wordsInfoMap_ = wordsInfoMap;
	}

	public static void loadPhraseInfo() {
		try {
			wordsInfoMap_ = PhraseWordsMapBuilder.build(indexGroupMap_, keywordGroupMap_, syntacticPatternMap_, semanticPatternMap_);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadPhraseInfoIndexGroup(Document doc) {
		try {
			indexGroupMap_ = PhraseXmlReaderIndexGroup.loadIndexGroupMap(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadPhraseInfoKeywordGroup(Document doc) {
		try {
			keywordGroupMap_ = PhraseXmlReaderKeywordGroup.loadKeywordGroupMap(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadPhraseInfoSyntacticPattern(Document doc) {
		try {
			syntacticPatternMap_ = PhraseXmlReaderSyntacticPattern.loadSyntacticPatternMap(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadPhraseInfoSemanticPattern(Document doc) {
		try {
			semanticPatternMap_ = PhraseXmlReaderSemanticPattern.loadSemanticPatternMap(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<WordsInfo> getWordsInfo(String word) {
		return wordsInfoMap_.get(word);
	}

	public static HashMap<String, ArrayList<WordsInfo>> getWordsInfoMap() {
		return wordsInfoMap_;
	}

	public static SemanticPattern getSemanticPattern(String id) {
		return semanticPatternMap_.getSemanticPattern(id, false);
	}

	public static SyntacticPattern getSyntacticPattern(String id) {
		return syntacticPatternMap_.getSyntacticPattern(id, false);
	}

	public static KeywordGroup getKeywordGroup(String id) {
		return keywordGroupMap_.getKeywordGroup(id, false);
	}

	public static IndexGroup getIndexGroup(String id) {
		return indexGroupMap_.getIndexGroup(id, false);
	}

	public static class WordsInfo {
		public WordsInfo(Type type, String id, String index) {
			type_ = type;
			id_ = id;
			index_ = index;
		}

		public Type getType() {
			return type_;
		}

		public String getId() {
			return id_;
		}

		public String getIndex() {
			return index_;
		}

		public static enum Type {
			INDEX, KEYWORD
		}

		public boolean isSame(WordsInfo wInfo) {
			if (this.type_ == wInfo.type_
					&& Util.isEqualStr(this.id_, wInfo.id_)
					&& Util.isEqualStr(this.index_, wInfo.index_)) {
				return true;
			}
			return false;
		}

		private Type type_ = Type.INDEX; // default is INDEX
		private String id_ = null;
		private String index_ = null;
	}
}
