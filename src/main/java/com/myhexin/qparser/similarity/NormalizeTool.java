package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.QueryNodes;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.similarity.PatternNode.PatternNodeType;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.StrStrPair;

/**
 * NormalizeTool为一个chunk单元进行泛化服务，
 * 生成<b>npattern,indexes,nodelist</b>等重要信息
 * <br>注意：<b>所有的操作都是在一个chunk内进行，外部需保证泛化对象为一个chunk，
 * NormalizeTool内部不再做检查.若泛化整个query，see {@link NormalizeCollection}</b>
 * @author luwenxing
 */
public class NormalizeTool {
	
	/** 初始化传入的原始query text */
	private String text ;
	
	/** 预处理过的 rawQueryStr_，将里面的标点符号无关的信息去除*/
	private String preTreatedRawStr_ ;
	
	/** 问句类型 */
	private Type type ;
	
	/** 经过数字泛化的pattern */
	private String nPattern ;
	
	/**可以替代index的关键词*/
	private List<String> keyWords = null;
	
	/**本chunk query所对应的querytype*/
	private String qtype = null;
	
	@SuppressWarnings("unused")
	private List<SegWordInfo> segWordInfoList ;
	
	/** chunk内所有的指标 */
	private List<String> indexList = new ArrayList<String>();
	
	/** 分词返回的结果 */
	private String segLine = null ;
	
	/** 带有同义词转换的分词结果 */
	private String hasTransSegLine = null;
	
	/**分词后提取的结构化的word 和 相应的info信息 */
	private List<Pair<String,String>> wordInfoList = null ;
	
	/** 泛化后提取的结构化的word 和 相应的info信息 */
	private List<Pair<String,String>> wordPtnInforList = null ;
	
	/** 分词后的list */
	private List<String> segList ;
	
	/**分词后的词及权重*/
	private List<Pair<String, Double>> segValueList = null ;
	
	/**记录泛化的部分的前后值，如：[from=_SMLNUM_, to=5, from=_SMLNUM_, to=10]。
	 * 其中5被泛化成了_SMLNUM_。本变量主要用于后期{@link SimilarityChunkUnit}中replace
	 * 中的替换操作。
	 */
	private List<StrStrPair> numStockPtnValPairList = new ArrayList<StrStrPair>() ;
	
	/**分词后生成的SemanticNodeList, 只具有浅层语义*/
	private List<SemanticNode> nodes = null ;
	
	/** 语义泛化后的节点list */
	private List<PatternNode> pNodeList = null ;
	
	/** 人工定义的词的权重 */
	private static Map<String, Double> WORD_MAP 
		= new HashMap<String, Double>() ;
	
	/** 可忽略词集合 */
	private static Set<String> IGNORABLE_WORDS = new HashSet<String>();
	
	/** 预处理是需要忽略的标点 */
    private final static String DECIMAL = "^[,.，。?？、]*(.+?)[,.，。?？、]*$" ;
    /** 标点的Pattern */
    static final Pattern DECIMAL_PTN = Pattern.compile(DECIMAL) ;
    
    // 以下数字泛化的regex及pattern
    private final static String CHNUM_REGEX = "[\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+";
    private final static String UNIT_REGEX = "个?[年月日天季周毛角元块]+";
    private final static String MA_REGEX_1 = "(\\d+)[Mm][Aa]";
    private final static String MA_REGEX_2 = "[Mm][Aa](\\d+)";
    private final static String NORMAL_NUM_REGEX = "\\d+\\.?\\d+";
    private final static String MATH_PERCENT_NUM_REGEX = "(\\d+\\.?\\d+)%" ;
    private final static String CN_PERCENT_NUM_REGEX = "百分之(\\d+\\.?\\d+)" ;
    
    /** 中文小数 */
    private final static String CN_POINT_REGEX = "[零一两二三四五六七八九十]{1,3}点[零一两二三四五六七八九拾玖捌柒陆伍肆叁贰壹]+" ;
    private final static String N_CHENG_REGEX = "[一二两三四五六七八九十\\d]{1,3}成" ;	// "一成"
    
    /** 形如“1000W” */
    private final static String NUM_CASE_REGEX = "\\d+[WwKk]" ; 
    
    /** 大于1000的中文模糊数字，"三四亿" */
    private final static String CN_FUZZY_GT_1000 = "(一二|二三|三四|四五|五六|六七|七八|八九)([十百千万]?亿|[十百千]?万|千)" ;
    /** 小于1000的中文模糊数字，"一两百" */
    private final static String CN_FUZZY_LT_1000 = "(一二|二三|三四|四五|五六|六七|七八|八九)百" ;
    
    /**指标级别的语义*/
    private final static String SEMANTIC_CLASS = "onto_class";
    private final static String SEMANTIC_FAKECLASS = "onto_fakeClass";
    private final static String SEMANTIC_TECHOP = "onto_techOp";
    /**change语义*/
    private final static String SMEANTIC_CHANGE = "onto_change";
    /**trigger语义*/
    private final static String SMEANTIC_TRIGGER = "onto_trigger";
    /**prop语义*/
    private final static String SMEANTIC_PROP = "onto_value:prop";
    
    private final static String BIG =  "_BIGNUM_";	// big number
    private final static String SML = "_SMLNUM_";		// small number
    private final static String STOCK = "_STOCK_" ;	// 股票简称 或 股票代码
    private final static String FUND = "_FUND_" ; // 基金简称 或 基金代码
    private final static String HKSTOCK = "_HKSTOCK_";//港股简称 或 港股代码
    
    private final static Pattern CHNUM_REGEX_PTN = Pattern.compile(CHNUM_REGEX);
    private final static Pattern NUM_REGEX_PTN = Pattern.compile(NORMAL_NUM_REGEX);
    private final static Pattern MATH_PERCENT_NUM_REGEX_PTN = Pattern.compile(MATH_PERCENT_NUM_REGEX);
    private final static Pattern CN_PERCENT_NUM_REGEX_PTN = Pattern.compile(CN_PERCENT_NUM_REGEX);
    private final static Pattern DOUBLE_WITH_CHINESE_NUM_PTN = Pattern.compile("(\\d+?\\.?\\d*?[十百千万亿仟佰拾]+)");
    
    private static Pattern UNIT_REGEX_PTN = Pattern.compile(UNIT_REGEX);
    private static Pattern MA_REGEX_1_PTN = Pattern.compile(MA_REGEX_1);
    private static Pattern MA_REGEX_2_PTN = Pattern.compile(MA_REGEX_2);
	
	/** Debug variable */
	private static final boolean SEG_BY_SEMANTIC = false ;
	//private static final boolean USE_INDEX = false ;
	private static final boolean USE_KEY_WORDS = true;
	
    public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(NormalizeTool.class.getName());
    
    
    /** constructor */
    public NormalizeTool(String rawQueryStr, Type type){
        this.text = rawQueryStr ;
        Matcher m = DECIMAL_PTN.matcher(rawQueryStr) ;
        if(m.find()) {
        	this.preTreatedRawStr_ = m.group(1) ;
        } else {
        	this.preTreatedRawStr_ = rawQueryStr ;
        }
        this.type = type ;
    }
    
    /** constructor */
    public NormalizeTool(Type type, String rawQueryStr, String segLine, String hasTransSegLine){
    	this.type = type ;
    	this.text = rawQueryStr ;
        Matcher m = DECIMAL_PTN.matcher(rawQueryStr) ;
        if(m.find()) {
        	this.preTreatedRawStr_ = m.group(1) ;
        } else {
        	this.preTreatedRawStr_ = rawQueryStr ;
        }
    	this.segLine = segLine;
    	this.hasTransSegLine = hasTransSegLine;
    }
    
    public String text(){ return this.text; }
    
    public String nPattern() { return this.nPattern ; }
    
    public List<String> indexList(){ return this.indexList ; }
    
    public List<PatternNode> pNodeList(){ return this.pNodeList ; }
    
    public List<StrStrPair> numStockValList(){ return this.numStockPtnValPairList ;}
    
    public String segLine(){ return this.segLine ;}
    
    public String toString(){ return this.nPattern ;}
    
    public List<String> getKeyWords(){ return this.keyWords; }
    
    public String getQType(){ return this.qtype; }

    
    public static void loadWordWeight(List<String> lines) throws UnexpectedException{
    	Map<String, Double> wordWeightMap 
		= new HashMap<String, Double>() ;
    	for(String line : lines){
    		if(line.startsWith("#") || line.trim().isEmpty()) {	continue ;}
    		if(line.equals("break")){ break ; }
    		
    		String[] kvArr = line.split(":") ;
    		if(kvArr.length != 2) {
    			String errMsg = String.format("error format [%s]", line) ;
    			throw new UnexpectedException(errMsg) ;
    		}
    		String key = kvArr[0] ;
    		try {
    			double val = Double.valueOf(kvArr[1]) ;
    			wordWeightMap.put(key, val);
    		} catch (NumberFormatException e){
    			logger_.warn(e.getMessage()) ;
    		}
    	}
    	WORD_MAP = wordWeightMap ;
    }
    
    /**泛化入口的主函数*/
    public void normalParse(){
    	this.nPattern =  this.createOnlyNumGenPattern(preTreatedRawStr_, type) ;
		this.segList = this.getSegList() ;
		/*if(USE_INDEX) {
		    try{
			    this.indexList.addAll(this.getQueryIdxes(preTreatedRawStr_, type)) ;
			    // 只具有浅层语义的nodes
			    this.nodes = this.nodes() ;
			    this.pNodeList = this.patternNodes() ;
		    }catch(Exception e){
			    logger_.warn("create index failed");
			}
		}else{
		    this.indexList = new ArrayList<String>();
		    this.nodes = new ArrayList<SemanticNode>();
		    this.pNodeList = new ArrayList<PatternNode>();
		}*/
		
		
		this.indexList = new ArrayList<String>();
		this.nodes = new ArrayList<SemanticNode>();
		this.pNodeList = new ArrayList<PatternNode>();
		if(USE_KEY_WORDS){
		    this.keyWords = createKeyWords();
		}
		//this.qtype = createQParseStr();
    }
    
    /*private String createQParseStr(){
        ResourceDomainType domainType = EnumDef.getResourceDomainTypeByQueryType(this.type);
        if( domainType == null ){
            return null;
        }
        return domainType.name();
    }*/
    
    private List<String> createKeyWords(){
        if( this.hasTransSegLine == null || this.hasTransSegLine.isEmpty() ){
            this.hasTransSegLine = NormalizeUtil.tokenizeByHasTransSegger(this.preTreatedRawStr_, type);
        }
        if( this.hasTransSegLine == null || this.hasTransSegLine.isEmpty() ){
            return null;
        }
        Map<String, List<String>> semanticToKeyWords = createSemanticToKeyWords(this.hasTransSegLine);
        //首先尝试获取指标级别的关键词
        List<String> rlt = extractDefSemanticsKeyWords(semanticToKeyWords, SEMANTIC_CLASS, SEMANTIC_FAKECLASS, SEMANTIC_TECHOP);
        if( rlt == null || rlt.isEmpty() ){
            rlt = extractDefSemanticsKeyWords(semanticToKeyWords, SMEANTIC_CHANGE);
        }
        if( rlt == null || rlt.isEmpty() ){
            rlt = extractDefSemanticsKeyWords(semanticToKeyWords, SMEANTIC_TRIGGER);
        }
        /*
        if( rlt == null || rlt.isEmpty() ){
            rlt = extractDefSemanticsKeyWords(semanticToKeyWords, SMEANTIC_PROP);
        }
        */
        return rlt;
    }
    
    private List<String> extractDefSemanticsKeyWords( Map<String, List<String>> semanticToKeyWords, String... semantics ){
        List<String> rlt = new ArrayList<String>();
        List<String> nowList = null;
        for( String semantic : semantics ){
            nowList = semanticToKeyWords.get(semantic);
            if( nowList == null ){
                continue;
            }
            rlt.addAll(nowList);
        }
        return rlt;
    }
    
    private Map<String, List<String>> createSemanticToKeyWords( String hasTransSegLine ){
        Map<String, List<String>> rlt = new HashMap<String, List<String>>();
        String[] lines = hasTransSegLine.split("\n");
        String segLine = lines[0];
        String[] tokens = segLine.split("\t");
        int count = tokens.length;
        String[] kv = null;
        String word = null;
        String semantic = null;
        for( int i=0; i<count; i++ ){
            kv = tokens[i].split("/");
            if( kv.length < 2 ){
                continue;
            }
            word = kv[0];
            semantic = kv[1];
            tryAddKeyWordToMap(rlt, word, semantic);
        }
        return rlt;
    }
    
    private void tryAddKeyWordToMap( 
            Map<String, List<String>> destination, 
            String word, 
            String semantic ){
        String iwantSemantic = null;
        if( semantic.contains(SEMANTIC_CLASS) ){
            iwantSemantic = SEMANTIC_CLASS;
        }else if(semantic.contains(SEMANTIC_FAKECLASS )){
            iwantSemantic = SEMANTIC_FAKECLASS;
        }else if(semantic.contains(SEMANTIC_TECHOP)){
            iwantSemantic = SEMANTIC_TECHOP;
        }else if(semantic.contains(SMEANTIC_CHANGE)){
            iwantSemantic = SMEANTIC_CHANGE;
        }else if(semantic.contains(SMEANTIC_TRIGGER)){
            iwantSemantic = SMEANTIC_TRIGGER;
        }else if(semantic.contains(SMEANTIC_PROP )){
            iwantSemantic = SMEANTIC_PROP;
        }
        if( iwantSemantic == null ){
            return;
        }
        if( !destination.containsKey(iwantSemantic) ){
            destination.put(iwantSemantic, new ArrayList<String>());
        }
        List<String> destList = destination.get(iwantSemantic);
        destList.add(word);
    }
    
    @SuppressWarnings("unused")
	private List<SegWordInfo> parseSegLine(String segLine){
    	return null ;
    }
    
    /** 获得词的权重，默认为1.0 */
    private double getWeight(String word){
    	if(WORD_MAP.containsKey(word)) {
    		return WORD_MAP.get(word) ;
    	} else {
    		return 1.0 ;
    	}
    }
    
    /**
     * 获得一个chunk句的npattern和所有的指标。
     * <br>注意：<b>此函数为最开始开发的，传参，返回值都不灵活，后续开发请不调用此函数。</b></br>
     * @param rawQueryStr
     * @param type
     * @return
     */
    /*@Deprecated
    public String _getNumNormalizeAndIdxlist(String rawQueryStr, Query.Type type){
        String normalize = createOnlyNumGenPattern(rawQueryStr, type);
        String idxList = null; //formatIdxList(getQueryIdxes(rawQueryStr, type));
        String rlt = String.format("%s||%s", normalize, idxList);
        logger_.info("[{}]==>[{}]", rawQueryStr, rlt);
        return rlt;
    }*/
    
    /*@Deprecated
    private String _formatIdxList(Set<String> queryIdxes) {
        StringBuilder sb = new StringBuilder();
        for(String idx : queryIdxes){
            if(sb.length() > 0) {sb.append(";");}
            sb.append(idx);
        }
        return sb.toString();
    }*/

    /**
     * 返回问句对应所有指标序列
     * @param rawQueryStr
     * @param type
     * @return
     * @throws UnexpectedException 
     */
    /*public Set<String> getQueryIdxes(String rawQueryStr, Query.Type type){
        Set<String> idxes = new HashSet<String>();
        Query query = new Query(rawQueryStr);
        query.setType(type);
        //query.setParserType(ParserType.NORMALIZE_PARSER);
        //ArrayList<QpStep> bars=setBarStep();	
        //query.setQpBarStep(bars);
        Set<String> cnStrs = new HashSet<String>();
        try {
        	//parser_.cleanParseForTree(query);
        	ParseResult pr = parser_.parse(query);
        	if(pr.qlist!=null && pr.qlist.size()>0) {
        		cnStrs = this.getAllCNodeFromNodes(pr.qlist.get(0)) ;
        	}
        	
        } catch (Exception e) {
            logger_.warn(String.format("GetIdxList Error {[%s]}", e.getMessage()));
        }
        
        for(String cName: cnStrs){
            idxes.add(String.format("INDEX[%s]", cName));
        }
        return idxes;
    }*/
    /**
     * @deprecated
     * @return
     */
    /*private ArrayList<QpStep> setBarStep(){
    	ArrayList<QpStep> bars=new ArrayList<QpStep>();
    	bars.add(QpStep.SimilarByNewSimilar);bars.add(QpStep.ChunkQuery);bars.add(QpStep.SimilarityQueryWithOptions);
    	bars.add(QpStep.ParseAppendInfo);bars.add(QpStep.DictChunk);bars.add(QpStep.CheckInfoQuery);
    	bars.add(QpStep.Compile);bars.add(QpStep.SetSearchKeyword);bars.add(QpStep.SuggestInfo);
    	bars.add(QpStep.PureTokenizeForDate);
    	return bars;
    }*/
    /**
     * @deprecated
     * @return
     */
    /*private static ArrayList<QpStep> setBarStepForChunk(){
    	ArrayList<QpStep> bars=new ArrayList<QpStep>();
    	bars.add(QpStep.SimilarByNewSimilar);bars.add(QpStep.ChunkQuery);bars.add(QpStep.SimilarityQueryWithOptions);
    	bars.add(QpStep.ParseAppendInfo);bars.add(QpStep.SpecMarkChunk);bars.add(QpStep.CheckInfoQuery);
    	bars.add(QpStep.PatternChunk);bars.add(QpStep.SetChunkLogAfterCP);bars.add(QpStep.DeepTokenize);
    	bars.add(QpStep.CheckBadNode);bars.add(QpStep.Normalize);bars.add(QpStep.TrySmartAnswer);
    	bars.add(QpStep.Build);bars.add(QpStep.CheckInfoQuery);bars.add(QpStep.PureTokenizeForDate);
    	bars.add(QpStep.Compile);bars.add(QpStep.SetSearchKeyword);bars.add(QpStep.SuggestInfo);
    	return bars;
    }*/
    
    /*private Query query(){
    	if(this.chunkQuery == null) {
    		Query query = new Query(preTreatedRawStr_);
    		query.setQPFunction(Query.QPFunction.STOP_ATTER_DICT_CHUNK) ;
            query.setType(type);
            query.setParserType(ParserType.CHUNK_PARSER);
//            query.setQpBarStep(setBarStepForChunk());
            //parser_.cleanParseForChunk(query);
            parser_.parse(query);
            this.chunkQuery = query ;
    	}
    	return chunkQuery ;
    }*/
    
    
    /**
     * 在完成树绑定之后，从nodes中提取指标
     * @param query
     * @return
     */
    /*private Set<String> getAllCNodeFromNodes(ArrayList<SemanticNode> nodes) {
        Set<String> cnText = new HashSet<String>();
        if (nodes == null || nodes.size() == 0)
            return cnText;
        for (SemanticNode node : nodes) {
            if (node.type == NodeType.CLASS) {
                cnText.add(node.text);
            }
        }
        return cnText;
    }*/

    /**
     * 对文本中的数字进行泛化。<br>数字转换为“_NUM_”，返回文本。
     * 目前数字分两类：_BIGNUM_（>=1k）_SMLNUM_（<1k）
     * <br>ps:采用无同义词转换的分词，对onto_date和onto_num中的数字泛化
     * <br>   中文数字暂时未作处理
     * @param rawQueryStr
     * @param type
     * @return
     * @throws NotSupportedException 
     */
    public String createOnlyNumGenPattern(String rawQueryStr, Query.Type type){
        String rltStr = rawQueryStr;
        if(segLine == null) {
        	segLine = tokenizeBySegger(rawQueryStr, type); 
        }
        List<String> segList = this.getSegList() ;
        this.segList = segList ;
		StringBuilder sbParseText = new StringBuilder();
		for(String seg : segList){
			sbParseText.append(seg) ;
		} 
		rltStr = sbParseText.toString(); 
        return rltStr;
    }
    
    
    /** 抽取分词 */
    private List<String> extractSegList() {
    	if(this.wordPtnInforList == null ) {
    		this.wordPtnInforList = this.createWordPtnInfoList() ;
    	}
    	List<String> segList = new ArrayList<String>() ;
    	for( Pair<String, String> wordPtnInfo : this.wordPtnInforList) {
    		segList.add(wordPtnInfo.first) ;
    	}
    	return segList ;
    } 
    
    private List<Pair<String,String>> createWordPtnInfoList(){
    	List<Pair<String,String>> wordPtnInfoList = new ArrayList<Pair<String,String>>() ;
    	for(Pair<String, String> pair : this.getWordInfoList()){
			String word = pair.first ;
			String info = pair.second ;
			String newWord = replaceWord(word, info);
			wordPtnInfoList.add(new Pair<String, String>(newWord, info)) ;
		} 
    	return wordPtnInfoList ;
    }

    /** 
     * 分词 成功返回分词结果，否则返回null
     */
	private String tokenizeBySegger(String rawQueryStr, Query.Type type) {
		String segLine = NormalizeUtil.tokenizeBySegger(rawQueryStr, type) ;
		return segLine;
	}
	
	private List<Pair<String, String>> getWordInfoList(){
		if(this.wordInfoList != null) 
			return this.wordInfoList ;
		if (this.segLine == null) {
			this.segLine = tokenizeBySegger(preTreatedRawStr_, type);
		}
		if (this.segLine == null) {
            return new ArrayList<Pair<String,String>>();
        }
		List<Pair<String, String>> wordInfoList = new ArrayList<Pair<String,String>>() ;
		for (String token : this.segLine.split("\t")) {
			if(token.contains("/trans:")){
				try {
					wordInfoList.add(createTransWordInfo(token)) ;
					continue ;
				} catch (QPException e) {
					logger_.warn(e.getMessage()) ;
				}
			}
		    wordInfoList.add(createWordInfo(token)) ;
		}
		return this.wordInfoList = wordInfoList ;
	}
	
	/**
	 * 根据原始的分词结果获取词的word 和 info；在分词中是以'/'划分word和info
	 * @param token
	 * @return
	 */
	private Pair<String, String> createWordInfo(String token){
		int pos = token.lastIndexOf('/');
		if (pos == -1) {
			return  new Pair<String, String>(token, "");
		} else {
			String word = token.substring(0, pos);
			String info = (pos == token.length() - 1 ? "" : token
					.substring(pos + 1));
			return new Pair<String, String>(word, info) ;
		}
	}
	
	/** 
	 * 含有同义词的分词，根据"/trans:"来划分word和info
	 * @param token
	 * @return wordInfoPair
	 * @throws UnexpectedException
	 */
	private Pair<String, String> createTransWordInfo(String token) throws UnexpectedException{
		String[] tmpArr = token.split("/trans:") ;
		String word , info ;
		if(tmpArr.length ==2 ){
			word = tmpArr[0] ;
			info = tmpArr[1] ;
		} else {
			throw new UnexpectedException("token is not trans word") ;
		}
		return new Pair<String, String>(word, info) ;
	}
	
	public List<String> getSegList(){
		if (segLine == null) {
			segLine = tokenizeBySegger(preTreatedRawStr_, type);
		}
		if(this.segList == null) {
			this.segList = extractSegList() ;
		}
		return this.segList ;
	}
	
	public List<Pair<String, Double>> segValueList(){
		if(this.segValueList == null) {
			this.segValueList = this.getSegValueList() ;
		}
		return this.segValueList ;
	}
	
	private List<Pair<String, Double>> getSegValueList() {
		if (this.wordPtnInforList == null) {
			this.wordPtnInforList = this.createWordPtnInfoList();
		}
		List<Pair<String, Double>> segValueList = null ;

		if (SEG_BY_SEMANTIC) {
			if (segLine == null) {
				segLine = tokenizeBySegger(preTreatedRawStr_, type);
			}
			try {
				segValueList = computeSegValueBySemantic(segLine);
			} catch (QPException e) {
				e.printStackTrace();
			}
		} else {
			segValueList = new ArrayList<Pair<String, Double>>();
			for (Pair<String, String> wordPtnInfo : this.wordPtnInforList) {
				Pair<String, Double> segVal = computeSegValueByNormal(wordPtnInfo);
				segValueList.add(segVal);
			}
		}

		return segValueList;
	}
	/**
	 * 普通的计算分词的权重
	 * @param wordPtnInfo
	 */
	private Pair<String, Double> computeSegValueByNormal(Pair<String, String> wordPtnInfo){
		// num stock第一优先级
		if(ChunkSimilarity.NUM_STOCK_PTN.matcher(wordPtnInfo.first).find()) {
			return new Pair<String, Double>(wordPtnInfo.first, 10.0) ;
		}
		String smInfo = wordPtnInfo.second;
		int pos = smInfo.indexOf(':', 5);
		if (pos == -1) {
			double weight = 1.0;
			if (IGNORABLE_WORDS.contains(wordPtnInfo.first)) {
				weight = 0.1;
			}
			return new Pair<String, Double>(wordPtnInfo.first, weight);
		}
		String strType = smInfo.substring(5, pos);
		if (strType.equals("vagueDate")) {
			return new Pair<String, Double>(wordPtnInfo.first, 6.0) ;
        } else if (strType.equals("vagueNum")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 6.0) ;
        }else if (strType.equals("special")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 2.0) ;
        }else if (strType.equals("change")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 7.0) ;
        } else if (strType.equals("trigger")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 5.0) ;
        } else if (strType.equals("operator")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 6.0) ;
        } else if (strType.equals("techOp")){
        	return new Pair<String, Double>(wordPtnInfo.first, 8.0) ;
        } else if (strType.equals("geoname")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 2.0) ;
        } else if (strType.equals("logic")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 2.0) ;
        } else if (strType.equals("qword")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 2.0) ;
        } else if (strType.equals("techPeriod")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 8.0) ;
        } else if (strType.equals("class")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 10.0) ;
        } else if (strType.equals("fakeClass")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 10.0) ;
        }else if (strType.equals("fakeProp")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 5.0) ;
        }else if (strType.equals("prop")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 5.0) ;
        } else if (strType.equals("value")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 6.0) ;
        } else if (strType.equals("sort")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 7.0) ;
        } else if (strType.equals("avg")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 6.0) ;
        } else if (strType.equals("neg")) {
        	return new Pair<String, Double>(wordPtnInfo.first, 8.0) ;
        } else if(IGNORABLE_WORDS.contains(wordPtnInfo.first)) {
        	return new Pair<String, Double>(wordPtnInfo.first, 0.1) ;
        } else {
        	return new Pair<String, Double>(wordPtnInfo.first, 1.0) ;
        }
	} 
	
	/**
	 * 将语义权重信息（简单parser后）加入到分词权重中
	 * @param segLine 单个chunk的原始分词信息
	 * @throws QPException 
	 */
	private List<Pair<String, Double>> computeSegValueBySemantic(String segLine) throws QPException {
		List<Pair<String, Double>> rtnList = new ArrayList<Pair<String,Double>>() ;
		ArrayList<SemanticNode> nodes = NormalizeUtil.makeQueryBySegLine(segLine, this.type) ;
		for(int pos = 0; pos < nodes.size() ; ++pos){
			SemanticNode node = nodes.get(pos) ;
			Pair<String, Double> pair = null ;
			String chkPosStr ;
			double weight ;
			switch(node.getType()){
				case NUM :
					chkPosStr = getChunkText(pos, nodes) ;
					weight = 10.0 ;
					break ;
				default:
					chkPosStr = getChunkText(pos, nodes) ;
					weight = 1.0 ;
			}
			pair = new Pair<String, Double>(chkPosStr, weight) ;
			rtnList.add(pair) ;
		}
		return rtnList ;
	}
	
	public String getChunkText(int pos, ArrayList<SemanticNode> nodes){
    	return getChunkText(pos, pos+1, nodes) ;
    }
	
    private static String getSysText(ArrayList<SemanticNode> simpleNodes) {
        StringBuilder sb = new StringBuilder();
        for(SemanticNode node : simpleNodes){
            sb.append(node.getText());
        }
        return sb.toString();
    }
    public static final String FAILTAG = "[提取chunk失败]";
    private String getChunkText(int beg, int end, ArrayList<SemanticNode> semanticNodes) {
    	/*StringBuilder buf = new StringBuilder();
    	for(int i=beg;i<=end && i<nodes.size();i++) {
    		SemanticNode node = nodes.get(i);
    		buf.append(node.text);
    	}
        String chkText = buf.toString();*/
    	QueryNodes nodes = new QueryNodes(semanticNodes);
    	
        IntIntPair boundOfNodes = nodes.getRollbackBoundsInRange(beg, end, this.type);
        String sysText = getSysText(nodes.subListOfBackNodes(boundOfNodes.first, boundOfNodes.second));
        IntIntPair bound = nodes.getBacknodeTextBound(boundOfNodes);
        
        ArrayList<TextTranser> allTransers = new ArrayList<TextTranser>();
        //TODO what are they below?
        //allTransers.addAll(query.getLog().getRegexTransers());
        //allTransers.addAll(query.getLog().getLtpTransers());
            
        String chkText = sysText;
        int sFlag = -1;
        int eFlag = -1;
        try {
            sFlag = TextTranser.findRepWordIdxInOriginByTransers(bound.first, allTransers, true);
            eFlag = TextTranser.findRepWordIdxInOriginByTransers(bound.second, allTransers, true);
        } catch (Exception e) {
            chkText = String.format("%s%s", sysText, FAILTAG);
        }
        
        if(sFlag < 0 || eFlag < 0 ||  sFlag > text.length() || eFlag > text.length() || sFlag > eFlag){
            chkText = String.format("%s%s", sysText, FAILTAG);
        } else {
            chkText = text.substring(sFlag, eFlag);
        }
        
        return chkText;
   }

	
	/** 只具有浅层语义的nodes */
	public List<SemanticNode> nodes() {
		if (this.nodes != null) {
			return this.nodes;
		}
		return new ArrayList<SemanticNode>();
		//return this.nodes = this.query().nodes().subList(0, this.query().nodes().size()) ;
	}
	
	public List<PatternNode> patternNodes(){
		if(this.pNodeList != null) {
			return this.pNodeList ;
		}
		List<PatternNode> rtnList = new ArrayList<PatternNode>() ;
		
		for(SemanticNode sn : this.nodes()){
			boolean isFeatureNode = isFeatureNode(sn);
			if(!isFeatureNode) {
				continue ;
			}
			PatternNode pnode = PatternNode.makePatternNode(sn) ;
			double weight = getPNodeWeight(pnode);
			pnode.setPriority(weight) ;
			rtnList.add(pnode) ;
		}
		return this.pNodeList = rtnList ;
	}

	/**
	 * 判断节点是否是特征节点
	 * @param sn
	 * @return
	 */
	private boolean isFeatureNode(SemanticNode sn) {
		boolean isFeatureNode = false ;
		isFeatureNode |= sn.getType() == NodeType.CLASS ;
//		isFeatureNode |= sn.type == NodeType.OPERATOR ;
//		isFeatureNode |= sn.type == NodeType.TECHOPERATOR ;
//		isFeatureNode |= sn.type == NodeType.TECH_PERIOD ;
//		isFeatureNode |= sn.type == NodeType.CHANGE ;
//		isFeatureNode |= sn.type == NodeType.SORT ;
		return isFeatureNode ;
	}

	private double getPNodeWeight(PatternNode pnode) {
		double weight = this.getWeight(pnode.text());
		if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.index) {
			weight = 3 ;
		} else if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.oper) {
			weight = 2 ;
		} else if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.sort) {
			weight = 2 ;
		} else if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.techop) {
			weight = 2 ;
		} else if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.techperiod) {
			weight = 2 ;
		}else if(weight == 1.0 && pnode.getNodeType() == PatternNodeType.change) {
			weight = 2 ;
		}
		return weight;
	}
    
    private String replaceWord(String word, String info){
    	if(isStockNameOrCode(word, info)){
    		StrStrPair pair= new StrStrPair(STOCK, word) ;
    		this.numStockPtnValPairList.add(pair) ;
    		return STOCK ;
    	}else if( isFundNameOrCode(word, info) ){
    	    StrStrPair pair= new StrStrPair(FUND, word) ;
            this.numStockPtnValPairList.add(pair) ;
            return FUND ;
    	}else if( isHKStockNameOrCode(word, info) ){
            StrStrPair pair= new StrStrPair(HKSTOCK, word) ;
            this.numStockPtnValPairList.add(pair) ;
            return HKSTOCK ;
        }
    	else if(isNumText(word, info)){
    		return replaceNumWord(word, info) ;
    	}
    	return word ;
    }

    private boolean isFundNameOrCode( String word, String info ){
        boolean isFundNameOrCode = info.contains("prop=_基金代码") ;
        isFundNameOrCode |= info.contains("prop=_基金简称") ;
        isFundNameOrCode &= !info.contains("onto_trigger:prop=_基金简称") ;
        return isFundNameOrCode;
    }
    
    private boolean isHKStockNameOrCode( String word, String info ){
        boolean yes = info.contains("prop=_港股代码") ;
        yes |= info.contains("prop=_港股简称") ;
        yes &= !info.contains("onto_trigger:prop=_港股简称") ;
        return yes;
    }
    
    private String replaceNumWord(String word, String info) {
        double num;
        Matcher match ;
        if(NUM_REGEX_PTN.matcher(word).matches()){
        	/** 单纯数字*/
        	String numStr = word ;
            num = Double.valueOf(word);
            word =  num > 1000 ? BIG : SML;
            this.numStockPtnValPairList.add(new StrStrPair(word, numStr)) ;
        } else if((match = MATH_PERCENT_NUM_REGEX_PTN.matcher(word)).matches() ||
        		(match = CN_PERCENT_NUM_REGEX_PTN.matcher(word)).matches()){
        	/** 百分比 */
        	String numStr = match.group(1) ;
        	num = Double.valueOf(numStr) ;
        	String repStr = num > 1000 ? BIG : SML;
        	word = word.replace(numStr, repStr) ;
        	StrStrPair pair = new StrStrPair(repStr, numStr) ;
        	this.numStockPtnValPairList.add(pair) ;
        } else if (MA_REGEX_1_PTN.matcher(word).find()
                || MA_REGEX_2_PTN.matcher(word).find()) {
            /** ma */
            match = MA_REGEX_1_PTN.matcher(word).find() ? MA_REGEX_1_PTN
                    .matcher(word) : MA_REGEX_2_PTN.matcher(word);
            while (match.find()) {
                String numStr = match.group(1);
                num = Double.valueOf(numStr);
                String repWord = num > 1000 ? BIG : SML;
                word = word.replaceFirst(numStr, repWord);
                StrStrPair pair = new StrStrPair(SML, numStr);
                this.numStockPtnValPairList.add(pair);
            }
        } else if((match = DOUBLE_WITH_CHINESE_NUM_PTN.matcher(word)).find()){
        	/** e.g. 3.5亿 */
        	match = null ;
        	match = DOUBLE_WITH_CHINESE_NUM_PTN.matcher(word) ;
        	while(match.find()) {
        		String numStr = match.group(1);
            	try {
            		NumRange range = NumParser.getNumRangeFromStr(numStr) ;
            		num = range.getDoubleTo() ;
            		String repStr = num > 1000 ? BIG : SML;
					word = word.replaceFirst(numStr, repStr) ;
					StrStrPair pair = new StrStrPair(repStr, numStr) ;
					this.numStockPtnValPairList.add(pair) ;
				} catch (NotSupportedException e) {
					logger_.warn(
	                        String.format("sent[%s]formatError\n[%s]", word, e.getMessage()));
				}
        	}
        } else if(word.matches(N_CHENG_REGEX)){
        	String numStr = word ;
        	word = SML ;
        	StrStrPair pair = new StrStrPair(word, numStr) ;
        	this.numStockPtnValPairList.add(pair) ;
        } else if(word.matches(CN_POINT_REGEX)){
        	String numStr = word ;
        	word = SML ;
        	StrStrPair pair = new StrStrPair(word, numStr) ;
        	this.numStockPtnValPairList.add(pair) ;
        } else if(word.matches(NUM_CASE_REGEX)){
        	String numStr = word ;
        	word = BIG ;
        	StrStrPair pair = new StrStrPair(word, numStr) ;
        	this.numStockPtnValPairList.add(pair) ;
        } else {
        	match = null ;
            match = CHNUM_REGEX_PTN.matcher(word);
            if(!match.find()) { return word; }
            match = CHNUM_REGEX_PTN.matcher(word);
            try {
            	while(match.find()) {
            		String numStr = match.group(0);
            		num = getArabicAsDouble(numStr);
            		String ptn = null ;
            		if(num > 1000){
            			word = word.replaceFirst(numStr, BIG);
            			ptn = BIG ;
            		}else{
            			word = word.replaceFirst(numStr, SML); 
            			ptn = SML ;
            		}
            		StrStrPair pair = new StrStrPair(ptn, numStr) ;
            		this.numStockPtnValPairList.add(pair) ;
            	}
            } catch (NotSupportedException e) {
            	if(word.matches(CN_FUZZY_GT_1000)) {
            		String numStr = word ;
            		word = BIG ;
            		StrStrPair pair = new StrStrPair(BIG, numStr) ;
            		this.numStockPtnValPairList.add(pair) ;
            	} else if(word.matches(CN_FUZZY_LT_1000)) {
            		String numStr = word ;
            		StrStrPair pair = new StrStrPair(SML, numStr) ;
            		this.numStockPtnValPairList.add(pair) ;
            		word = SML ;
            	} else {
            		logger_.warn(
            				String.format("sent[%s]formatError\n[%s]", word, e.getMessage()));
            	}
            }
        }
        return word;
    }

    /**
     * 判断是否为数字型的字符串
     * @param str
     * @return
     */
    public static boolean isNumberStr(String str){
        for(int i =0; i < str.length(); i++){
          if(!java.lang.Character.isDigit(str.charAt(i))){
            return false;
          }
        } 
        return true;
    }
    
    private boolean isNumText(String word, String info) {
        boolean isNumText = isNumberStr(word) || info.contains("date")
                || info.contains("num") || hasUnit(word)
                || MA_REGEX_1_PTN.matcher(word).find()
                || MA_REGEX_2_PTN.matcher(word).find();

        isNumText = isNumText || word.matches(N_CHENG_REGEX);
        isNumText = isNumText || word.matches(CN_POINT_REGEX);
        isNumText = isNumText || word.matches(NUM_CASE_REGEX);
        isNumText = isNumText
                || (word.matches(CHNUM_REGEX) && !info.contains("prop=_股票代码") && !info
                        .contains("prop=_股票代码"));
        return isNumText;
    }
	
	private boolean isStockNameOrCode(String word, String info){
		boolean isStockNameOrCode = info.contains("prop=_股票代码");
		isStockNameOrCode |= info.contains("prop=_股票简称");
		isStockNameOrCode |= info.contains("prop=_股票对象");
		isStockNameOrCode &= !info.contains("onto_trigger:prop=_股票简称") ;
		return isStockNameOrCode;
	}

    private boolean hasUnit(String word) {
        return UNIT_REGEX_PTN.matcher(word).find();
    }
    
    /*public static class Result{
    	public Result(NormalizeTool normalizeTool) {
    		this.npattern = normalizeTool.nPattern ;
    		if(normalizeTool.segValueList() != null) {
    			List<HashMap<String, Double>> segMap = new ArrayList<HashMap<String,Double>>() ;
    			for(Pair<String, Double> pair : normalizeTool.segValueList()){
    				HashMap<String, Double> map = new HashMap<String, Double>() ;
    				map.put(pair.first, pair.second) ;
    				segMap.add(map) ;
    			}
    			this.segger = segMap ;
    		}
    		this.indexes = normalizeTool.indexList ;
    		this.nodelist = normalizeTool.pNodeList ;
		}
		String npattern ;
    	List<HashMap<String,Double>> segger ;
    	List<String> indexes ;
    	List<PatternNode> nodelist ;
    }*/
    
    public static Double getArabicAsDouble(String chineseNumber) throws NotSupportedException
    {
        Double chineseVal = Double.valueOf(getArabic(chineseNumber));
        
        return chineseVal;
    }
    
    /**
     * 将中文数字转为阿拉伯数字
     * @param String chineseNumber 中文数字
     * @return String ArabicNumber 阿拉伯数字，若无法转换，返回-1或原始字符串
     * @throws UnexpectedException
     * @throws NotSupportedException 
     */
    static Pattern FORM_P1 = Pattern.compile("^(\\d+?)([十百千万亿仟佰拾]+?)$");
    public static String getArabic(String chineseNumber) throws NotSupportedException
              {
        chineseNumber = chineseNumber.replace("个", "");
        if (chineseNumber.indexOf("十") == -1
                && chineseNumber.indexOf("拾") == -1
                && chineseNumber.indexOf("百") == -1
                && chineseNumber.indexOf("佰") == -1
                && chineseNumber.indexOf("千") == -1
                && chineseNumber.indexOf("仟") == -1
                && chineseNumber.indexOf("万") == -1
                && chineseNumber.indexOf("亿") == -1) {
            chineseNumber = chineseNumber.replace("玖", "9");
            chineseNumber = chineseNumber.replace("捌", "8");
            chineseNumber = chineseNumber.replace("柒", "7");
            chineseNumber = chineseNumber.replace("陆", "6");
            chineseNumber = chineseNumber.replace("伍", "5");
            chineseNumber = chineseNumber.replace("肆", "4");
            chineseNumber = chineseNumber.replace("叁", "3");
            chineseNumber = chineseNumber.replace("贰", "2");
            chineseNumber = chineseNumber.replace("壹", "1");
            chineseNumber = chineseNumber.replace("零", "0");
            chineseNumber = chineseNumber.replace("九", "9");
            chineseNumber = chineseNumber.replace("八", "8");
            chineseNumber = chineseNumber.replace("七", "7");
            chineseNumber = chineseNumber.replace("六", "6");
            chineseNumber = chineseNumber.replace("五", "5");
            chineseNumber = chineseNumber.replace("四", "4");
            chineseNumber = chineseNumber.replace("三", "3");
            chineseNumber = chineseNumber.replace("两", "2");
            chineseNumber = chineseNumber.replace("二", "2");
            chineseNumber = chineseNumber.replace("一", "1");
        } else if (FORM_P1.matcher(chineseNumber).matches()) {
            Matcher mid = FORM_P1.matcher(chineseNumber);
            mid.matches();
            int head = Integer.valueOf(mid.group(1));
            String chinesepart = chineseNumber.replace(mid.group(1), "一");
            chineseNumber = String.valueOf(head
                    * parseChineseNumber(chinesepart));
        } else {
            chineseNumber = chineseNumber.replace("0", "零");
            chineseNumber = chineseNumber.replace("9", "九");
            chineseNumber = chineseNumber.replace("8", "八");
            chineseNumber = chineseNumber.replace("7", "七");
            chineseNumber = chineseNumber.replace("6", "六");
            chineseNumber = chineseNumber.replace("5", "五");
            chineseNumber = chineseNumber.replace("4", "四");
            chineseNumber = chineseNumber.replace("3", "三");
            chineseNumber = chineseNumber.replace("2", "二");
            chineseNumber = chineseNumber.replace("1", "一");
            chineseNumber = String.valueOf(parseChineseNumber(chineseNumber));
        }
        return chineseNumber;
    }

    
    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     * @param chineseNumber 中文数字
     * @return 阿拉伯数字(Integer),如果是无法识别的中文数字则返回-1
     * @throws NotSupportedException 
     * @throws UnexpectedException
     */
    public static long parseChineseNumber(String chineseNumber) throws NotSupportedException {
        chineseNumber = chineseNumber.replace("两", "二");
        chineseNumber = chineseNumber.replace("仟", "千");
        chineseNumber = chineseNumber.replace("佰", "百");
        chineseNumber = chineseNumber.replace("拾", "十");
        chineseNumber = chineseNumber.replace("玖", "九");
        chineseNumber = chineseNumber.replace("捌", "八");
        chineseNumber = chineseNumber.replace("柒", "七");
        chineseNumber = chineseNumber.replace("陆", "六");
        chineseNumber = chineseNumber.replace("伍", "五");
        chineseNumber = chineseNumber.replace("肆", "四");
        chineseNumber = chineseNumber.replace("叁", "三");
        chineseNumber = chineseNumber.replace("贰", "二");
        chineseNumber = chineseNumber.replace("壹", "一");
        if (chineseNumber.startsWith("百") || chineseNumber.startsWith("千")
                || chineseNumber.startsWith("万")
                || chineseNumber.startsWith("亿")) {
            chineseNumber = "一" + chineseNumber;
        }
        return parseChineseNumber(chineseNumber, 1);
    }
    
    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     * 
     * @param preNumber 第二大的位数
     * @param chineseNumber 中文数字
     * @throws NotSupportedException 
     * @throws UnexpectedException
     */
    private static long parseChineseNumber(String chineseNumber, int preNumber) throws NotSupportedException {
        long ret = 0;
        if (chineseNumber.indexOf("零") == 0) {
            int index = 0;
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1);
        } else if (chineseNumber.indexOf("亿") != -1) {
            int index = chineseNumber.indexOf("亿");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100000000
                    + parseChineseNumber(postfix, 10000000);
        } else if (chineseNumber.indexOf("万") != -1) {
            int index = chineseNumber.indexOf("万");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 10000
                    + parseChineseNumber(postfix, 1000);
        } else if (chineseNumber.indexOf("千") != -1) {
            int index = chineseNumber.indexOf("千");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 1000
                    + parseChineseNumber(postfix, 100);
        } else if (chineseNumber.indexOf("百") != -1) {
            int index = chineseNumber.indexOf("百");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100
                    + parseChineseNumber(postfix, 10);
        } else if (chineseNumber.indexOf("十") != -1) {
            if (chineseNumber.indexOf("十") != 0) {
                int index = chineseNumber.indexOf("十");
                int end = chineseNumber.length();
                String prefix = chineseNumber.substring(0, index);
                String postfix = chineseNumber.substring(index + 1, end);
                ret = parseChineseNumber(prefix, 1) * 10
                        + parseChineseNumber(postfix, 1);
            } else {
                chineseNumber = "一" + chineseNumber;
                int index = chineseNumber.indexOf("十");
                int end = chineseNumber.length();
                String prefix = chineseNumber.substring(0, index);
                String postfix = chineseNumber.substring(index + 1, end);
                ret = parseChineseNumber(prefix, 1) * 10
                        + parseChineseNumber(postfix, 1);
            }
        } else if (chineseNumber.equals("一")) {
            ret = 1 * preNumber;
        } else if (chineseNumber.equals("二")) {
            ret = 2 * preNumber;
        } else if (chineseNumber.equals("三")) {
            ret = 3 * preNumber;
        } else if (chineseNumber.equals("四")) {
            ret = 4 * preNumber;
        } else if (chineseNumber.equals("五")) {
            ret = 5 * preNumber;
        } else if (chineseNumber.equals("六")) {
            ret = 6 * preNumber;
        } else if (chineseNumber.equals("七")) {
            ret = 7 * preNumber;
        } else if (chineseNumber.equals("八")) {
            ret = 8 * preNumber;
        } else if (chineseNumber.equals("九")) {
            ret = 9 * preNumber;
        } else if (chineseNumber.equals("")) {
            ret = 0;
        } else {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_NUM_FMT,
                    chineseNumber);
        }
        return ret;
    }
}
