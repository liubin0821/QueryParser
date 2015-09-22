package com.myhexin.qparser.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.CondUseNewapi;
import com.myhexin.DB.mybatis.mode.ConfigTextLine;
import com.myhexin.DB.mybatis.mode.DataLine;
import com.myhexin.DB.mybatis.mode.DictIndexScore;
import com.myhexin.DB.mybatis.mode.QueryClass;
import com.myhexin.DB.mybatis.mode.SearchIdConfig;
import com.myhexin.DB.mybatis.mode.SearchIdPropertyConfig;
import com.myhexin.DB.mybatis.mode.SearchPropConfig;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.phrase.Timing;
import com.myhexin.qparser.resource.model.DomainInfo;
import com.myhexin.qparser.resource.model.IndexDefDateInfo;
import com.myhexin.qparser.resource.model.IndexIdNameMapInfo;
import com.myhexin.qparser.resource.model.MultilineTechopInfo;
import com.myhexin.qparser.resource.model.NodeMergeInfo;
import com.myhexin.qparser.resource.model.RefCodeInfo;
import com.myhexin.qparser.resource.model.RenameMapInfo;
import com.myhexin.qparser.resource.model.ResourceInterface;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.resource.model.TechIndexParentChildInfo;
import com.myhexin.qparser.resource.model.UserDefineIndexInfo;
import com.myhexin.qparser.similarity.CodesInfosResource;
import com.myhexin.qparser.similarity.NPatternInfos;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.util.cfgdate.CfgDateLogicInstance;
import com.myhexin.server.Parser;


/**
 * 重构
 * 资源单例,准备替换掉Resource.java
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-30
 *
 */
public class ResourceInst {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	
	private static List<ResourceInterface> resourceList = new ArrayList<ResourceInterface>();
	static {
		resourceList.add(RefCodeInfo.getInstance());
		
		//DomainInfo中用到了refCode中内容,所以这个要在RefCodeInfo之后
		resourceList.add(DomainInfo.getInstance());
		resourceList.add(IndexDefDateInfo.getInstance());
		
		resourceList.add(IndexIdNameMapInfo.getInstance());
		resourceList.add(MultilineTechopInfo.getInstance());
		resourceList.add(NodeMergeInfo.getInstance());
		resourceList.add(RenameMapInfo.getInstance());
		resourceList.add(SemanticCondInfo.getInstance());
		resourceList.add(TechIndexParentChildInfo.getInstance());
		
		
		//CfgDateLogicInstance中用到了refCode中内容,所以这个要在RefCodeInfo之后
		resourceList.add(CfgDateLogicInstance.getInstance());
		resourceList.add(NPatternInfos.getInstance());
		resourceList.add(CodesInfosResource.getInstance());
		
		
		resourceList.add(UserDefineIndexInfo.getInstance());
	}
	
	//不用ResourceKeys
	private Map<String, List<Integer> > dictTextTypeIdMap = null;
	private Map<String, Integer> dictIndexScoreMap = null;
	private List<String> ignoreWordList;
	private List<CondUseNewapi> useNewapiInfos;
	private Boolean isAllUseNewapi = new Boolean(false);
	private Boolean isAllUseOldApi = new Boolean(false);
	private Map<String,List<SearchIdConfig>> searchIdConfigs;
	private Map<String, String> pluginDescMap;
	private Map<String, QueryClass> qcMap;
	private Map<String, SearchPropConfig> propWeightMap;
	private Map<String, SearchIdPropertyConfig> searchIdPropertyMap;
	
	public Map<String, SearchIdPropertyConfig> getSearchIdPropertyMap() {
	  return searchIdPropertyMap;
	}
	
	public Map<String, SearchPropConfig> getPropWeightMap() {
    return propWeightMap;
  }
	public Map<String, QueryClass> getQcMap() {
    return qcMap;
  }
  public List<String> getIgnoreWordList() {
		return ignoreWordList;
	}
	public Map<String, List<SearchIdConfig>> getSearchIdConfigs() {
		return searchIdConfigs;
	}
	public Map<String, String> getPluginDescMap() {
		return pluginDescMap;
	}
	public List<CondUseNewapi> getUseNewapiInfos() {
		return useNewapiInfos;
	}
	public boolean isAllUseNewapi() {
		return isAllUseNewapi;
	}
	
	public boolean isAllUseOldApi() {
		return isAllUseOldApi;
	}
	
	
	private ResourceInst(){}
	private static ResourceInst instance = new ResourceInst();
	public static ResourceInst getInstance() {
		return instance;
	}
	private final static String CONST_ALL_USE_NEW_API = "ALL_USE_NEW_API";
	private final static String CONST_ALL_USE_OLD_API = "ALL_USE_OLD_API";
	public void reloadResource() {
		logger_.info("reloadResource start...");
		StringBuilder log = new StringBuilder();
		
		Timing t = new Timing();
		for(ResourceInterface val : resourceList) {
			t.start();
			val.reload();
			t.end();
			log.append(String.format("\n[%dms] %s\n", t.mills(), val.getClass().getSimpleName())); 
		}
		
		//text文件被移到数据库,把text文件从数据库加载出来
		t.start();
		List<ConfigTextLine>  allCondfigLines = mybatisHelp.getDateParserInfoMapper().selectAllConfigFileLines();
		Map<String, List<ConfigTextLine>> configLineMap = new HashMap<String, List<ConfigTextLine>>();
		for(ConfigTextLine line : allCondfigLines) {
			List<ConfigTextLine> list = configLineMap.get(line.getFileName());
			if(list==null) {
				list = new ArrayList<ConfigTextLine>();
				configLineMap.put(line.getFileName(), list);
			}
			list.add(line);
		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "selectAllConfigFileLines")); 
		
		//可忽略词
		t.start();
		List<ConfigTextLine> ignoreWords = configLineMap.get("ignorable_words.txt");
		ignoreWordList = loadIgnorableWords(ignoreWords);
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "ignorable_words.txt")); 
		
		//指标计算表达式
		/*t.start();
		List<IndexCalcExpr> calcList = mybatisHelp.getDateParserInfoMapper().selectCalcExpr();
		Map<String, IndexCalcExpr> exprMap = new HashMap<String, IndexCalcExpr>();
		if(calcList!=null) {
			for(IndexCalcExpr expr : calcList) {
				exprMap.put(expr.getIndex_name(), expr);
			}
		}
		this.set(ResourceKeys.KeyExprMap.class , exprMap);
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "IndexCalcExpr")); */
		
		
		//走新解析的指标名称
		//TODO 删除这段代码,因为解析已经给出领域
		//配置信息, 哪些指标的时候使用新解析, query_type=zhishu
		t.start();
		useNewapiInfos = mybatisHelp.getDateParserInfoMapper().selectCondUserNewapiInfos();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "CondUseNewapi")); 
		
		t.start();
		isAllUseNewapi = new Boolean(false);
		isAllUseOldApi = new Boolean(false);
		if(useNewapiInfos!=null) {
			for(CondUseNewapi c : useNewapiInfos) {
				if(c.getInclude_names()!=null && c.getInclude_names().equals(CONST_ALL_USE_NEW_API)) {
					isAllUseNewapi = true;
					break;
				}else if(c.getInclude_names()!=null && c.getInclude_names().equals(CONST_ALL_USE_OLD_API)) {
					isAllUseOldApi = true;
					break;
				}
				c.loadIntoMap();
			}
		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "isAllUseNewapi, isAllUseOldApi")); 

		//dingchen语义配置
		t.start();
		Map<String,List<SearchIdConfig>> temp_searchIdConfigs = new HashMap<String, List<SearchIdConfig>>();
		String[] channelArray = {"news","hotnews", "report","pubnote","navigation","usersite","investqa","weibo","blog","guba","forum","product","video", "info","stockrecommend"};
		for(String channel : channelArray){
			temp_searchIdConfigs.put(channel, new ArrayList<SearchIdConfig>());
		}
		
		List<SearchIdConfig> configs = mybatisHelp.getSearchIdConfigMapper().selectAll();
		for(SearchIdConfig config:configs){
			if(config!=null && config.getChannel()!=null && config.getChannel().length()>0){
				for(String channel : channelArray){
					if(config.getChannel().contains(channel)){
					  temp_searchIdConfigs.get(channel).add(config);
					}
				}
			}
		}
		this.searchIdConfigs = temp_searchIdConfigs;
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "dingchen语义配置")); 
		
		//dingchen id名字信息
		t.start();
		List<QueryClass> qcList = mybatisHelp.getQueryClassMapper().selectAll();
		qcMap = new HashMap<String, QueryClass>();
		for (QueryClass qc : qcList) {
		  String id = qc.getId().toString();
		  qcMap.put(id, qc);
		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "dingchen id名字信息"));
		
		//dingchen prop权重配置
		t.start();
		List<SearchPropConfig> searchPropConfigs = mybatisHelp.getSearchPropConfigMapper().selectAll();
		propWeightMap = new HashMap<String, SearchPropConfig>();
		for (SearchPropConfig searchPropConfig : searchPropConfigs) {
		  String typeLabel = searchPropConfig.getTypeLabel();
		  propWeightMap.put(typeLabel, searchPropConfig);
		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "dingchen prop权重配置"));
		
		//liushichao id权重配置
		t.start();
		List<SearchIdPropertyConfig> searchIdPropertyConfigs = mybatisHelp.getSearchIdPropertyConfigMapper().selectAll();
		searchIdPropertyMap = new HashMap<String, SearchIdPropertyConfig>();
		for (SearchIdPropertyConfig searchIdPropertyConfig : searchIdPropertyConfigs) {
      String idNum = searchIdPropertyConfig.getIdnum();
      if (idNum != null) {
        String[] idNumSplits = idNum.split("&");
        searchIdPropertyMap.put(idNumSplits[idNumSplits.length-1], searchIdPropertyConfig);
      }
      
    }
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "liushichao id权重配置"));
		
		//clear up memory
		allCondfigLines=null;
		configLineMap=null;
		
		//解析插件详细信息
		t.start();
		pluginDescMap = getPluginDesc();
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "pluginDescMap")); 
		
		//词典指标优先级配置
		t.start();
		List<DictIndexScore> dictIndexScoreList = mybatisHelp.getDictIndexScoreMapper().list();
		dictIndexScoreMap = new HashMap<String, Integer>();
		if(dictIndexScoreList!=null && dictIndexScoreList.size()>0) {
			for(DictIndexScore dis : dictIndexScoreList) {
				if(dis.getDict_txt()!=null)
					dictIndexScoreMap.put(dis.getDict_txt() + "_&_" + dis.getIndex_txt(), dis.getScore());
				else if(dis.getDict_typeid()>0){
					dictIndexScoreMap.put("_typeid_" + dis.getDict_typeid() + "_&_" + dis.getIndex_txt(), dis.getScore());
				}
			}
		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "词典指标优先级配置")); 
		
		//词典词到typeId关系MAP
		t.start();
		int typeIdMapSize = mybatisHelp.getDataLinesMapper().countListTypeIdTexts();
		logger_.info("typeIdMapSize=" + typeIdMapSize);
		
		//现在typeid的数量已经超过10w,为了保证index消歧逻辑正常
		//暂时去掉10w的限制,风险是数据量越来越大,占用的内存太多
		//if(typeIdMapSize<=typeIdMapSizeLimitation) {
			List<DataLine> dataLines = mybatisHelp.getDataLinesMapper().listTypeIdTexts();
			dictTextTypeIdMap = new HashMap<String, List<Integer> >();
			if(dataLines!=null && dataLines.size()>0) {
				for(DataLine dl : dataLines) {
					List<Integer> typeIdList = dictTextTypeIdMap.get(dl.getText());
					if(typeIdList==null) {
						typeIdList = new ArrayList<Integer>();
						dictTextTypeIdMap.put(dl.getText(), typeIdList);
					}
					typeIdList.add(dl.getType_id());
				}
			}
		//		}else{
		//			logger_.info("typeIdMapSize不能超过: " + typeIdMapSizeLimitation);
		//			logger_.warn("[dict_index_score]配置了太多的typeId,使得初始化需要加载太多数据,已被代码发现并跳过");
		//		}
		t.end();
		log.append(String.format("[%dms] %s\n", t.mills(), "词典词到typeId关系MAP")); 
		//词典指标优先级配置-结束
		logger_.info(log.toString());
		logger_.info("reloadResource Completed!");
	}
	private final static int typeIdMapSizeLimitation = 100000;
	
	public Map<String, List<Integer> > getDictTextTypeIdMap() {
		return dictTextTypeIdMap;
	}
	
	public Map<String, Integer> getDictIndexScoreMap() {
		return dictIndexScoreMap;
	}
	
    /**
     * load可忽略词
     * @param lines
     */
    private List<String> loadIgnorableWords(List<ConfigTextLine> lines){
    	List<String> contentsSet = new ArrayList<String>() ;
    	for(ConfigTextLine line : lines){
    		String lineStr = line.getLine();
    		if(lineStr.startsWith("#")) {	continue ;}
    		if(lineStr.equals("break")){ break ; }
    		contentsSet.add(lineStr) ;
    	}
    	return contentsSet ;
    }
    
    private Map<String, String> getPluginDesc() {
    	Map<String, String> pluginMap = new HashMap<String, String>();
    	try {
			Document pluginXml = Util.readXMLFile(Param.PLUGIN_DETAIL, true);
			Element root = pluginXml.getDocumentElement();
			NodeList pluginItems = root.getChildNodes();
			
			for (int i = 1; i < pluginItems.getLength(); i++) {
				Node plugin = pluginItems.item(i);
				if (plugin.getNodeType() != Node.ELEMENT_NODE) {
	                continue;
	            }
				
				if (!plugin.getNodeName().equals("plugin")) {
	            	continue;
	            }
				
				NamedNodeMap nnm = plugin.getAttributes();       
	            String name = nnm.getNamedItem("name").getNodeValue();
	            String value = plugin.getTextContent();
	            value  = value.trim();
	            value = value.replaceAll("\n", "");
	            
	            pluginMap.put(name, value);
			}
		} catch (DataConfException e) {
			e.printStackTrace();
		}
    	
    	return pluginMap;
    }
    
    
    
    
}
