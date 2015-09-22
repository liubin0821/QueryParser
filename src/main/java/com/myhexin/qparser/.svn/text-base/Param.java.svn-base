package com.myhexin.qparser;

import com.myhexin.qparser.define.EnumDef.Unit;

public class Param {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Param.class.getSimpleName());

	public static boolean DEBUG = false;
	public static boolean DEBUG_DATE = false;
	public static boolean DEBUG_NUM = false;
	public static boolean DEBUG_DATEINFO = false;
	public static boolean DEBUG_WASTE = false;
	public static boolean DEBUG_SUGGEST = false;
	public static boolean DEBUG_TB = false;
	public static boolean DEBUG_COMPILE = false;
	public static boolean DEBUG_WEBCOMPILE = false;
	public static boolean DEBUG_FUZZY = false;
	public static boolean DEBUG_WEBDATA = false;
	public static boolean DEBUG_EXTRA_INFO = false;
	public static boolean DEBUG_MERGE = false;

	/** 是否使用实体识别 */
	public static boolean USE_ENTITY_IDENT = false;
	public static boolean USE_FUZZY_SEARCH = false;

	public static boolean FOR_IFIND_SERVER = true;
	public static boolean FOR_IFIND_CLIENT = false;

	public static boolean SUPPORT_FUND = false;
	public static boolean SUPPORT_STOCK = true;

	public static String MULTI_SEGGER_URL = null;
	public static String ALL_SEGGER_URL = null;
	public static String MAINSEARCH_SEGGER_URL = null;
	public static String STOCK_SEGGER_URL = null;
	public static String FUND_SEGGER_URL = null;
	public static String DYNAMIC_SERV_URL = null;
	public static String GET_QUERY_CLASSIFY_URL_OFFLINE = "";
	public static String GET_QUERY_CLASSIFY_URL_ONLINE = "";
	//private static String GET_QUERY_CLASSIFY_URL_ONLINE_2 = "";
	//public static String GET_QUERY_CLASSIFY_URL_ONLINE_3 = "";

	public static String CHUNK_SWITCH = null;
	public static String CRF_ENV_ADDRESS = null;

	public static int PER_HOST_CONN_COUNT = 10240;
	public static int TOTAL_CONN_COUNT = 102400;
	public static int CREATE_CONN_TIME_OUT = 50;
	public static int READ_CONTENT_TIME_OUT = 100;
	public static int MAX_CONNECT_TIMES = 5;
	public static int READ_CONTENT_TIME_OUT_DYNAMIC_RLT = 100;
	
	public static final boolean USE_DYNAMIC_LTP_MIXED = false;
	/** crf min acceptable prob */
	public static double CRF_MIN_PROB_PARSER = 0.8;
	public static double CRF_MIN_PROB = 0.45;
	public static int NEW_PARSER_USED_MIN_SCORE = 0;
	public static int PARSED_MAX_CHARS = 50;

	/** used to link tdb in fuzzy search */
	public static String JOSEKI_URL = null;
	public static String IFIND_SUGGEST_URL = null;
	public static String IFIND_SUGGEST_SEG_URL = null;
	public static String IFIND_SUGGEST_TEMP_URL = null;

	public static double ONTO_PROD_SCORE = 0.5;
	public static double ONTO_UNPROD_SCORE = 0.2;
	public static String ONTO_SPARQL_LIMIT = "3000";
	public static double YUAN_2_CLOSING_STANDARD = 500.0;
	public static double GU_2_TOTAL_STOCK_ISSUE_STANDARD = 10000.0;

	/** 默认浮动范围 */
	public static final double MOVE_VAL = 5;
	public static final Unit MOVE_UNIT = Unit.PERCENT;

	public static boolean ALL_MULTRESULT = false;

	public static int INDEX_INTERACT_MIN_SCORE = 0;

	public static int INDEX_INTERACT_MAX_SCORE = 100;
	
	public static int MULTRESULT_ONE_INDEX_MAX_NUM = -1; 
	public static int MULTRESULT_ONE_SYNTACTIC_MAX_NUM = -1; 
	public static int MULTRESULT_ONE_QUERY_MAX_NUM = -1; 
	public static int MULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM = -1;
	public static int MULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM = -1;

	/**
	 * 所有配置文件均相对以下路径 !!!!!重要!!!!!!!!重要!!!!!!!!!!重要!!!!!!!!!重要!!!!!!!!
	 * 每当添加一个文件名，都要在{@link #addDataRoot_}中添加相应的 操作，否则无法添加data root前缀，从而导致找不到文件
	 */
	public static String DATA_ROOT = "./data";
	public static String IFIND_INDEX_FILE = "ifind_index.txt";
	public static String SPECIAL_WORDS_FILE = "special_words.txt";
	public static String CHANGEABLE_INDEX_FILE = "changeable_index.txt";
	public static String OPERATOR_2_INDEX = "op_2_index.txt";
	public static String RELATED_INDEX_FILE = "ifind_related_index.txt";
	public static String VAGUE_NUM_INFO_FOR_OPER_FILE = "vague_num_info_for_oper.txt";
	public static String FAKE_NUM_DEF_INFO_FILE = "fake_num_def_info.txt";
	/** used in fuzzy search */
	public static String FUZZY_MODEL_PROD_L2 = "l2_fuzzy_production_model.txt";
	/** used in fuzzy search */
	public static String FUZZY_MODEL_PROD_L1 = "l1_fuzzy_production_model.txt";
	/** used in package suggest, TrieTree */
	public static String ALL_PIN_YIN = "allpinyin.txt";
	/** used in package suggest, WebQuerySuggest */
	public static String UNICOD_TO_PINYIN = "unicode2pinyin.txt";
	/** 智能回答使用的匹配文件名 */
	public static String SMART_ANSWER_FILE = "smart_answer.xml";
	/** used in package suggest, WebQuerySuggest */
	public static String SUGGEST_TEMP_FILE = "web_tempsentence.txt";
	public static String MSG_DEF_FILE = "msg_define.txt";
	
	// ================ ifind相关的数据文件 ==============
	/** used in package suggest, WebQuerySuggest */
	public static String INDEX_TECHOP_MAPPING = "stock/index_techop_mapping.txt";
	public static String ALL_ONTO_FILE = "stock/stock_onto.xml";
	public static String ALL_ONTO_FILE_OLD_SYSTEM = "stock/stock_onto_old_system.xml";
	public static String ALL_ONTO_CONDITION_FILE = "stock/stock_onto_condition.xml";
	public static String STOCK_INDEX_FILE = "stock/stock_index.xml";
	public static String ALL_PHRASE_FILE = "stock/stock_phrase.xml";
	public static String ALL_PHRASE_INDEXGROUP = "stock/stock_phrase_indexgroup.xml";
	public static String ALL_PHRASE_KEYWORDGROUP = "stock/stock_phrase_keywordgroup.xml";
	public static String ALL_PHRASE_SYNTACTIC = "stock/stock_phrase_syntactic.xml";
	public static String ALL_PHRASE_SEMANTIC = "stock/stock_phrase_semantic.xml";

	public static String STOCK_ONTO_FILE = null;
	public static String STOCK_ONTO_CONDITION_FILE = null;
	public static String STOCK_PHRASE_FILE = null;

	public static String STOCK_THEMATIC = "stock/stock_thematic.xml";
	public static String LIGHTPARSER_TYPE = "stock/lightparser_type.xml";

	public static String STOCK_PATTERN_FILE = "stock/stock_pattern_rule.txt";
	public static String STOCK_RULE_FILE = "stock/stock_index_rule.xml";
	public static String STOCK_DEF_VAL_FILE = "stock/stock_def_value.xml";
	public static String STOCK_DEF_OP_FILE = "stock/stock_tech_def_op.xml";
	public static String STOCK_INDEX_GROUP_FILE = "stock/stock_index_group.xml";
	public static String STOCK_USER_INDEX_FILE = "stock/stock_user_index.xml";
	public static String FUND_ONTO_FILE = "fund/fund_onto.xml";
	public static String FUND_INDEX_FILE = "fund/fund_index.xml";
	public static String FUND_PATTERN_FILE = "fund/fund_pattern_rule.txt";
	public static String FUND_RULE_FILE = "fund/fund_index_rule.xml";
	public static String FUND_USER_INDEX_FILE = "fund/fund_user_index.xml";
	public static String FUND_CHANGE_INFO_FILE = "fund/fund_change.xml";
	public static String IFIND_INDEX_IN_COMMON_USE = "index_in_common_use.txt";
	public static String IFIND_PARAM_FILE = "ifind_param.xml";
	public static String IFIND_TRUMP_CARD_FILE = "ifind_trump_cards.txt";
	public static String STOCK_DEFAULT_PROP_FILE = "ifind_default_prop.xml";

	public static String ADD_CONDS_FILE = "addcond_info.xml";
	public static String PLUGIN_DETAIL = "plugin_detail.xml";
	
	//添加转回测的时候添加的配置文件
	public static String CHANGE_TO_NEW_DATABASE_INDEX = "ChangeToNewDatabaseIndex.txt";
	
	//报告期配置文件,临时使用,后面会添加到数据库中去
	public static String TEMP_REPORT_PERIOD_CFG_DATE = "temp_cfg_date.txt";
	
	// ================ 以下使用XML读写通用工具 =============
	// 吴永行 2013.10.29


	public static String INDEX_PRO_CATEGORY = "data/stock/stock_index_pro_category.xml";
	public static String CATEGORY_ARRAY_ID = "category_array";
	// end

	// ================ 行情技术指标的配置文件 =============
	public static String TECH_INFO_FILE = "tech_info.txt";

	// ================ 以下配置应当不再使用 =============
	public static String IFIND_SUGGEST_INDICATOR_INDEX_DIR = "";
	public static String IFIND_SUGGEST_INDICATOR_INDEX_DOC = "";
	public static String DATE_INFO_FILE = "unit_test";

	public static String[] PRETREAT_PLUGINS = null;
	public static String[] PLUGINS = null;

	// ================ 以下单元测试使用 =============
	public static String UNIT_TEST_DIR = "unit_test";

	// 模板
	public static String TEMPLATES_PATH = "/templates";
	public static String MACRO_INDUSTRY_PARSER_TEMPLATES_PATH = "/templates/mi";
	public static String LIGHT_PARSER_TEMPLATES_PATH = "/templates/lightparser";
	
	//2014.05.13  李科 多种可能型配置文件
	
	public static boolean SUPPORT_IFUND = true;
	
	private static boolean addedRoot=false;
	
	
	public static String DATETIME_MERGE_CONFIG = "datetime_merge.txt";
	
	
	//相似问句相关URL
	public static String stock_segger_url;
	public static String fund_segger_url;
	public static String search_segger_url;
	public static String hkstock_segger_url;

	//configurl
	public static String http_config_file;
	
	private static void addDataRoot_() {
		//spring加载 调用初始化函数时可能调用多次,  限制addDataRoot_只能被调用一次
		if(addedRoot)
			return;
		
		if (!DATA_ROOT.endsWith("/")) {
			DATA_ROOT += "/";
		}
		IFIND_INDEX_FILE = DATA_ROOT + IFIND_INDEX_FILE;
		STOCK_INDEX_FILE = DATA_ROOT + STOCK_INDEX_FILE;
		ALL_PHRASE_FILE = DATA_ROOT + ALL_PHRASE_FILE;
		ALL_ONTO_FILE = DATA_ROOT + ALL_ONTO_FILE;
		ALL_ONTO_FILE_OLD_SYSTEM = DATA_ROOT + ALL_ONTO_FILE_OLD_SYSTEM;
		ALL_ONTO_CONDITION_FILE = DATA_ROOT + ALL_ONTO_CONDITION_FILE;
		STOCK_USER_INDEX_FILE = DATA_ROOT + STOCK_USER_INDEX_FILE;
		ALL_PHRASE_INDEXGROUP = DATA_ROOT + ALL_PHRASE_INDEXGROUP;
		ALL_PHRASE_KEYWORDGROUP = DATA_ROOT + ALL_PHRASE_KEYWORDGROUP;
		ALL_PHRASE_SYNTACTIC = DATA_ROOT + ALL_PHRASE_SYNTACTIC;
		ALL_PHRASE_SEMANTIC = DATA_ROOT + ALL_PHRASE_SEMANTIC;
		STOCK_THEMATIC = DATA_ROOT + STOCK_THEMATIC;
		LIGHTPARSER_TYPE = DATA_ROOT + LIGHTPARSER_TYPE;

		STOCK_DEF_VAL_FILE = DATA_ROOT + STOCK_DEF_VAL_FILE;
		STOCK_DEF_OP_FILE = DATA_ROOT + STOCK_DEF_OP_FILE;
		FUND_ONTO_FILE = DATA_ROOT + FUND_ONTO_FILE;
		FUND_INDEX_FILE = DATA_ROOT + FUND_INDEX_FILE;
		FUND_RULE_FILE = DATA_ROOT + FUND_RULE_FILE;
		FUND_USER_INDEX_FILE = DATA_ROOT + FUND_USER_INDEX_FILE;
		FUND_CHANGE_INFO_FILE = DATA_ROOT + FUND_CHANGE_INFO_FILE;
		STOCK_PATTERN_FILE = DATA_ROOT + STOCK_PATTERN_FILE;
		STOCK_RULE_FILE = DATA_ROOT + STOCK_RULE_FILE;
		SPECIAL_WORDS_FILE = DATA_ROOT + SPECIAL_WORDS_FILE;
		RELATED_INDEX_FILE = DATA_ROOT + RELATED_INDEX_FILE;
		FUZZY_MODEL_PROD_L2 = DATA_ROOT + FUZZY_MODEL_PROD_L2;
		FUZZY_MODEL_PROD_L1 = DATA_ROOT + FUZZY_MODEL_PROD_L1;
		ALL_PIN_YIN = DATA_ROOT + ALL_PIN_YIN;
		UNICOD_TO_PINYIN = DATA_ROOT + UNICOD_TO_PINYIN;
		SMART_ANSWER_FILE = DATA_ROOT + SMART_ANSWER_FILE;
		SUGGEST_TEMP_FILE = DATA_ROOT + SUGGEST_TEMP_FILE;
		IFIND_INDEX_IN_COMMON_USE = DATA_ROOT + IFIND_INDEX_IN_COMMON_USE;
		IFIND_PARAM_FILE = DATA_ROOT + IFIND_PARAM_FILE;
		IFIND_TRUMP_CARD_FILE = DATA_ROOT + IFIND_TRUMP_CARD_FILE;
		STOCK_INDEX_GROUP_FILE = DATA_ROOT + STOCK_INDEX_GROUP_FILE;
		STOCK_DEFAULT_PROP_FILE = DATA_ROOT + STOCK_DEFAULT_PROP_FILE;
		OPERATOR_2_INDEX = DATA_ROOT + OPERATOR_2_INDEX;
		VAGUE_NUM_INFO_FOR_OPER_FILE = DATA_ROOT + VAGUE_NUM_INFO_FOR_OPER_FILE;
		FAKE_NUM_DEF_INFO_FILE = DATA_ROOT + FAKE_NUM_DEF_INFO_FILE;
		TECH_INFO_FILE = DATA_ROOT + TECH_INFO_FILE;
		ADD_CONDS_FILE = DATA_ROOT + ADD_CONDS_FILE;
		MSG_DEF_FILE = DATA_ROOT + MSG_DEF_FILE;
		INDEX_TECHOP_MAPPING = DATA_ROOT + INDEX_TECHOP_MAPPING;
		
		CHANGE_TO_NEW_DATABASE_INDEX = DATA_ROOT + CHANGE_TO_NEW_DATABASE_INDEX;
		TEMP_REPORT_PERIOD_CFG_DATE = DATA_ROOT + TEMP_REPORT_PERIOD_CFG_DATE;
		PLUGIN_DETAIL = DATA_ROOT + PLUGIN_DETAIL;
		DATETIME_MERGE_CONFIG = DATA_ROOT + DATETIME_MERGE_CONFIG;
		// 模板
		addedRoot=true;
	}

	/**
	 * 主搜索redis地址
	 */
  private static String MAINSEARCH_EVENTS_REDIS_HOST;
  private static String MAINSEARCH_EVENTS_REDIS_PORT;
  private static String MAINSEARCH_EVENTS_REDIS_DB;
	
	/**
	 * @author		徐祥
	 * @createTime	2014-05-04 19:35
	 * @description	在spring设置各属性值以后，初始化参数
	 */
	private static void initParams() {

		if (FOR_IFIND_CLIENT && FOR_IFIND_SERVER) {
			logger_.error("for_ifind_client & for_ifind_server cannot be both true");
		} else if (!FOR_IFIND_CLIENT && !FOR_IFIND_SERVER) {
			logger_.error("for_ifind_client & for_ifind_server cannot be both false");
		}

		if (!SUPPORT_FUND && !SUPPORT_STOCK) {
			logger_.error("support_stock & support_fund cannot be both false");
		}
		
		addDataRoot_();
	}
	
	// 为sping定义的set方法
	public void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}
	
	public void setDEBUG_MERGE(boolean dEBUG_MERGE) {
		DEBUG_MERGE = dEBUG_MERGE;
	}

	public void setDEBUG_DATE(boolean dEBUG_DATE) {
		DEBUG_DATE = dEBUG_DATE;
	}
	public static String getHttp_config_file() {
		return http_config_file;
	}

	public static void setHttp_config_file(String http_config_file) {
		Param.http_config_file = http_config_file;
	}

	public void setDEBUG_NUM(boolean dEBUG_NUM) {
		DEBUG_NUM = dEBUG_NUM;
	}

	public void setDEBUG_DATEINFO(boolean dEBUG_DATEINFO) {
		DEBUG_DATEINFO = dEBUG_DATEINFO;
	}

	public void setDEBUG_WASTE(boolean dEBUG_WASTE) {
		DEBUG_WASTE = dEBUG_WASTE;
	}

	public void setDEBUG_SUGGEST(boolean dEBUG_SUGGEST) {
		DEBUG_SUGGEST = dEBUG_SUGGEST;
	}

	public void setDEBUG_TB(boolean dEBUG_TB) {
		DEBUG_TB = dEBUG_TB;
	}

	public void setDEBUG_COMPILE(boolean dEBUG_COMPILE) {
		DEBUG_COMPILE = dEBUG_COMPILE;
	}

	public void setDEBUG_WEBCOMPILE(boolean dEBUG_WEBCOMPILE) {
		DEBUG_WEBCOMPILE = dEBUG_WEBCOMPILE;
	}

	public void setDEBUG_FUZZY(boolean dEBUG_FUZZY) {
		DEBUG_FUZZY = dEBUG_FUZZY;
	}

	public void setDEBUG_WEBDATA(boolean dEBUG_WEBDATA) {
		DEBUG_WEBDATA = dEBUG_WEBDATA;
	}

	public void setDEBUG_EXTRA_INFO(boolean dEBUG_EXTRA_INFO) {
		DEBUG_EXTRA_INFO = dEBUG_EXTRA_INFO;
	}

	public void setUSE_ENTITY_IDENT(boolean uSE_ENTITY_IDENT) {
		USE_ENTITY_IDENT = uSE_ENTITY_IDENT;
	}

	public void setUSE_FUZZY_SEARCH(boolean uSE_FUZZY_SEARCH) {
		USE_FUZZY_SEARCH = uSE_FUZZY_SEARCH;
	}

	public void setFOR_IFIND_SERVER(boolean fOR_IFIND_SERVER) {
		FOR_IFIND_SERVER = fOR_IFIND_SERVER;
	}

	public void setFOR_IFIND_CLIENT(boolean fOR_IFIND_CLIENT) {
		FOR_IFIND_CLIENT = fOR_IFIND_CLIENT;
	}

	public void setSUPPORT_FUND(boolean sUPPORT_FUND) {
		SUPPORT_FUND = sUPPORT_FUND;
	}

	public void setSUPPORT_STOCK(boolean sUPPORT_STOCK) {
		SUPPORT_STOCK = sUPPORT_STOCK;
	}

	public void setMULTI_SEGGER_URL(String mULTI_SEGGER_URL) {
		MULTI_SEGGER_URL = mULTI_SEGGER_URL;
	}

	public void setALL_SEGGER_URL(String aLL_SEGGER_URL) {
		ALL_SEGGER_URL = aLL_SEGGER_URL;
	}
	
	public void setMAINSEARCH_SEGGER_URL(String mAINSEARCH_SEGGER_URL) {
	  MAINSEARCH_SEGGER_URL = mAINSEARCH_SEGGER_URL;
  }

	public void setSTOCK_SEGGER_URL(String sTOCK_SEGGER_URL) {
		STOCK_SEGGER_URL = sTOCK_SEGGER_URL;
	}

	public void setFUND_SEGGER_URL(String fUND_SEGGER_URL) {
		FUND_SEGGER_URL = fUND_SEGGER_URL;
	}

	public void setDYNAMIC_SERV_URL(String dYNAMIC_SERV_URL) {
		DYNAMIC_SERV_URL = dYNAMIC_SERV_URL;
	}

	public void setGET_QUERY_CLASSIFY_URL_OFFLINE(
			String gET_QUERY_CLASSIFY_URL_OFFLINE) {
		GET_QUERY_CLASSIFY_URL_OFFLINE = gET_QUERY_CLASSIFY_URL_OFFLINE;
	}

	public void setGET_QUERY_CLASSIFY_URL_ONLINE(
			String gET_QUERY_CLASSIFY_URL_ONLINE) {
		GET_QUERY_CLASSIFY_URL_ONLINE = gET_QUERY_CLASSIFY_URL_ONLINE;
	}

	/*public void setGET_QUERY_CLASSIFY_URL_ONLINE_2(
			String gET_QUERY_CLASSIFY_URL_ONLINE_2) {
		GET_QUERY_CLASSIFY_URL_ONLINE_2 = gET_QUERY_CLASSIFY_URL_ONLINE_2;
	}

	public void setGET_QUERY_CLASSIFY_URL_ONLINE_3(
			String gET_QUERY_CLASSIFY_URL_ONLINE_3) {
		GET_QUERY_CLASSIFY_URL_ONLINE_3 = gET_QUERY_CLASSIFY_URL_ONLINE_3;
	}*/

	public void setCHUNK_SWITCH(String cHUNK_SWITCH) {
		CHUNK_SWITCH = cHUNK_SWITCH;
	}


	public void setCRF_ENV_ADDRESS(String cRF_ENV_ADDRESS) {
		CRF_ENV_ADDRESS = cRF_ENV_ADDRESS;
	}

	public void setPER_HOST_CONN_COUNT(int pER_HOST_CONN_COUNT) {
		PER_HOST_CONN_COUNT = pER_HOST_CONN_COUNT;
	}

	public void setTOTAL_CONN_COUNT(int tOTAL_CONN_COUNT) {
		TOTAL_CONN_COUNT = tOTAL_CONN_COUNT;
	}

	public void setCREATE_CONN_TIME_OUT(int cREATE_CONN_TIME_OUT) {
		CREATE_CONN_TIME_OUT = cREATE_CONN_TIME_OUT;
	}

	public void setREAD_CONTENT_TIME_OUT(int rEAD_CONTENT_TIME_OUT) {
		READ_CONTENT_TIME_OUT = rEAD_CONTENT_TIME_OUT;
	}

	public void setMAX_CONNECT_TIMES(int mAX_CONNECT_TIMES) {
		MAX_CONNECT_TIMES = mAX_CONNECT_TIMES;
	}

	public void setREAD_CONTENT_TIME_OUT_DYNAMIC_RLT(
			int rEAD_CONTENT_TIME_OUT_DYNAMIC_RLT) {
		READ_CONTENT_TIME_OUT_DYNAMIC_RLT = rEAD_CONTENT_TIME_OUT_DYNAMIC_RLT;
	}

	public void setCRF_MIN_PROB_PARSER(double cRF_MIN_PROB_PARSER) {
		CRF_MIN_PROB_PARSER = cRF_MIN_PROB_PARSER;
	}

	public void setCRF_MIN_PROB(double cRF_MIN_PROB) {
		CRF_MIN_PROB = cRF_MIN_PROB;
	}

	public void setNEW_PARSER_USED_MIN_SCORE(int nEW_PARSER_USED_MIN_SCORE) {
		NEW_PARSER_USED_MIN_SCORE = nEW_PARSER_USED_MIN_SCORE;
	}

	public void setPARSED_MAX_CHARS(int pARSED_MAX_CHARS) {
		PARSED_MAX_CHARS = pARSED_MAX_CHARS;
	}

	public void setJOSEKI_URL(String jOSEKI_URL) {
		JOSEKI_URL = jOSEKI_URL;
	}

	public void setIFIND_SUGGEST_URL(String iFIND_SUGGEST_URL) {
		IFIND_SUGGEST_URL = iFIND_SUGGEST_URL;
	}

	public void setIFIND_SUGGEST_SEG_URL(String iFIND_SUGGEST_SEG_URL) {
		IFIND_SUGGEST_SEG_URL = iFIND_SUGGEST_SEG_URL;
	}

	public void setIFIND_SUGGEST_TEMP_URL(String iFIND_SUGGEST_TEMP_URL) {
		IFIND_SUGGEST_TEMP_URL = iFIND_SUGGEST_TEMP_URL;
	}

	public void setONTO_PROD_SCORE(double oNTO_PROD_SCORE) {
		ONTO_PROD_SCORE = oNTO_PROD_SCORE;
	}

	public void setONTO_UNPROD_SCORE(double oNTO_UNPROD_SCORE) {
		ONTO_UNPROD_SCORE = oNTO_UNPROD_SCORE;
	}

	public void setONTO_SPARQL_LIMIT(String oNTO_SPARQL_LIMIT) {
		ONTO_SPARQL_LIMIT = oNTO_SPARQL_LIMIT;
	}

	public void setYUAN_2_CLOSING_STANDARD(double yUAN_2_CLOSING_STANDARD) {
		YUAN_2_CLOSING_STANDARD = yUAN_2_CLOSING_STANDARD;
	}

	public void setGU_2_TOTAL_STOCK_ISSUE_STANDARD(
			double gU_2_TOTAL_STOCK_ISSUE_STANDARD) {
		GU_2_TOTAL_STOCK_ISSUE_STANDARD = gU_2_TOTAL_STOCK_ISSUE_STANDARD;
	}

	public void setINDEX_INTERACT_MIN_SCORE(int iNDEX_INTERACT_MIN_SCORE) {
		INDEX_INTERACT_MIN_SCORE = iNDEX_INTERACT_MIN_SCORE;
	}

	public void setINDEX_INTERACT_MAX_SCORE(int iNDEX_INTERACT_MAX_SCORE) {
		INDEX_INTERACT_MAX_SCORE = iNDEX_INTERACT_MAX_SCORE;
	}

	public void setDATA_ROOT(String dATA_ROOT) {
		DATA_ROOT = dATA_ROOT;
	}

	public void setIFIND_INDEX_FILE(String iFIND_INDEX_FILE) {
		IFIND_INDEX_FILE = iFIND_INDEX_FILE;
	}

	public void setSPECIAL_WORDS_FILE(String sPECIAL_WORDS_FILE) {
		SPECIAL_WORDS_FILE = sPECIAL_WORDS_FILE;
	}

	public void setCHANGEABLE_INDEX_FILE(String cHANGEABLE_INDEX_FILE) {
		CHANGEABLE_INDEX_FILE = cHANGEABLE_INDEX_FILE;
	}

	public void setOPERATOR_2_INDEX(String oPERATOR_2_INDEX) {
		OPERATOR_2_INDEX = oPERATOR_2_INDEX;
	}


	public void setRELATED_INDEX_FILE(String rELATED_INDEX_FILE) {
		RELATED_INDEX_FILE = rELATED_INDEX_FILE;
	}

	public void setVAGUE_NUM_INFO_FOR_OPER_FILE(
			String vAGUE_NUM_INFO_FOR_OPER_FILE) {
		VAGUE_NUM_INFO_FOR_OPER_FILE = vAGUE_NUM_INFO_FOR_OPER_FILE;
	}

	public void setFAKE_NUM_DEF_INFO_FILE(String fAKE_NUM_DEF_INFO_FILE) {
		FAKE_NUM_DEF_INFO_FILE = fAKE_NUM_DEF_INFO_FILE;
	}

	public void setFUZZY_MODEL_PROD_L2(String fUZZY_MODEL_PROD_L2) {
		FUZZY_MODEL_PROD_L2 = fUZZY_MODEL_PROD_L2;
	}

	public void setFUZZY_MODEL_PROD_L1(String fUZZY_MODEL_PROD_L1) {
		FUZZY_MODEL_PROD_L1 = fUZZY_MODEL_PROD_L1;
	}

	public void setALL_PIN_YIN(String aLL_PIN_YIN) {
		ALL_PIN_YIN = aLL_PIN_YIN;
	}

	public void setUNICOD_TO_PINYIN(String uNICOD_TO_PINYIN) {
		UNICOD_TO_PINYIN = uNICOD_TO_PINYIN;
	}

	public void setSMART_ANSWER_FILE(String sMART_ANSWER_FILE) {
		SMART_ANSWER_FILE = sMART_ANSWER_FILE;
	}

	public void setSUGGEST_TEMP_FILE(String sUGGEST_TEMP_FILE) {
		SUGGEST_TEMP_FILE = sUGGEST_TEMP_FILE;
	}

	public void setMSG_DEF_FILE(String mSG_DEF_FILE) {
		MSG_DEF_FILE = mSG_DEF_FILE;
	}

	public void setINDEX_TECHOP_MAPPING(String iNDEX_TECHOP_MAPPING) {
		INDEX_TECHOP_MAPPING = iNDEX_TECHOP_MAPPING;
	}

	public void setALL_ONTO_FILE(String aLL_ONTO_FILE) {
		ALL_ONTO_FILE = aLL_ONTO_FILE;
	}

	public void setALL_ONTO_FILE_OLD_SYSTEM(String aLL_ONTO_FILE_OLD_SYSTEM) {
		ALL_ONTO_FILE_OLD_SYSTEM = aLL_ONTO_FILE_OLD_SYSTEM;
	}

	public void setALL_ONTO_CONDITION_FILE(String aLL_ONTO_CONDITION_FILE) {
		ALL_ONTO_CONDITION_FILE = aLL_ONTO_CONDITION_FILE;
	}

	public void setSTOCK_INDEX_FILE(String sTOCK_INDEX_FILE) {
		STOCK_INDEX_FILE = sTOCK_INDEX_FILE;
	}

	public void setALL_PHRASE_FILE(String aLL_PHRASE_FILE) {
		ALL_PHRASE_FILE = aLL_PHRASE_FILE;
	}

	public void setALL_PHRASE_INDEXGROUP(String aLL_PHRASE_INDEXGROUP) {
		ALL_PHRASE_INDEXGROUP = aLL_PHRASE_INDEXGROUP;
	}

	public void setALL_PHRASE_KEYWORDGROUP(String aLL_PHRASE_KEYWORDGROUP) {
		ALL_PHRASE_KEYWORDGROUP = aLL_PHRASE_KEYWORDGROUP;
	}

	public void setALL_PHRASE_SYNTACTIC(String aLL_PHRASE_SYNTACTIC) {
		ALL_PHRASE_SYNTACTIC = aLL_PHRASE_SYNTACTIC;
	}

	public void setALL_PHRASE_SEMANTIC(String aLL_PHRASE_SEMANTIC) {
		ALL_PHRASE_SEMANTIC = aLL_PHRASE_SEMANTIC;
	}

	public void setSTOCK_ONTO_FILE(String sTOCK_ONTO_FILE) {
		STOCK_ONTO_FILE = sTOCK_ONTO_FILE;
	}

	public void setSTOCK_ONTO_CONDITION_FILE(String sTOCK_ONTO_CONDITION_FILE) {
		STOCK_ONTO_CONDITION_FILE = sTOCK_ONTO_CONDITION_FILE;
	}

	public void setSTOCK_PHRASE_FILE(String sTOCK_PHRASE_FILE) {
		STOCK_PHRASE_FILE = sTOCK_PHRASE_FILE;
	}

	public void setSTOCK_THEMATIC(String sTOCK_THEMATIC) {
		STOCK_THEMATIC = sTOCK_THEMATIC;
	}

	public void setLIGHTPARSER_TYPE(String lIGHTPARSER_TYPE) {
		LIGHTPARSER_TYPE = lIGHTPARSER_TYPE;
	}

	public void setSTOCK_PATTERN_FILE(String sTOCK_PATTERN_FILE) {
		STOCK_PATTERN_FILE = sTOCK_PATTERN_FILE;
	}

	public void setSTOCK_RULE_FILE(String sTOCK_RULE_FILE) {
		STOCK_RULE_FILE = sTOCK_RULE_FILE;
	}

	public void setSTOCK_DEF_VAL_FILE(String sTOCK_DEF_VAL_FILE) {
		STOCK_DEF_VAL_FILE = sTOCK_DEF_VAL_FILE;
	}

	public void setSTOCK_DEF_OP_FILE(String sTOCK_DEF_OP_FILE) {
		STOCK_DEF_OP_FILE = sTOCK_DEF_OP_FILE;
	}

	public void setSTOCK_INDEX_GROUP_FILE(String sTOCK_INDEX_GROUP_FILE) {
		STOCK_INDEX_GROUP_FILE = sTOCK_INDEX_GROUP_FILE;
	}

	public void setSTOCK_USER_INDEX_FILE(String sTOCK_USER_INDEX_FILE) {
		STOCK_USER_INDEX_FILE = sTOCK_USER_INDEX_FILE;
	}


	public void setFUND_ONTO_FILE(String fUND_ONTO_FILE) {
		FUND_ONTO_FILE = fUND_ONTO_FILE;
	}

	public void setFUND_INDEX_FILE(String fUND_INDEX_FILE) {
		FUND_INDEX_FILE = fUND_INDEX_FILE;
	}

	public void setFUND_PATTERN_FILE(String fUND_PATTERN_FILE) {
		FUND_PATTERN_FILE = fUND_PATTERN_FILE;
	}

	public void setFUND_RULE_FILE(String fUND_RULE_FILE) {
		FUND_RULE_FILE = fUND_RULE_FILE;
	}

	public void setFUND_USER_INDEX_FILE(String fUND_USER_INDEX_FILE) {
		FUND_USER_INDEX_FILE = fUND_USER_INDEX_FILE;
	}

	public void setFUND_CHANGE_INFO_FILE(String fUND_CHANGE_INFO_FILE) {
		FUND_CHANGE_INFO_FILE = fUND_CHANGE_INFO_FILE;
	}

	public void setIFIND_INDEX_IN_COMMON_USE(String iFIND_INDEX_IN_COMMON_USE) {
		IFIND_INDEX_IN_COMMON_USE = iFIND_INDEX_IN_COMMON_USE;
	}

	public void setIFIND_PARAM_FILE(String iFIND_PARAM_FILE) {
		IFIND_PARAM_FILE = iFIND_PARAM_FILE;
	}

	public void setIFIND_TRUMP_CARD_FILE(String iFIND_TRUMP_CARD_FILE) {
		IFIND_TRUMP_CARD_FILE = iFIND_TRUMP_CARD_FILE;
	}

	public void setSTOCK_DEFAULT_PROP_FILE(String sTOCK_DEFAULT_PROP_FILE) {
		STOCK_DEFAULT_PROP_FILE = sTOCK_DEFAULT_PROP_FILE;
	}

	public void setADD_CONDS_FILE(String aDD_CONDS_FILE) {
		ADD_CONDS_FILE = aDD_CONDS_FILE;
	}

	public void setINDEX_PRO_CATEGORY(String iNDEX_PRO_CATEGORY) {
		INDEX_PRO_CATEGORY = iNDEX_PRO_CATEGORY;
	}

	public void setCATEGORY_ARRAY_ID(String cATEGORY_ARRAY_ID) {
		CATEGORY_ARRAY_ID = cATEGORY_ARRAY_ID;
	}

	public void setTECH_INFO_FILE(String tECH_INFO_FILE) {
		TECH_INFO_FILE = tECH_INFO_FILE;
	}

	public void setIFIND_SUGGEST_INDICATOR_INDEX_DIR(
			String iFIND_SUGGEST_INDICATOR_INDEX_DIR) {
		IFIND_SUGGEST_INDICATOR_INDEX_DIR = iFIND_SUGGEST_INDICATOR_INDEX_DIR;
	}

	public void setIFIND_SUGGEST_INDICATOR_INDEX_DOC(
			String iFIND_SUGGEST_INDICATOR_INDEX_DOC) {
		IFIND_SUGGEST_INDICATOR_INDEX_DOC = iFIND_SUGGEST_INDICATOR_INDEX_DOC;
	}

	public void setDATE_INFO_FILE(String dATE_INFO_FILE) {
		DATE_INFO_FILE = dATE_INFO_FILE;
	}


	public void setPRETREAT_PLUGINS(String[] pRETREAT_PLUGINS) {
		PRETREAT_PLUGINS = pRETREAT_PLUGINS;
	}

	public void setPLUGINS(String[] pLUGINS) {
		PLUGINS = pLUGINS;
	}

	public void setUNIT_TEST_DIR(String uNIT_TEST_DIR) {
		UNIT_TEST_DIR = uNIT_TEST_DIR;
	}

	public void setTEMPLATES_PATH(String tEMPLATES_PATH) {
		TEMPLATES_PATH = tEMPLATES_PATH;
	}

	public void setMACRO_INDUSTRY_PARSER_TEMPLATES_PATH(
			String mACRO_INDUSTRY_PARSER_TEMPLATES_PATH) {
		MACRO_INDUSTRY_PARSER_TEMPLATES_PATH = mACRO_INDUSTRY_PARSER_TEMPLATES_PATH;
	}

	public void setLIGHT_PARSER_TEMPLATES_PATH(
			String lIGHT_PARSER_TEMPLATES_PATH) {
		LIGHT_PARSER_TEMPLATES_PATH = lIGHT_PARSER_TEMPLATES_PATH;
	}

	public void setSUPPORT_IFUND(boolean sUPPORT_IFUND) {
		SUPPORT_IFUND = sUPPORT_IFUND;
	}

	public void setALL_MULTRESULT(boolean aLL_MULTRESULT) {
    	ALL_MULTRESULT = aLL_MULTRESULT;
    }

	public void setMULTRESULT_ONE_INDEX_MAX_NUM(
            int mULTRESULT_ONE_INDEX_MAX_NUM) {
    	MULTRESULT_ONE_INDEX_MAX_NUM = mULTRESULT_ONE_INDEX_MAX_NUM;
    }

	public void setMULTRESULT_ONE_SYNTACTIC_MAX_NUM(
            int mULTRESULT_ONE_SYNTACTIC_MAX_NUM) {
    	MULTRESULT_ONE_SYNTACTIC_MAX_NUM = mULTRESULT_ONE_SYNTACTIC_MAX_NUM;
    }

	public void setMULTRESULT_ONE_QUERY_MAX_NUM(
            int mULTRESULT_ONE_QUERY_MAX_NUM) {
    	MULTRESULT_ONE_QUERY_MAX_NUM = mULTRESULT_ONE_QUERY_MAX_NUM;
    }

	public void setMULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM(
            int mULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM) {
		MULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM = mULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM;
    }
	
	public void setMULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM(
            int mULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM) {
		MULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM = mULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM;
    }

	public String getStock_segger_url() {
		return stock_segger_url;
	}

	public void setStock_segger_url(String stock_segger_url) {
		this.stock_segger_url = stock_segger_url;
	}

	public String getFund_segger_url() {
		return fund_segger_url;
	}

	public void setFund_segger_url(String fund_segger_url) {
		this.fund_segger_url = fund_segger_url;
	}

	public String getSearch_segger_url() {
		return search_segger_url;
	}

	public void setSearch_segger_url(String search_segger_url) {
		this.search_segger_url = search_segger_url;
	}

	public String getHkstock_segger_url() {
		return hkstock_segger_url;
	}

	public void setHkstock_segger_url(String hkstock_segger_url) {
		this.hkstock_segger_url = hkstock_segger_url;
	}

  public static String getMAINSEARCH_EVENTS_REDIS_HOST() {
    return MAINSEARCH_EVENTS_REDIS_HOST;
  }

  public static void setMAINSEARCH_EVENTS_REDIS_HOST(
      String mAINSEARCH_EVENTS_REDIS_HOST) {
    MAINSEARCH_EVENTS_REDIS_HOST = mAINSEARCH_EVENTS_REDIS_HOST;
  }

  public static String getMAINSEARCH_EVENTS_REDIS_PORT() {
    return MAINSEARCH_EVENTS_REDIS_PORT;
  }

  public static void setMAINSEARCH_EVENTS_REDIS_PORT(
      String mAINSEARCH_EVENTS_REDIS_PORT) {
    MAINSEARCH_EVENTS_REDIS_PORT = mAINSEARCH_EVENTS_REDIS_PORT;
  }

  public static String getMAINSEARCH_EVENTS_REDIS_DB() {
    return MAINSEARCH_EVENTS_REDIS_DB;
  }

  public static void setMAINSEARCH_EVENTS_REDIS_DB(
      String mAINSEARCH_EVENTS_REDIS_DB) {
    MAINSEARCH_EVENTS_REDIS_DB = mAINSEARCH_EVENTS_REDIS_DB;
  }
	
}
