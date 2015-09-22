package com.myhexin.qparser.resource;

import java.io.IOException;

import org.w3c.dom.Document;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.FakeNumDefInfo;
import com.myhexin.qparser.conf.IndexDefOpInfo;
import com.myhexin.qparser.conf.IndexInteraction;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.fuzzy.FuzzySearcher;
import com.myhexin.qparser.ifind.IFindParamList;
import com.myhexin.qparser.ifind.IndexInfo;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.phrase.IndexTechopMapping;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.StopWords;
import com.myhexin.qparser.phrase.Timing;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginChangeNewIndexToIfindIndex;
import com.myhexin.qparser.phrase.parsePlugins.DateNumModify.DateNumModify;
import com.myhexin.qparser.phrase.parsePlugins.RemoveSomeConditionNode.RemoveSomeConditionNode;
import com.myhexin.qparser.phrase.parsePlugins.ThematicClassify.ThematicConfInfo;
import com.myhexin.qparser.phrase.util.Traditional2simplified;
import com.myhexin.qparser.rule.IndexRuleLoader;
import com.myhexin.qparser.smart.SmartAnswer;
import com.myhexin.qparser.suggest.QuerySuggest;
import com.myhexin.qparser.tokenize.NoParserMatch;
import com.myhexin.qparser.util.OnlineIpInfo;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.util.lightparser.LightParserTypeConf;
import com.myhexin.server.Parser;

public class Resource {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
	//private volatile static boolean loaded = false;

	/**
	 * @author 徐祥
	 * @createTime 2014-05-05 11:55
	 * @description 确保Param已被初始化
	 */
	public static synchronized void reloadData() throws DataConfException, UnexpectedException, NotSupportedException, IOException {
		long start = System.currentTimeMillis();
		logger_.info("开始加载");
		StringBuilder log = new StringBuilder();
		Timing t = new Timing();
		
		t.start();
		ResourceInst.getInstance().reloadResource();
		t.end();
		log.append(String.format("\n[%dms] %s\n", t.mills(), "ResourceInst.getInstance().reloadResource()")); 																				
			
		
		//ArrayList<String> configFileRealNameList = new ArrayList<String>();
		if (Param.SUPPORT_STOCK) {
			t.start();
			Document docCondition = Util.readXMLFile(Param.ALL_ONTO_CONDITION_FILE, true);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_ONTO_CONDITION_FILE));
			
			t.start();
			MemOnto.loadAllOnto(Util.readXMLFile(Param.ALL_ONTO_FILE, true),  docCondition);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_ONTO_FILE));	
			
			t.start();
			MemOnto.loadAllOntoOldIndex(Util.readXMLFile(Param.ALL_ONTO_FILE_OLD_SYSTEM, true),  docCondition);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_ONTO_FILE_OLD_SYSTEM));	
						
			t.start();			
			DateUtil.loadTradeDate();
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), "loadTradeDate"));	
			
			t.start();
			PhraseInfo.loadPhraseInfoIndexGroup(Util.readXMLFile(Param.ALL_PHRASE_INDEXGROUP, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_PHRASE_INDEXGROUP));	
			
			t.start();
			PhraseInfo.loadPhraseInfoKeywordGroup(Util.readXMLFile(Param.ALL_PHRASE_KEYWORDGROUP, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_PHRASE_KEYWORDGROUP));	
			
			t.start();
			PhraseInfo.loadPhraseInfoSyntacticPattern(Util.readXMLFile(Param.ALL_PHRASE_SYNTACTIC, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.ALL_PHRASE_SYNTACTIC));	
			
			t.start();
			PhraseInfo.loadPhraseInfoSemanticPattern(Util.readXMLFile(Param.ALL_PHRASE_SEMANTIC, true));
			t.end();
			log.append(String.format("[%dms] %s", t.mills(), Param.ALL_PHRASE_SEMANTIC));	
			
			t.start();
			PhraseInfo.loadPhraseInfo();
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), "loadPhraseInfo"));	
			
			t.start();
			ThematicConfInfo.loadResource(Param.STOCK_THEMATIC);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.STOCK_THEMATIC));	
			
			t.start();
			LightParserTypeConf.loadLightParserTypeConf(Util.readXMLFile(Param.LIGHTPARSER_TYPE, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.LIGHTPARSER_TYPE));	
						
			/*configFileRealNameList.add(Param.ALL_ONTO_FILE);
			configFileRealNameList.add(Param.ALL_ONTO_CONDITION_FILE);
			configFileRealNameList.add(Param.ALL_ONTO_FILE_OLD_SYSTEM);
			configFileRealNameList.add(Param.ALL_ONTO_CONDITION_FILE);
			configFileRealNameList.add(Param.ALL_PHRASE_INDEXGROUP);
			configFileRealNameList.add(Param.ALL_PHRASE_KEYWORDGROUP);
			configFileRealNameList.add(Param.ALL_PHRASE_SYNTACTIC);
			configFileRealNameList.add(Param.ALL_PHRASE_SEMANTIC);
			configFileRealNameList.add(Param.STOCK_THEMATIC);
			configFileRealNameList.add(Param.LIGHTPARSER_TYPE);*/
			
			//UserIndexInfo.loadStockFakeIndex(Util.readXMLFile(Param.STOCK_USER_INDEX_FILE, true));
			t.start();		 
			IndexTechopMapping.loadMapping(Util.readTxtFile(Param.INDEX_TECHOP_MAPPING, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.INDEX_TECHOP_MAPPING));	
			
			//configFileRealNameList.add(Param.INDEX_TECHOP_MAPPING);
			
						
			//IndexRuleLoader.loadStockRuleData(Util.readXMLFile(Param.STOCK_RULE_FILE, true));
			//IndexValueDefInfo.loadResource(Util.readXMLFile(Param.STOCK_DEF_VAL_FILE, true));
			t.start();		 
			IndexRuleLoader.loadIndexRuleData(Util.readXMLFile(Param.STOCK_RULE_FILE, true),Query.Type.ALL);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.STOCK_RULE_FILE));	
			
			t.start();
			IndexDefOpInfo.load(Util.readXMLFile(Param.STOCK_DEF_OP_FILE,true), Query.Type.ALL);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.STOCK_DEF_OP_FILE));	
			
			//Operator2Index.loadInfo(Util.readTxtFile(Param.OPERATOR_2_INDEX, true));
			/*t.start();			 
			FakeNumInfoForOper.loadInfo(Util.readTxtFile(Param.VAGUE_NUM_INFO_FOR_OPER_FILE, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.VAGUE_NUM_INFO_FOR_OPER_FILE));	
			*/
			t.start();
			FakeNumDefInfo.loadInfo(Util.readTxtFile(Param.FAKE_NUM_DEF_INFO_FILE, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.FAKE_NUM_DEF_INFO_FILE));	
			
			t.start();
			StopWords.init();
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), "StopWords.init()"));	
			
			t.start();
			NoParserMatch.init();
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), "NoParserMatch.init();"));	
			
			t.start();
			StopWords.loadStopWords();
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), "StopWords.loadStopWords()"));	
			
			if(OnlineIpInfo.isOnlineIp()){//线上加载,线下不加载
				t.start();
				NoParserMatch.loadNoParser();
				t.end();
				log.append(String.format("[%dms] %s\n", t.mills(), "NoParserMatch.loadNoParser()"));	
				
				t.start();
				NoParserMatch.loadNoParserWell();
				t.end();
				log.append(String.format("[%dms] %s\n", t.mills(), "NoParserMatch.loadNoParserWell()"));	
			}
						
			//configFileRealNameList.add(Param.STOCK_RULE_FILE);
			//configFileRealNameList.add(Param.STOCK_DEF_OP_FILE);
			//configFileRealNameList.add(Param.VAGUE_NUM_INFO_FOR_OPER_FILE);
			//configFileRealNameList.add(Param.FAKE_NUM_DEF_INFO_FILE);
						
		}
		if (Param.SUPPORT_IFUND) {
			t.start();
			PhraseParserPluginChangeNewIndexToIfindIndex.loadIfindIndexDate(Param.IFIND_INDEX_FILE);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.IFIND_INDEX_FILE));	
			//configFileRealNameList.add(Param.IFIND_INDEX_FILE);
		}
		
		if (Param.SUPPORT_FUND) {
			//MemOnto.loadFundOnto(Util.readXMLFile(Param.FUND_ONTO_FILE, true));
			t.start();
			IndexInfo.loadFundIndexInfo(Util.readXMLFile(Param.FUND_INDEX_FILE, true));
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.FUND_INDEX_FILE));	
			//configFileRealNameList.add(Param.FUND_INDEX_FILE);
		}
		
		if (Param.SUPPORT_FUND || Param.SUPPORT_STOCK) {
			t.start();
			IFindParamList.loadParamListInfo(Param.IFIND_PARAM_FILE);
			t.end();
			log.append(String.format("[%dms] %s\n", t.mills(), Param.IFIND_PARAM_FILE));	
			//configFileRealNameList.add(Param.IFIND_PARAM_FILE);
		}
		
		t.start();
		QuerySuggest.loadSuggestSentence(Util.readTxtFile(Param.SUGGEST_TEMP_FILE, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.SUGGEST_TEMP_FILE));	
		
		t.start();
		QuerySuggest.loadu2pHashMap(Util.readTxtFile(Param.UNICOD_TO_PINYIN, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.UNICOD_TO_PINYIN));	
		
		t.start();
		QuerySuggest.loadIndexInCommonUse(Util.readTxtFile(Param.IFIND_INDEX_IN_COMMON_USE, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.IFIND_INDEX_IN_COMMON_USE));	
		
		t.start();
		SmartAnswer.loadSmartAnswer(Util.readXMLFile(Param.SMART_ANSWER_FILE, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.SMART_ANSWER_FILE));	
		//IndexInteraction.loadIndexInteraction(Util.readXMLFile(Param.INDEX_INTERACTION, true));
		
		t.start();
		SpecialWords.loadSpecWordInfo(Util.readTxtFile(Param.SPECIAL_WORDS_FILE, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.SPECIAL_WORDS_FILE));	
		
		t.start();
		MsgDef.loadMessage(Util.readTxtFile(Param.MSG_DEF_FILE,true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.MSG_DEF_FILE));	
		
		//configFileRealNameList.add(Param.SUGGEST_TEMP_FILE);
		//configFileRealNameList.add(Param.UNICOD_TO_PINYIN);
		//configFileRealNameList.add(Param.IFIND_INDEX_IN_COMMON_USE);
		//configFileRealNameList.add(Param.SMART_ANSWER_FILE);
		//configFileRealNameList.add(Param.SPECIAL_WORDS_FILE);					
		t.start();
		FuzzySearcher.loadFuzzyModel();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "FuzzySearcher.loadFuzzyModel()"));	
		
		t.start();
		DateNumModify.loadData();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "DateNumModify.loadData()"));	
		
		t.start();
		RemoveSomeConditionNode.loadDate();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "RemoveSomeConditionNode.loadDate()"));	
		
		t.start();
		Traditional2simplified.loadData();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "Traditional2simplified.loadData()"));	
		
		t.start();
		IndexInteraction.loadData();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "IndexInteraction.loadData()"));	
					
		//刘小峰 2014/11/28 转回测
		//ChangeToNewDataBaseIndex.loadChangeToNewDatabaseIndex(Util.readTxtFile(Param.CHANGE_TO_NEW_DATABASE_INDEX,true) );
		t.start();
		IndexInfo.loadStockIndexInfo(Util.readXMLFile(Param.STOCK_INDEX_FILE, true));
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), Param.STOCK_INDEX_FILE));	
					
		//CheakQueryParserConfAndUp cp = new CheakQueryParserConfAndUp(configFileRealNameList);
		//cp.start();
		logger_.info(log.toString());
		long end = System.currentTimeMillis();
		logger_.info("结束加载 : " + (end-start) + " mills");		
	}

	
	/*private static void reloadData() throws DataConfException,
			UnexpectedException, NotSupportedException {
		logger_.info("try reload conf...");
		
		ResourceInst.getInstance().reloadResource();
		
		MultiValueMap rtns = OntoXmlReader.loadOnto(
				Util.readXMLFile(Param.ALL_ONTO_FILE, false),
				Util.readXMLFile(Param.ALL_ONTO_CONDITION_FILE, false));
		MultiValueMap rtnsold = OntoXmlReaderOldSystem.loadOnto(
				Util.readXMLFile(Param.ALL_ONTO_FILE_OLD_SYSTEM, false),
				Util.readXMLFile(Param.ALL_ONTO_CONDITION_FILE, false));
		MemOnto.init();
		MemOnto.reload(rtns, rtnsold);
		
		IndexGroupMap indexGroupMap = PhraseXmlReaderIndexGroup.loadIndexGroupMap(Util.readXMLFile(
				Param.ALL_PHRASE_INDEXGROUP, false));
		KeywordGroupMap keywordGroupMap = PhraseXmlReaderKeywordGroup.loadKeywordGroupMap(Util.readXMLFile(
				Param.ALL_PHRASE_KEYWORDGROUP, false));
		SyntacticPatternMap syntacticPatternMap = PhraseXmlReaderSyntacticPattern.loadSyntacticPatternMap(Util.readXMLFile(
				Param.ALL_PHRASE_SYNTACTIC, false));
		SemanticPatternMap semanticPatternMap = PhraseXmlReaderSemanticPattern.loadSemanticPatternMap(Util.readXMLFile(
				Param.ALL_PHRASE_SEMANTIC, false));
		HashMap<String, ArrayList<WordsInfo>> wordsInfoMap = PhraseWordsMapBuilder.build(indexGroupMap, keywordGroupMap, syntacticPatternMap, semanticPatternMap);
		PhraseInfo.init();
		PhraseInfo.reload(indexGroupMap, keywordGroupMap, syntacticPatternMap, semanticPatternMap, wordsInfoMap);
		
		ArrayList<ThematicMsg> thematicMsgs = ThematicConfInfo.loadXML(Util.readXMLFile(Param.STOCK_THEMATIC));
		ThematicConfInfo.init();
		ThematicConfInfo.reload(thematicMsgs);
		
		List<String> lightParserTypes = LightParserTypeXMLReader.loadLightParserTypes(Util.readXMLFile(
        		Param.LIGHTPARSER_TYPE, false));
		HashMap<String, LightParserType> lightParserTypeMap = LightParserTypeXMLReader.loadLightParserTypeMap(Util.readXMLFile(
        		Param.LIGHTPARSER_TYPE, false));
		LightParserTypeConf.init();
		LightParserTypeConf.reload(lightParserTypes, lightParserTypeMap);
		
		StopWords.init();
		NoParserMatch.init();
		HashSet<String> stopWords = StopWords.loadDict();
		StopWords.reload(stopWords);
		if(OnlineIpInfo.isOnlineIp()){//线上加载,线下不加载
			TrieTree noParserTrieTree = NoParserMatch.loadTrieTreeOfNoParser();
			TrieTree noParserWellTrieTree = NoParserMatch.loadTrieTreeOfNoParserWell();
			NoParserMatch.reload(noParserTrieTree, noParserWellTrieTree);
		}
		
		DateNumModify.reloadData();
		RemoveSomeConditionNode.reloadDate();
		Traditional2simplified.reloadData();
		IndexInteraction.reloadData();
		
		logger_.info("conf reloaded...");
	}*/
}
