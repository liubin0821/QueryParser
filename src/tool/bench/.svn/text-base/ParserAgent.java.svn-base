package bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedList;
import java.util.Set;


import bench.BenchQuery.QueryType;
import bench.BenchQuery.Status;
import bench.db.BenchDownLoder;
import bench.db.DBBenchManager;

import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.ClassNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.PropNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.tree.TreeNode;
import com.myhexin.qparser.util.StrStrPair;
import com.myhexin.qparser.util.Util;

/**
 * the Class ParserAgent
 *
 */
public class ParserAgent {
    /** */
    public static QueryParser queryParser;
    public static ArrayList<String> CN_STOP_WORDS_LIST = null ; 
    public static HashMap<String, String> PINYIN_TRANS_DICT = null ;
    private static final String  STOP_CN_WORDS_FILE_NAME_ = "/data/bench/stopCnWords.txt" ;
    private static final String PINYIN_FILE_NAME = "/data/bench/pinyin_trans.dict" ;
    /**
     * @rm.param parserConf
     */
    public static void init(String parserConf) {
    	if(queryParser == null){
    		queryParser = QueryParser.getParser(parserConf) ;
    	}
    	String conf = "/conf/qparser.conf" ;
    	String relativePath = parserConf.replace(conf, "") ;
    	if(loadStopCnWordsList(relativePath+STOP_CN_WORDS_FILE_NAME_) == true){
    		System.out.println("中文停用词构建成功");
    		System.out.println("the last stop word is " + 
    				CN_STOP_WORDS_LIST.get(CN_STOP_WORDS_LIST.size()-1));
    	} else System.out.println("中文停用词构建失败");
    	
    	if(loadPinyinTransDict(relativePath+PINYIN_FILE_NAME) == true){
    		System.out.println("pinyin trans file loaded success!!");
    	} else {
    		System.out.println("pinyin trans file loaded failed!!");
    	}
    	
    }
    
    public static boolean loadStopCnWordsList(String fileName){
    	if(CN_STOP_WORDS_LIST != null)
    		return true;
    	try {
			CN_STOP_WORDS_LIST = Util.readTxtFile(fileName) ;
		} catch (DataConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(CN_STOP_WORDS_LIST != null)
    		return true;
    	else return false ;
    }
    
    public static boolean loadPinyinTransDict(String fileName){
    	if(PINYIN_TRANS_DICT != null)
    		return true ;
    	
    	try{
    		PINYIN_TRANS_DICT = new HashMap<String, String>() ;
    		ArrayList<String> pinyinDict = Util.readTxtFile(fileName) ;
    		for(String s : pinyinDict){
    			if(s.contains("\t")){
    				String[] arr = s.split("\t") ;
    				PINYIN_TRANS_DICT.put(arr[0], arr[1]) ;
    			}
    		}
    	} catch (DataConfException e) {
			// TODO Auto-generated catch block
    		e.printStackTrace();
		} 
    	
    	if(PINYIN_TRANS_DICT != null)
    		return true;
    	else return false ;
    }
    
    /**
     * @rm.param benchQuery
     * @return Query
     */

    public static Query parse(BenchQuery benchQuery) {
    	if(queryParser == null){
    		throw new NullPointerException("没有初始化QueryParser");
    	}
        Query query = new Query(benchQuery.text);
        try{
        	queryParser.parse(query);
        }catch(Exception e){
        	query.getLog().logMsg(ParseLog.LOG_ERROR, "解析出错");
        }
       
        return query;
    }
    
    /**
     * 根据解析树query，依次为benchQuery各域解析赋值
	 * @rm.param query
	 * @rm.param benchQuery
     * @rm.param benchManager 
	 * @return 获取结果是否成功
	 */
	public static boolean parseBenchQueryFileds(Query query, BenchQuery benchQuery, BenchManager benchManager) {
//		System.err.println(query.text);
		benchQuery.unknowns = parseUnknowns(query);
		benchQuery.trans = parseTrans(query);
		benchQuery.patterns = parseSemanticPatterns(query);
		
		benchQuery.type = parseQueryType(query);
		String generalPtn = parseGeneralTree(query);
		benchQuery.generalPtn = generalPtn ;
		benchQuery.treeCode = benchManager.addTree(generalPtn);
//		System.out.println("generalPtn: " + generalPtn);
		String specificPtn = parseQueryPattern(query,benchQuery, benchManager);
		benchQuery.specificPtn = specificPtn ;
//		benchQuery.specificPtn = specificPtn + " STR_LEN:" + (benchQuery.text.length()-1)/3 ;
		
		benchQuery.specificPtnCode = benchManager.addPattern(benchQuery.specificPtn);

//		System.out.println("------------------");
//		System.out.println("specificPtn: " + specificPtn);
		if(query.hasFatalError()){
			benchQuery.treeCode = 0;
			benchQuery.status = Status.DEL;
		}
		return true;
	}
	
	/**
	 * 解析问句时，采用了哪些semantic pattern 进行替换，例如
	 * pattern:label=年报|中报|季报;sort str(公布|披露|发表|发布) label PTN=> group(1) rm.index(定期报告实际披露日期) group(3)
	 * @rm.param query
	 * @return trans
	 */
	public static String parseSemanticPatterns(Query query){
		return query.getLog().getMsg(ParseLog.LOG_PATTERN);
	}
	
	/**
	 * @rm.param query
	 * @return trans
	 */
	public static String parseTrans(Query query){
		boolean first = true;
		StringBuilder transBulder= new StringBuilder();
		ArrayList<StrStrPair> tranPairs = query.getLog().getTransWords();
		for(StrStrPair pair : tranPairs){
			if(!first){
				transBulder.append("\n");
			}
			transBulder.append(pair.first);
			transBulder.append(" ≌ ");
			transBulder.append(pair.second);
			first = false;
		}
		return transBulder.toString();
	}
	
	/**
	 * @rm.param query
	 * @return unknowns
	 */
	public static String parseUnknowns(Query query){
    	boolean first = true;
    	StringBuilder unknowns= new StringBuilder();
		ArrayList<String> terms = query.getLog().getSkipWords();
		for(String term : terms){
			if(!first){
				unknowns.append("\n");
			}
			unknowns.append(term);
			first = false;
		}
		return unknowns.toString();
	}
	
	/**
	 * 获取问句中全部指标，用“\n”隔开
	 * @rm.param query
	 * @return 指标
	 */
	public static String parseIndexs(Query query){
		StringBuilder indexBuilder = new StringBuilder();
		for(SemanticNode node : query.getNodes()){
			if(node.type == NodeType.CLASS){
				indexBuilder.append(node.text+"\n");
			}
		}
		return indexBuilder.toString().trim();
	}
	
	
	/**
	 * 获取问句中全部计算，用“\n”隔开
	 * @rm.param query
	 * @return 计算
	 */
	public static String parseOperators(Query query){
		StringBuilder indexBuilder = new StringBuilder();
		for(SemanticNode node : query.getNodes()){
			if(node.type == NodeType.OPERATOR){
				OperatorNode operNode = (OperatorNode)node;
				indexBuilder.append(operNode.operatorType+"("+node.text+")\n");
			}else if(node.type == NodeType.TECHOPERATOR){
				TechOpNode operNode = (TechOpNode)node;
				indexBuilder.append(operNode.type+"("+node.text+")\n");
			}
		}
		return indexBuilder.toString().trim();
	}
	
	/**
	 * 获取问句中时间，用“\n”隔开
	 * @rm.param query
	 * @return 时间
	 * @throws UnexpectedException 
	 */
	public static String parseDates(Query query) throws UnexpectedException{
		StringBuilder indexBuilder = new StringBuilder();
		for(SemanticNode node : query.getNodes()){
			if(node.type == NodeType.DATE){
				DateNode dateNode = (DateNode)node;
				String sequance = "单段时间";
				if(dateNode.isSequence){
					sequance = "连续时间";
				} else
					try {
						if(dateNode.hasSeveralDateCycle()){
							sequance = "多段时间";
						}
					} catch (UnexpectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				indexBuilder.append(dateNode.type+":"+sequance+"("+node.text+")\n");
			}
		}
		return indexBuilder.toString().trim();
	}
	
	/**
	 * 获取Query的泛化树结构
	 * @rm.param query
	 * @return 泛化树结构
	 */
	public static String parseGeneralTree(Query query){
		
		// 连续时间节点用单个时间替代
		ArrayList<TreeNode> treeNodes = replaceLogicNode(query.tree()) ;
		
		// 非连续时间LogicNode节点的展开
		treeNodes = tidyQuery(treeNodes);    // 展开逻辑节点
		if(treeNodes.size() == 0){
			return "问句解析出错";
		}
		
		/** 粗粒度泛化树，只对INST泛化,泛化成功后返回*/
		String tree = parseSimpleTree(treeNodes) ;
		if(tree != null){
			return tree;
		}
		
		/** query中没有INST节点的情况 */
		StringBuilder result = new StringBuilder();
		ArrayList<String> patts = new ArrayList<String>();
		for(int idx = 0; idx < treeNodes.size(); idx++){
			TreeNode currNode = treeNodes.get(idx);
			String treeI = parseTree(currNode, 0);
			
			if(treeI != null){
				treeI = treeI.trim();
				if(patts.contains(treeI)){
					continue ;
				}
				if(treeI.contains("\t")){
					patts.add(treeI);
				}else{
					for(String tmp : treeI.split("\n")){
						/** 此处去重 */
						if(patts.contains(tmp)){
							continue ;
						}else{
							patts.add(tmp);
						}
					}
				}
			}
		}
		Collections.sort(patts);
		for(String pat : patts){
			result.append(pat+"\n");
		}
		return result.toString().trim();
	}
	
	/**
	 * 细粒度泛化
	 * @rm.param query
	 * @return 泛化问句
	 */
	public static String parseQueryPattern(Query query , BenchQuery benchQuery, BenchManager benchMgr){
		if(query == null){
			return null;
		}
		/** 是不是最简单的指标查询问句 */
//		System.out.println("最简单的指标查询问句: " + subTree);
//		if(subTree!= null){
//			return subTree;
//		}
		StringBuilder textBuilder = new StringBuilder();
		String preText = "";
		for(int idx =0; idx < query.getNodes().size(); idx++){
			SemanticNode node = query.getNodes().get(idx);
			switch(node.type){
			case CLASS:
				preText = "";
				textBuilder.append(" INDEX:" + node.text);
				break;
			case PROP:
//				String prop = node.text.replace("_", "");
				String prop = node.text ;
				if(prop.contains("_"))
					prop = "TRIGGER" ;
				if(prop.matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
					prop = "行业概念";
				}
				if(!preText.equals(prop)){    //删除重复连续的prop
					textBuilder.append(" PROP:"+prop);
					preText = prop; 
				}
				break;
			case DATE:
				preText = "";
				DateNode dateNode = (DateNode)node;
				try {
					if(dateNode.isSequence)
						textBuilder.append(" DATE:连续" + dateNode.getUnitOfDate());
					else 
						textBuilder.append(" DATE:非连续" + dateNode.getUnitOfDate());
				} catch (UnexpectedException e) {
					e.printStackTrace();
				}
				break;
			case OPERATOR:
				preText = "";
				OperatorNode operNode = (OperatorNode)node;
				textBuilder.append(" OPER("+operNode.operatorType+")");
				break;
			case NUM:
				if(preText.equals("NUM")){
					continue;
				}
				preText = "NUM";
				NumNode numNode = (NumNode)node ;
				Unit unit = numNode.getUnit() ;
				if(unit != Unit.UNKNOWN)
					textBuilder.append(" "+node.type + "(" + unit + ")");
				else 
					textBuilder.append(" "+node.type);
				break;
			case STR_VAL:
				StrNode strNode = (StrNode)node;
				if(strNode.ofWhat != null){
					prop = strNode.ofWhat.get(0).text.replaceAll("_", "");
					if(prop.matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
						prop = "行业概念";
					}
					if(!preText.equals(prop)){
						if(prop.matches("股票代码|股票简称"))
							textBuilder.append(" STR_VAL:股票代码(简称)") ;
						else 
							textBuilder.append(" STR_VAL:" + prop);
						preText = prop; 
					}
				}else{
					textBuilder.append(" "+node.type);
				}
				break;
			case SORT:
				textBuilder.append(" "+node.type);
				break;
			case LOGIC:
				LogicNode logicNode = (LogicNode)node;
				if(logicNode.logicType == LogicType.OR){
					textBuilder.append(" LOGIC:"+node.text);
				}
				break;
			case TRIGGER:
				break ;
			case UNKNOWN:
				/** 
				 * 当UNKNOWN为真正无法识别的词时才添加。
				 * @note: 需要更深入了解UNKNOWN带来的信息 ，unknown的parse存在问题，暂不处理
				 * @author lu 
				 */
				UnknownNode unode = (UnknownNode)node ;
				
				//如果分词中未识别股票拼音在此处转换
				if(PINYIN_TRANS_DICT.containsKey(unode.text)){
					textBuilder.append(" STR_VAL:股票代码(简称)");
					break;
				}
				
				// query only has 2 semantic node ,first Type is NUM and second is UNKNOW 
				if (query.getNodes().size() == 2 && idx == 1
						&& query.getNodes().get(0).type == NodeType.NUM) {
					textBuilder.append(" UNKNOWN:" + unode.text);
					break;
				} else if (unode.text.trim().equals("")
						|| (ParserAgent.CN_STOP_WORDS_LIST != null && ParserAgent.CN_STOP_WORDS_LIST
								.contains(unode.text.trim())))
					break;
				else {
					textBuilder.append(" UNKNOWN:" + unode.text);
					break;
				}
			default:
				if(node.text.length() > 1 && !SpecialWords.hasWord(node.text,
                        SpecialWordType.TB_COMMON_SKIP)){
					textBuilder.append(" "+node.text);
				}
			}
		}
		String result = textBuilder.toString();
		result = result.replaceAll("INDEXS 时间 NUM", "时间 INDEXS NUM");
		result = result.replaceAll("INDEXS 连续时间 NUM", "连续时间 INDEXS NUM");
		result = result.replaceAll("( INDEXS){2,}", " INDEXS");
		result = result.replaceAll("( INDEXS NUM){2,}", " INDEXS NUM");
		result = result.replaceAll("( INDEXS NUM){2,}", " INDEXS NUM");
		result = result.replaceAll("( 时间 INDEXS NUM){2,}", " 时间 INDEXS NUM");
		result = result.replaceAll("( 连续时间 INDEXS NUM){2,}", " 连续时间 INDEXS NUM");		
		
		result =  parsePtnByRequest(result.trim(), benchMgr) ;    //按照产品的需求对pattern处理
//		result = parsePtnByTextLength(result, query, benchMgr) ;
		result = parseLength(result) ;
		result = parseTreeCode(result, benchQuery) ;
		return result.trim() ;
//		return result.trim() ;
	}
	
	/**
	 * 判定query属于哪种类型的type()
	 * @rm.param query
	 * @return QueryType
	 * last update: lu 
	 */
	public static QueryType parseQueryType(Query query){
		QueryType baseType = QueryType.UNKNOWN;
		if(query.getType() == Query.Type.FUND){
			baseType = QueryType.FUND;
		}else if(query.getType() == Query.Type.STOCK){
			baseType = QueryType.STOCK;
		}
		boolean isSTR = true ;   //是否是字符串型
		for(SemanticNode node : query.getNodes()){
			if(node.type != NodeType.STR_VAL){
				isSTR = false ;
				break ;
			}
		}
		
		// 非文字型问句，则为财务型的
		if(baseType == QueryType.STOCK){
			if(isSTR == true)
				baseType = QueryType.STOCK_STR ;
			else baseType = QueryType.STOCK_CAIWU ;			
		} else if(baseType == QueryType.FUND){
			if(isSTR == true)
				baseType = QueryType.FUND_STR ;
			else baseType = QueryType.FUND_CAIWU ;
		}
		return baseType;
	}
	
	private static String parseSimpleTree(TreeNode currNode){
		boolean hasCode = false;
		boolean onlyInst = false;
		boolean hasInds = false;
		boolean hasSort = false;
		boolean hasNum = false;
		boolean hasDate = false;
		ArrayList<String> patts = new ArrayList<String>();
		if(currNode.type() != NodeType.INST){
			return null;
		}
		StringBuilder treeBuilder = new StringBuilder();
		if(currNode.sons.size() == 1){
			onlyInst = true;
		}else{
			if(currNode.sons.get(0).text().matches("股票代码|股票简称")){
				hasCode = true;
			}else if(currNode.sons.get(0).text().matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
				hasInds = true;
			}else if(currNode.sons.size() > 1 && currNode.sons.get(1).sons != null){								
				if(currNode.sons.get(1).sons.get(0) != null &&
						currNode.sons.get(1).sons.get(0).type() == NodeType.SORT){
					hasSort = true;
				}else if(currNode.sons.get(1).text().equals("_数值")){
					hasNum = true;
				}else if(currNode.sons.get(1).type() == NodeType.PROP){
					PropNode propNode = (PropNode)(currNode.sons.get(1).node());
					if(propNode.isDateProp()){
						hasDate = true;
					}else{
						String index = currNode.sons.get(0).text();
						String prop = propNode.text.replace("_", "");
						if(!index.equals(prop)){
							index += ":"+prop;
						}
						patts.add("INDEXS:"+index);
					}
				}else{
					return null;
				}
				if(currNode.sons.size() > 2){
					if(currNode.sons.get(2).text().equals("_数值")){
						hasNum = true;
					}else if(currNode.sons.get(2).type() == NodeType.PROP){
						PropNode propNode = (PropNode)(currNode.sons.get(2).node());
						if(propNode.isDateProp()){
							hasDate = true;
						}
					}
				}
			}else{
				return null;
			}
		}
		
		if(hasCode){
			patts.add("STOCK:股票代码(简称)");
		}
		if(hasInds){
			patts.add("INDEXS:行业概念");
		}
		if(onlyInst){
			patts.add("INDEXS:指标查询");
		}
		if(hasNum){
			patts.add("INDEXS:指标范围");
		}
		if(hasDate){
			patts.add("INDEXS:查询时间");
		}
		if(hasSort){
			patts.add("INDEXS:指标排序");
		}
		Collections.shuffle(patts);
		for(String pat : patts){
			treeBuilder.append(pat+"\n");
		}
		return treeBuilder.toString().trim();
	}
	
	/***
	 * 粗粒度泛化树，对treeNodes中的CLASS节点解析，获取INDEX对应的类型
	 * @Note  by LU
	 * @rm.param treeNodes
	 * @return
	 */
	private static String parseSimpleTree(ArrayList<TreeNode> treeNodes){
		if(treeNodes.size() == 0){
			return null;
		}
		
		StringBuilder treeBuilder = new StringBuilder();
		
		boolean hasCode = false;    // 股票代码
		boolean onlyInst = false;    //行业概念
		boolean hasInds = false;    //行业
		boolean hasSort = false;    //排序
		boolean hasNum = false;    //数值
		boolean hasDate = false;    //日期
		ArrayList<String> patts = new ArrayList<String>();    //只含INDEX属性值的数组

		for (TreeNode treeNode : treeNodes) {
			if (treeNode.type() != NodeType.INST) {
				// 无INST类型节点，则无法泛化
				return null;
			}
			if (treeNode.sons.size() == 1) {
				onlyInst = true;
			} else {
				if (treeNode.sons.get(0).text().matches("股票代码|股票简称")) {
					hasCode = true;
				} else if (treeNode.sons.get(0).text()
						.matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")) {
					hasInds = true;
				} else if (treeNode.sons.size() > 1
						&& treeNode.sons.get(1).sons != null) {
					if (treeNode.sons.get(1).sons.get(0) != null
							&& treeNode.sons.get(1).sons.get(0).type() == NodeType.SORT) {
						hasSort = true;
					} else if (treeNode.sons.get(1).text().equals("_数值")) {
						hasNum = true;
					} else if (treeNode.sons.get(1).type() == NodeType.PROP) {
						PropNode propNode = (PropNode) (treeNode.sons.get(1)
								.node());
						if (propNode.isDateProp()) {
							hasDate = true;
						} else {
							String index = treeNode.sons.get(0).text();
							String prop = propNode.text.replace("_", "");
							if (!index.equals(prop)) {
								index += ":" + prop;
							}
							patts.add("INDEXS:" + index);
						}
					} else {
						return null;
					}
					if (treeNode.sons.size() > 2) {
						if (treeNode.sons.get(2).text().equals("_数值")) {
							hasNum = true;
						} else if (treeNode.sons.get(2).type() == NodeType.PROP) {
							PropNode propNode = (PropNode) (treeNode.sons
									.get(2).node());
							if (propNode.isDateProp()) {
								hasDate = true;
							}
						}
					}
				} else {
					return null;
				}
			}
		}
		
		
		if(hasCode){
			patts.add("STR_VAL:股票代码(简称)");
		}
		if(hasInds){
//			patts.add("INDEX STR_VAL:行业概念");
//			patts.add("INDEX");
			patts.add("STR_VAL:行业概念");
		}
		if(onlyInst){
			patts.add("INDEX");
		}
		if(hasNum){
			patts.add("INDEX NUM");
		}
		if(hasDate){
			patts.add("INDEX DATE");
		}
		if(hasSort){
			patts.add("INDEX SORT");
		}
		
		/** @Note sort会重排模板中元素的次序，需考虑 */
		Collections.sort(patts);
		for(String pat : patts){
			treeBuilder.append(pat+" ");
		}
		return treeBuilder.toString().trim();
	}
	/***
	 * 对最简单的指标查询问句泛化
	 * @rm.param query
	 * @return
	 */
	private static String parseSimpleSubTree(Query query){
		if(query.tree().size() == 0){
			return null;
		}
		
		/** 第一个节点，只有两种类型：LOGIC和INST */
		TreeNode root = query.tree().get(0);
		StringBuilder treeBuilder = new StringBuilder();
		if(root.type() == NodeType.LOGIC){
			boolean hasCode = false;
			boolean hasInst = false;
			int count = 0;    //INDEX的个数
			String msg = null;
			for(TreeNode treeNode : root.sons){
				if(treeNode.type() != NodeType.INST){
					return null;
				}
				if(treeNode.sons.size() == 1){
					count ++;
//					msg = " INDEXS:指标查询" ;
					msg = " INDEXS:" + treeNode.sons.get(0).text();
				}else{
					if(treeNode.sons.get(0).text().matches("股票代码|股票简称")){
						hasCode = true;
					}else if(treeNode.sons.get(0).text().matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
						hasInst = true;
					}else if(treeNode.sons.size() > 1 && treeNode.sons.get(1).sons != null
							&& treeNode.sons.get(1).sons.get(0).type() == NodeType.SORT){
						count ++;
//						msg =  " INDEXS:指标排序";						
						msg =  " INDEXS:" + treeNode.sons.get(1).sons.get(0).type();
					}else if(treeNode.sons.size() > 1 && treeNode.sons.get(1).sons != null
							&& treeNode.sons.get(1).text().equals("_数值")){
						count ++;
//						msg = " INDEXS:指标范围";
						msg = " INDEXS:" + treeNode.sons.get(1).text();
					}else{
						return null;
					}
				}
				
				if(count > 1){
					return null;
				}
			}
			
			if(hasCode){
				treeBuilder.append("STOCK:股票代码(简称)");
			}
			if(hasInst){
				treeBuilder.append(" INDEXS:行业概念");
			}
			if(msg != null){
				treeBuilder.append(msg);
			}
			return treeBuilder.toString().trim();
		}else if(root.type() == NodeType.INST){
			if(root.sons.size() == 1){
//				return "INDEXS:指标查询" ;
				return "INDEXS:" + root.sons.get(0).text();
			}else{
				if(root.sons.get(0).text().matches("股票代码|股票简称")){
					return "STOCK:股票代码(简称)";
				}else if(root.sons.get(0).text().matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
//					return "INDEXS:行业概念";
					return "INDEXS:" + root.sons.get(0).text();
				}else if(root.sons.size() > 1 && root.sons.get(1).sons != null
						&& root.sons.get(1).sons.get(0).type() == NodeType.SORT){					
//					return "INDEXS:指标排序";
					return "INDEXS:" + root.sons.get(1).sons.get(0).text();
				}else if(root.sons.size() > 1 && root.sons.get(1).sons != null
						&& root.sons.get(1).text().equals("_数值")){
//					return "INDEXS:指标范围";
					return "INDEXS:" + root.sons.get(1).text();
				}else{
					return null;
				}
			}
		}
		return null;
	}
	
	private static String parseTree(TreeNode treeNode, int treeLevel){
		if(treeNode == null){
			return null;
		}
		StringBuilder result = new StringBuilder();
		if(treeNode.type() == NodeType.INST){
			String rst = parseSimpleTree(treeNode);
			if(rst != null){
				result.append("\n");
				for(int cnt = 0; cnt <treeLevel; cnt++ ){
					result.append("\t");
				}
				result.append(rst);
				return result.toString();
			}
		}
		
		SemanticNode currNode = treeNode.node();
		switch(currNode.type){
		case CLASS:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append("INDEXS");
			break;
		case PROP:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append("PROP");
			result.append(":");
			String prop = currNode.text.replace("_","");
			if(prop.matches("所属概念|所属申万行业|所属同花顺行业|所属证监会行业")){
				prop = "行业概念";
			}
			PropNode propNode = (PropNode)currNode;
			if(propNode.isDateProp()){
				prop = "查询时间";
			}
			result.append(prop);
			break;
		case LOGIC:
			LogicNode logicNode = (LogicNode)currNode;
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append(logicNode.type);
			result.append(":"+logicNode.logicType);
			break;
		case OPERATOR:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append(currNode.type);
			result.append(":");
			result.append(((OperatorNode)currNode).operatorType);
			
			break;
		case TECHOPERATOR:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append(currNode.type);
			break;
		case NUM:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append(currNode.type);
			result.append(":查询比较结果");
			break;
			
		case DATE:
			result.append("\n");
			for(int cnt = 0; cnt <treeLevel; cnt++ ){
				result.append("\t");
			}
			result.append(currNode.type);
			result.append(":时间值");
			break;
		default:
			break;
		}
		
		
		if(treeNode.sons != null){
			String preSon = null;
			for(int idx = 0; idx < treeNode.sons.size(); idx++){
				TreeNode son = treeNode.sons.get(idx);
				String soni = null;
				if(currNode.type == NodeType.INST && idx == 0){
					soni = parseTree(son, treeLevel);
				}else{
					soni = parseTree(son, treeLevel + 1);
				}
				if(soni != null){
					if(preSon != null){
						if(preSon.equals(soni)){
							continue ;
						}
					}
					preSon = soni;
					result.append(soni);
				}
			}
		}
		return result.toString();
	}
	
	/**依据逻辑节点递归展开树
	 * @rm.param query
	 * @return 树序列
	 */
	private static ArrayList<TreeNode> tidyQuery(Query query){
		ArrayList<TreeNode> rtn = new ArrayList<TreeNode>();
		if(query == null || query.hasFatalError()){
			return rtn;
		}
		
		for(TreeNode sub : query.tree()){
			if(sub.type() == NodeType.LOGIC){
				rtn.addAll(tidyQuery(sub));
			}else{
				rtn.add(sub);
			}
		}
		return rtn;
	}
	
	private static ArrayList<TreeNode> tidyQuery(ArrayList<TreeNode> trees){
		ArrayList<TreeNode> rtn = new ArrayList<TreeNode>();
		for(TreeNode sub : trees){
			if(sub.type() == NodeType.LOGIC){
				rtn.addAll(tidyQuery(sub));
			}else{
				rtn.add(sub);
			}
		}
		return rtn;
	}
	
	private static ArrayList<TreeNode> tidyQuery(TreeNode treeNode){
		ArrayList<TreeNode> rtn = new ArrayList<TreeNode>();
		if(treeNode == null ){
			return rtn;
		}
		if(treeNode.type() == NodeType.LOGIC){
			for(TreeNode sub : treeNode.sons){
				if(sub.type() == NodeType.LOGIC){
					rtn.addAll(tidyQuery(sub));
				}else{
					rtn.add(sub);
				}
			}
		}else{
			rtn.add(treeNode);
		}
		return rtn;
	}
	
	/************************************************************
	 * 替换LogicNode，如连续5年，只用一年的时间
	 * @param trees
	 * @return
	 */
	private static ArrayList<TreeNode> replaceLogicNode(ArrayList<TreeNode> trees){
		ArrayList<TreeNode> rtn = new ArrayList<TreeNode>() ;
		for(TreeNode treenode : trees){
			if(treenode.type() == NodeType.LOGIC && treenode.splitInfo != null){
				if(treenode.sons.size() == 0){
					rtn.add(treenode) ;
				} else {
					rtn.add(treenode.sons.get(0)) ;
				}
			} else {
				rtn.add(treenode) ;
			}
		}
		return rtn ;
	}
	
	/****
	 * 按照产品给出的需求做泛化处理
	 * @rm.param secificPtn
	 * @return secificPtn
	 * parseSimpleSubTree
	 */
	private static String parsePtnByRequest(String secificPtn , BenchManager benchMgr)
	{
		String rtn = secificPtn ;
		String[] ptns = rtn.trim().split(" ") ;
	/** 1. 股票名+指标  都视为一个pattern 
	 *  2. 指标+排名 视为一个pattern， INDEX + SORT
	 *  3. 指标+数值/数值范围  视为一个pattern， INDEX + NUM
	 */
		if(ptns.length == 2){
			if(ptns[0].startsWith("STR_VAL:股票代码(简称)")
					&& ptns[1].startsWith("INDEX"))
				return "STR_VAL:股票代码(简称) INDEX" ;
			
//			if(ptns[0].startsWith("INDEX") && ptns[1].startsWith("SORT"))
//				return "INDEX SORT" ;
//			
//			if(ptns[0].startsWith("INDEX") && ptns[1].startsWith("NUM"))
//				return "INDEX NUM" ;
		}
	/** 
	 * 4. 判断一个pattern是否是由重复的子pattern构成 
	 *    有重复的子pattern，其长度必大于等于4
	 */
//		if(rtn.length() >= 4){
//			String reslut = dealDuplicateSubPtn(rtn) ;
//			if(reslut != null)
//				return reslut ;
//		}
//		
	/** 5. 将省份、城市泛化成LOCATION */
		if(rtn.contains("STR_VAL:省份") || rtn.contains("STR_VAL:城市"))
			rtn = dealLocationPtn(rtn) ;
//		
//	/** 6. 将中间的行业或概念提到pattern的最后 */
//		rtn = sentIndustryToLast(rtn) ;
//	
//	/** 7. 将两个及以上的UNKNOWN先归为一类 */
//		if(rtn.contains("UNKNOWN"))
//			rtn = dealUnknownPtn(rtn) ;
//	
//	/** 8. 大pattern包含小pattern，当且仅当大pattern比小pattern多其前或后多一个INDEX 
//	 *     高级需求，暂不考虑
//	 * */
//		// 设当前pattern为小patten
////		ptns = rtn.split(" ") ;
////		if(ptns.length >= 3){
////			Set<String> ptnSet = benchMgr.pattern2CodeMap.keySet() ;
////			for(String tmpPtn : ptnSet){
////				if(tmpPtn.contains(rtn)){
////					
////				}
////				else continue ;
////			}
////		}
//		
		
		// return 	 
		return rtn ;
	}

	
	/***
	 * 判断一个pattern是否是由重复的子pattern构成 
	 * @rm.param args
	 * @throws SQLException
	 * @throws IOException
	 * @return  子pattern
	 */
	private static String dealDuplicateSubPtn(String ptnStr){

		/** 可能的子结构 */
		LinkedList<String> tmpList = new LinkedList<String>() ;   
		/** 所有可能的子结构 */
		LinkedList<String> subPtns = new LinkedList<String>() ; 
		/** 只有NodeType的pattern */
		StringBuilder ptnSb = new StringBuilder();
		String rtn = null ;

		/** 查找所有可能的子结构 */
		for(String s : ptnStr.split(" ")){
			if(s.contains(":")){
				tmpList.add(s.substring(0, s.indexOf(":"))) ;
				ptnSb.append(s.substring(0, s.indexOf(":")) + " ") ;
			} else {
				tmpList.add(s) ;
				ptnSb.append(s + " ") ;
			}
			
			if(tmpList.size() > 1 && tmpList.getFirst().equals(tmpList.getLast())){
				// 存在可能的重复子结构
				StringBuilder tmpSb = new StringBuilder() ;
				for(int idx = 0 ; idx < tmpList.size()-1 ; ++idx ){
					String tmpstr  = tmpList.get(idx);
					tmpSb.append(tmpstr + " ") ;
				}
				String subPtn = tmpSb.toString().trim() ;
				boolean isNeedAdd = true ;
				for(String tmpstr : subPtns){
					if(subPtn.replace(tmpstr, "").trim().equals("")){
						isNeedAdd = false ;
						break ;
					}
				}
				if(isNeedAdd == true)
					subPtns.addFirst(subPtn) ;
			}
		}
		
		/** 判断是否为是由重复的子pattern构成 ，从最长的子pattern开始判断*/
		ptnStr = ptnSb.toString().trim() ;
		for(String s : subPtns){
			if(ptnStr.replace(s, "").trim().equals("")){
				rtn = s ;
				break ;
			}
		}
	
		if(rtn != null && !rtn.isEmpty())
			return rtn ;
		else return null ;						
	}
	
	private static String sentIndustryToLast(String specificPtn){
		if(specificPtn.contains("行业概念")){
			LinkedList<String> ptnList = new LinkedList<String>() ;
			LinkedList<String> rtnList = new LinkedList<String>() ;
			Collections.addAll(ptnList, specificPtn.split(" ")) ;
			int pos = 1 ;
			/** 中间的行业概念提到最后 */
			rtnList.add(ptnList.get(0)) ;
			for(int idx = 1; idx < ptnList.size(); ++idx){
				String tmp = ptnList.get(idx) ;
				if(tmp.contains("行业概念")){
					rtnList.addLast(tmp) ;
				}
				else {
					rtnList.add(pos, tmp) ;
					++pos ;
				}
			}
			StringBuilder rtnBuilder = new StringBuilder() ;
			for(String s : rtnList){
				rtnBuilder.append(s + " ") ;
			}
			specificPtn = rtnBuilder.toString().trim() ;
		}
		return specificPtn ;
	}
	
	/***
	 * 将只含一个UNKNOWN的pattern中的UNKNOWN去除。
	 * @rm.param rtn
	 * @return
	 */
	private static String dealUnknownPtn(String rtn){
		if(rtn.contains("UNKNOWN")){
			int count = 0 ;
			String[] ss = rtn.split(" ") ;
			for(String s : ss){
				if(s.contains("UNKNOWN"))
					++count ;
			}
			if( (ss.length - count) >= 2 ){
				//超过两个直接返回
				return rtn ;
			}
			else {
				StringBuilder sb = new StringBuilder() ;
				for(String s : ss){
					if(!s.contains("UNKNOWN"))
						sb.append(" " + s) ;
				}
				return sb.toString().trim() ;
			}				
		}

		return rtn ;
	}
	
	/***
	 * 将省份、城市泛化成LOCATION 
	 * @rm.param rtn
	 * @return rtn
	 */
	private static String dealLocationPtn(String rtn){
		if(rtn.contains("STR_VAL:省份") || rtn.contains("STR_VAL:城市")){
			StringBuilder sb = new StringBuilder() ;
			for(String s : rtn.split(" ")){
				if(s.startsWith("STR_VAL:省份") || s.startsWith("STR_VAL:城市"))
					sb.append(" STR_VAL:LOCATION") ;
				else sb.append(" " +s) ;
			}
			rtn = sb.toString().trim() ;
		}
		return rtn ;
	}
	
	private static String parseLength(String ptn){
		ArrayList<String> ptns = new ArrayList<String>() ;
		for(String s : ptn.split(" ")){
			if(s.equals("STR_VAL:股票代码(简称)")){
				// 股票简称
				ptns.add("G") ;
			}
			else if(s.equals("STR_VAL:行业概念")){
				// 行业
				ptns.add("H") ;
			}
			else if(s.equals("STR_VAL:LOCATION")){
				// 地理位置
				ptns.add("L") ;
			}
			else {
				// 其他
				if(s.contains(":")){
					ptns.add(s.substring(s.indexOf(":")+1)) ;
				}
				else 
					ptns.add(s) ;
			}
		}
		
		StringBuilder builder = new StringBuilder() ;
		for(String s : ptns){
			builder.append(" " + s) ;
		}
		String rtn = builder.toString().trim() ;
		
		return ptn + " STR_LEN:" + (rtn.length()-1)/3 ;
	}
	
	private static String parseTreeCode(String ptn, BenchQuery benchQuery){
		return ptn + " TC:" + benchQuery.treeCode ;
	}
	
	
	public static void main(String[] args) throws SQLException, IOException{	
		ParserAgent.init("./conf/qparser.conf");
		BenchManager commandManager = new DBBenchManager(null);
//		BenchDownLoder.loadFromCorrectQueryCorpus("correct-query.txt") ;
//		System.exit(0) ;
		
		//break test2012年中圾预增200%以上的股票
		String text = "净利润增长率大于100% 高成长";
//		String text = "连续3天涨幅" ;
		
		BenchQuery bqtest = new BenchQuery(text);
		Query queryTest = ParserAgent.parse(bqtest);
		
		for(SemanticNode sn : queryTest.getNodes())
			System.out.println(sn);
		
		
		if(!ParserAgent.parseBenchQueryFileds(queryTest, bqtest, commandManager)) {
			System.err.println("<<< 标准集问句解析失败：" + bqtest.text);
		} else {
			System.out.println(bqtest.text);
			System.out.println("Treecode:" + bqtest.treeCode);
			System.out.println("Genral Pattern: " + bqtest.generalPtn);
			System.out.println("Specific Pattern: " + bqtest.specificPtn);
//			System.out.println("unknows: " + bqtest.unknowns);
			System.out.println("------------------------------------");
		}
		System.exit(0) ;
		
		long time = System.currentTimeMillis() ;
		
		HashSet<String> qureies = new HashSet<String>() ;
		try {
//			qureies.addAll(RMUtil.readTxtFile("correct-query.txt")) ;
			qureies.addAll(Util.readTxtFile("正确问句库.txt")) ;
		} catch (DataConfException e) {
			e.printStackTrace();
		}
		
//		int count = 0 ;
//		HashMap<String, LinkedList<String>> ptnMap = new HashMap<String, LinkedList<String>>() ;

		BufferedWriter tout = new BufferedWriter(new FileWriter(new File("tout正确问句.txt"))) ;
		for(String q : qureies){
			BenchQuery bq = new BenchQuery(q);
			Query query = ParserAgent.parse(bq);			
			if(ParserAgent.parseBenchQueryFileds(query, bq, commandManager) == false){
				System.err.println("<<< 标准集问句解析失败：" + bq.text);
				continue ;
			}
			tout.write("query: " + bq.text + "\t") ;
//			tout.write("treecode: " + bq.treeCode + "\t");
			tout.write("pattern: " + bq.specificPtn + "\t");
			tout.newLine() ;
//			tout.write("ptncode: " + bq.specificPtnCode + "\n");
			System.out.println("query: " + bq.text + "\t" + "pattern: " + bq.specificPtn + "\t");
							
		}
		tout.close() ;
		
		System.out.println("耗时" + (System.currentTimeMillis() - time)/1000.0 + " s");
		
		
	}
}
