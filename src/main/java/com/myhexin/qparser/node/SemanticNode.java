package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myhexin.DB.mybatis.mode.NodeMerge;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.chunk.Chunk;
import com.myhexin.qparser.define.EnumDef.HiddenType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.IndexMultPossibility;
import com.myhexin.qparser.phrase.OutputResult;
import com.myhexin.qparser.phrase.util.NodeWeightLevel;

/**
 * The Class SemanticNode.
 */
public abstract class SemanticNode { // implements Cloneable
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SemanticNode.class);
	
	
	/**
     * Instantiates a new semantic node.
     * 
     * @param text the text
     */
    public SemanticNode(String text){
        this.text = text;
    }
    public SemanticNode(){
    	
    }

    protected abstract SemanticNode copy();

    //TODO 过渡阶段使用
    private boolean isSkipOldDateParser = false;
    
    
    
    public boolean isSkipOldDateParser() {
		return isSkipOldDateParser;
	}
	public void setSkipOldDateParser(boolean isSkipOldDateParser) {
		this.isSkipOldDateParser = isSkipOldDateParser;
	}

	private NodeMerge nodeMerge;
    public void setNodeMergeInfo(NodeMerge nodeMerge) {
    	this.nodeMerge = nodeMerge;
    }
    
    public NodeMerge getNodeMerge() {
    	return nodeMerge;
    }
    
    protected void copy(SemanticNode rtn) {
    	
    	//基本类型
    	rtn.currentPos=currentPos;
    	rtn.hiddenType=hiddenType;
    	rtn.idList=idList;
    	
    	if(asOneNodes!=null)
    	{
    		rtn.asOneNodes=new ArrayList<SemanticNode>(asOneNodes.size());
    		for(SemanticNode n : asOneNodes)
    		{
    			rtn.asOneNodes.add(n.copy());
    		}
    	}
    	
    	if(canDelIfExistIndexStrs_!=null)
    	{
    		rtn.canDelIfExistIndexStrs_=new ArrayList<String>(canDelIfExistIndexStrs_);
    	}
    	
    	
    	if(groupNodes!=null){
    		rtn.groupNodes=new ArrayList<SemanticNode>(groupNodes.size());
    		for(SemanticNode n : groupNodes)
    		{
    			rtn.groupNodes.add(n.copy());
    		}
    	}
    	
    	if(indexMultPossibility!=null) {
    		rtn.indexMultPossibility = new ArrayList<IndexMultPossibility>(indexMultPossibility.size());
    		for(IndexMultPossibility a: indexMultPossibility) {
    			rtn.indexMultPossibility.add(a.copy());
    		}
    	}
    	if(thematics!=null) {
    		rtn.thematics=new ArrayList<String>(thematics);
    	}
    	rtn.isBoundToIndex=isBoundToIndex;
    	rtn.bindToInfo = bindToInfo;
    	rtn.isBoundToSynt=isBoundToSynt;
    	rtn.isCombined=isCombined;
    	rtn.isExecutive=isExecutive;
    	rtn.keywordNodeStartPos=keywordNodeStartPos;
    	rtn.lightParserResult=lightParserResult;
    	if(multResult!=null)
    	{
    		rtn.multResult=multResult.copy();
    	}
    	
    	if(ofChunk_!=null)
    	{
    		rtn.ofChunk_=ofChunk_.copy();
    	}
    	
    	rtn.origChunk=origChunk;
    	rtn.reportSearch=reportSearch;
    	rtn.score=score;
    	//rtn.standardStatement=standardStatement;
    	rtn.syntacticNum=syntacticNum;
    	rtn.text=text;
    	
    	rtn.type=type;

    }
    
    public void setOrigChunk(String origChunk) {
    	this.origChunk = origChunk;
    }
    
    /**
     * Parses the node.
     * 
     * @param k2v the k2v containing all necessary node specific info
     * @param qtype TODO
     * @throws QPException the qP exception
     */
    public abstract void parseNode(HashMap<String, String> k2v, Query.Type qtype)
            throws QPException;
    
    public String toString() {
        return "NodeType:"+type+"  NodeText:"+text;
    }
    
    public boolean isCombined() {
    	return isCombined;
    }
    
    /**
     * 获得方便用户理解的描述文字。默认返回{@link #text}，子类可覆盖此方法。
     * 例如{@link StrNode#getPubText}
     * @return
     */
    public String getPubText() {
        return text;
    }

    
    /**
     * 此方法被覆盖但仅是调用父类{@link Object#equals(Object)}，实质意思是不要覆盖此方法。
     * 因为其子类完全有不同需求，若没有再次覆盖，有时导致难以发现的bug。
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    /**
     * 此方法被覆盖但仅是调用父类{@link Object#hashCode()}，实质意思是不要覆盖此方法。
     * 因为其子类完全有不同需求，若没有再次覆盖，有时导致难以发现的bug。
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public Chunk getChunk() { return ofChunk_; }
    public void  setChunk(Chunk chunk) { this.ofChunk_ = chunk; }
    
    /** The text. */
    protected String text = "";
    public boolean isCombined = false;//是否被合并了
    
    /** The type. */
    public NodeType type = NodeType.UNKNOWN;
    /** 是否为隐含节点。如果是系统推导出来的为true，例如：pattern替换时的直接给定的数值、指标、时间等*/
    public HiddenType hiddenType = HiddenType.NON_HIDDEN;
    
    protected Chunk ofChunk_ = Chunk.NaC;
    
    /**
     * 歧义识别中，标记关键词节点是否可删除
     * */
    protected List<String> canDelIfExistIndexStrs_ = null;
    public List<String> getCanDelIndexStrs()
    {
    	return this.canDelIfExistIndexStrs_;
    }
    public void addCanDelIndexStr(String idx)
    {
    	if (this.canDelIfExistIndexStrs_ == null)
    	{
    		this.canDelIfExistIndexStrs_ = new ArrayList<String>();
    	}
    	this.canDelIfExistIndexStrs_.add(idx);
    }
    
    // 标记次节点是否被绑定到句式
    public boolean isBoundToSynt = false;
    // 标记此节点是否被绑定到指标
    private boolean isBoundToIndex = false;
    private BindToInfo bindToInfo = null;

	static class BindToInfo {
    	private SemanticNode boundToIndex;
    	private SemanticNode boundToIndexProp;
    }
    
    // 以下变量在问句的第一个节点中作为
    public int score = -1; // 最后打分
    public int syntacticNum = 0; // 问句中句式个数
    public int currentPos = 0; // 当前开始匹配的位置
    public int keywordNodeStartPos = 0; // 当前关键字开始匹配的位置
    //private String standardStatement = "";
    private OutputResult multResult = null;
    private String origChunk = "";
    private List<IndexMultPossibility> indexMultPossibility = null;
    //public String xmlResult = "";
    //public String jsonResult = "";
	//public String luaResult = ""; 
    //public String jsonResultOfMacroIndustry = ""; 
    //public String thematic = "";
    private List<String> thematics = null;
    private String lightParserResult = "";
    protected List<SemanticNode> asOneNodes;
    protected List<SemanticNode> groupNodes;
    protected String idList = "";
    protected String idSearch = "";
    protected String reportSearch = "";
    
    public void setThematics(List<String> thematics) {
    	this.thematics= thematics;
    }
    
    public void setLightParserResult(String s) {
    	this.lightParserResult = s;
    }
    
    public List<String> getThematics() {
    	return thematics;
    }
    
    public boolean thematicContain(String s) {
    	if(thematics==null) {
    		return false;
    	}else{
    		return thematics.contains(s);
    	}
    }
    
    public String getOrigChunk() {
    	return origChunk;
    }
    
    public String getLightParserResult() {
    	return lightParserResult;
    }
    
    public void setIndexMultPossibility(List<IndexMultPossibility> list) {
    	this.indexMultPossibility = list;
    }
    
    public List<IndexMultPossibility> getIndexMultPossibility() {
    	return indexMultPossibility;
    }
    
    public void setMultResult(OutputResult r) {
    	this.multResult = r;
    }

    
    public OutputResult getMultResult() {
    	return multResult;
    }
    
    public int getScore(){
    	return score;
    }
    public void setScore(int score )  {
    	this.score = score;
    }
    
    public void setIsBoundToIndex(boolean isBoundToIndex) {
    	this.isBoundToIndex = isBoundToIndex;
    }
    
    public void setBoundToIndexProp(SemanticNode boundToIndex, SemanticNode boundToIndexProp) {
    	if(this.bindToInfo ==null) {
    		this.bindToInfo  = new BindToInfo();
    	}
    	this.bindToInfo.boundToIndex = boundToIndex;
    	this.bindToInfo.boundToIndexProp = boundToIndexProp;
    }
    public String getBoundToIndexPropInfo() {
    	if(this.bindToInfo !=null) {
    		String s = "";
    		if(bindToInfo.boundToIndex!=null)
    		{
    			s += bindToInfo.boundToIndex.getText() + "->";
    		}
    		if(bindToInfo.boundToIndexProp!=null)
    		{
    			s += bindToInfo.boundToIndexProp.getText();
    		}else{
    			s+="null";
    		}
    		return s;
    	}else{
    		return "";
    	}
    }
    
    public SemanticNode getBoundToIndex() {
    	if(bindToInfo == null) {
    		return null;
    	}
    	return bindToInfo.boundToIndex;
    }
    
    public SemanticNode getBoundToIndexProp() {
    	if(bindToInfo == null) {
    		return null;
    	}
    	return bindToInfo.boundToIndexProp;
    }
    
    public boolean isBoundToIndex() {
    	return isBoundToIndex;
    }
    
    public boolean isBoundToSynt() {
    	return isBoundToSynt;
    }
    
    
    public void addGroupNode(SemanticNode snode) {
    	if(groupNodes == null) { groupNodes = new ArrayList<SemanticNode>(2); }
        groupNodes.add(snode);
    }
    
    public void addAsOneNode(SemanticNode snode) {
        if(asOneNodes == null) { asOneNodes = new ArrayList<SemanticNode>(2); }
        asOneNodes.add(snode);
    }
    
    public List<SemanticNode> getGroupNodes() { 
        return groupNodes == null ? Collections.<SemanticNode>emptyList() : groupNodes;
    }
    
	public int getSyntacticNum() {
		return syntacticNum;
	}

	public void setSyntacticNum(int syntacticNum) {
		this.syntacticNum = syntacticNum;
	}
	
	public void syntacticNumSelfIncrease(int increaseNum) { // 句式个数自增
		this.syntacticNum += increaseNum;
	}

	public int getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}
	
	public int getKeywordNodeStartPos() {
		return keywordNodeStartPos;
	}

	public void setKeywordNodeStartPos(int keywordNodeStartPos) {
		this.keywordNodeStartPos = keywordNodeStartPos;
	}

	//去除已经识别的句式中添加的两个boundary节点
	public int getOriginalPos() { // 获得原始节点中当前开始的位置
		return currentPos - 2*syntacticNum;
	}
	
/*	public String getStandardStatement() {
		return standardStatement;
	}
	public void setStandardStatement(String standardStatement) {
		warn(this, String.format("setStandardStatement(String)")) ;
		this.standardStatement = standardStatement;
	}*/
	
	// 获得节点文本
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	// 获得节点类型
	public NodeType getType() {
		return type;
	}
	public boolean isNestedSemanticNode() {
		if (type == NodeType.NESTED)
			return true;
		return false;
	}
	public boolean isFocusNode() {
		if (type == NodeType.FOCUS)
			return true;
		return false;
	}
	public boolean isIndexNode() {
		if (type == NodeType.FOCUS && ((FocusNode) this).hasIndex())
			return true;
		return false;
	}
	public boolean isClassNode() {
		if (type == NodeType.CLASS)
			return true;
		return false;
	}
	public boolean isDateNode() {
		if (type == NodeType.DATE)
			return true;
		return false;
	}
	public boolean isTimeNode(){
		if (type == NodeType.TIME)
			return true;
		return false;
	}
	
	public boolean isTechPeriodNode(){
		if (type == NodeType.TECH_PERIOD)
			return true;
		return false;
	}

	public boolean isConsistPeriodNode(){
		return type == NodeType.CONSIST_PERIOD;
	}

	public boolean isNumNode() {
		if (type == NodeType.NUM)
			return true;
		return false;
	}
	public boolean isStrNode() {
		if (type == NodeType.STR_VAL)
			return true;
		return false;
	}
	public boolean isBoundaryNode(){
		if(type == NodeType.BOUNDARY)
			return true;
		return false;
	}
	public boolean isUnknownNode() {
		if (type == NodeType.UNKNOWN)
			return true;
		return false;
	}public String getIdList() {
		return idList;
	}
	public void setIdList(String idList) {
		this.idList = idList;
	}
	public String getIdSearch() {
		return idSearch;
	}
	public void setIdSearch(String idSearch) {
		this.idSearch = idSearch;
	}
	public String getReportSearch() {
		return reportSearch;
	}
	public void setReportSearch(String reportSearch) {
		this.reportSearch = reportSearch;
	}


	public boolean isExecutive = false;


	public boolean valueTypeIsMatchOf(PropNodeFacade core) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public String getModification() {
		return null;
	}
	
	/**
	 * 按照Node类型和规则给出Node等级
	 * @return weight level
	 */

	private int syntacticNodeWeight = -1;
	private int searchPropWeight = -1;
	
	public int getNodeTypeWeight() {
	  int syntNodeWeight = getSyntacticWeight();
	  if (syntNodeWeight != -1) {
	    return syntNodeWeight;
	  }
	  
	  int propSearchWeight = getSearchPropWeight();
	  if (propSearchWeight != -1) {
      return propSearchWeight;
    }
	  
	  if (this.isStrNode() && ((StrNode) this).hasSubType("_疑问词"))
	    return NodeWeightLevel.USELESS_LEVEL;
	  
	  if (!this.isUnknownNode()) {
  	  if (this.isIndexNode())
  	    return NodeWeightLevel.KEY_LEVEL;
  	  else if (this.isStrNode() && ((StrNode) this).hasSubType(NodeWeightLevel.StrNodeSubType, false))
  	    return NodeWeightLevel.HIGH_LEVEL;
  	  else if (this.isTimeNode())
  	    return NodeWeightLevel.LOW_LEVEL;
  	  else
  	    return NodeWeightLevel.MIDUM_LEVEL;
	  } else {
	    return NodeWeightLevel.USELESS_LEVEL;
	  }
	      
	}
	
	public void setSyntacticWeight(int level) {
	  this.syntacticNodeWeight = level;
	}
	
	public int getSyntacticWeight() {
	  return syntacticNodeWeight;
	}
	 
  public void setSearchPropWeight(int level) {
    this.searchPropWeight = level;
  }
  
  public int getSearchPropWeight() {
    return searchPropWeight;
  }

  /**
   * node类型场景化输出
   * @return 
   */
  private Set<String> eventsSet;
  
  public Set<String> getEventsSet() {
    return this.eventsSet;
  }
  
  public String getEventsSetString() {
    StringBuffer sb = new StringBuffer();
    for (String event : eventsSet) {
      if (sb.length() > 0)
        sb.append(",");
      sb.append(event);
    }
    return sb.toString();
  }
  
  public void setEventsSet(Set<String> eventsSet) {
    this.eventsSet = eventsSet;
  }
  
  public boolean hasEvents() {
    return eventsSet != null && !eventsSet.isEmpty();
  }
  
	/**
	 * 
	 * @param sNode
	 * @return
	 */
	public boolean isBoundaryStartNode() {
		if (this.getType() == NodeType.BOUNDARY) {
			BoundaryNode bNode = (BoundaryNode) this;
			if (bNode.isStart()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param sNode
	 * @return
	 */
	public boolean isBoundaryEndNode() {
		if (this.getType() == NodeType.BOUNDARY) {
			BoundaryNode bNode = (BoundaryNode) this;
			if (bNode.isEnd()) {
				return true;
			}
		}
		return false;
	}
}
