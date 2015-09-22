package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.QueryIndexWithProp;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticElementIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;


public class PhraseParserPluginCreateLuaResult extends PhraseParserPluginAbstract{
	
	
    public PhraseParserPluginCreateLuaResult() {
        super("Create_Lua_Result");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return createLuaResult(nodes);
    }
    
    public ArrayList<ArrayList<SemanticNode>> createLuaResult(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	String luaResult = getLuaResult(nodes);
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		listEnv.put("luaResult", luaResult, true);
    	}
    	//nodes.get(0).luaResult = luaResult;
    	rlist.add(nodes);
		return rlist;
    }
    
    
	
	public String getLuaResult(ArrayList<SemanticNode> nodes) {
		String results = getLuaResults(nodes);
		return results;
	}
	
    public String getLuaResults(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	String split = ","; // 多个句式之间，用“,”分隔，其最外层是and关系，用and.ftl表示
    	StringBuffer buffers = new StringBuffer();
    	
        Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(nodes);

        ToLuaExpressionV result = new ToLuaExpressionV();
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = iterator.next();
    		int i = 0;
    		Map<Integer, List<SemanticNode>> elements = new HashMap<Integer, List<SemanticNode>>();
    		Iterator<List<SemanticNode>> iteSyntacticElementNode = new SyntacticElementIteratorImpl(nodes, boundaryInfos.bStart);
    		while (iteSyntacticElementNode.hasNext()) {
    			List<SemanticNode> ele = iteSyntacticElementNode.next();
				elements.put(++i, ele);
			}
    		
    		SyntacticPattern synPattern = boundaryInfos.getSyntacticPattern();
    		if(synPattern != null)
    			getLuaResults(synPattern,elements,result);
    			//buffers.append(getLuaResults(synPattern,elements,result));
    		else
    			getLuaRestlt(boundaryInfos.syntacticPatternId,elements,result);  
    			//buffers.append(getLuaRestlt(boundaryInfos.syntacticPatternId,elements,result));  
    		
    		//if(iterator.hasNext())
    		//	buffers.append(split);  
    	}
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    	String luaExpression = mybatisHelp.getToLuaExpressionMapper().
                getLuaExpression(result.syntactics.toString(), result.semantics.toString(), result.indexs.toString());
		if(luaExpression == null)
		return "";
		
		luaExpression = replaceNumInLua(result.elements, luaExpression);
		
		StringBuilder logsb_ = getLogsb_(nodes);
		if(logsb_!=null) logsb_.append(buffers.toString()+"\n");
		
		return luaExpression;
		
    	//logsb_.append(buffers.toString()+"\n");
        //return buffers.toString();
    }
    


    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-8 下午5:39:22
     * @description:   	
     * @param syntacticPatternId
     * @param elements
     * @param result 
     * @return
     */
    private void getLuaRestlt(String patternId, Map<Integer, List<SemanticNode>> elements, ToLuaExpressionV result) {
		//String semantics = "";
    	  	
    	StringBuilder sb = new StringBuilder();
    	//LinkedHashMap<Integer, ValueType> numberArgument = new LinkedHashMap<Integer,ValueType>();
    	if (patternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {    					
    		if(elements.size()==2){
    			getOneElementIndexs(elements, sb, 1);
    			if(isNum(elements.get(2)))
    				result.elements.add(elements.get(2));
    				//numberArgument.put(2, ValueType.NUMBER);   			
    		}
		}
    	else if (patternId.equals(IMPLICIT_PATTERN.FREE_VAR.toString())) {
    		if(elements.size()==1){
    			getOneElementIndexs(elements, sb, 1);
    		}
    	}
		else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
			if(elements.size()==2){
    			getOneElementIndexs(elements, sb, 1);
    			if(isNum(elements.get(2)))
    				result.elements.add(elements.get(2));
    				//numberArgument.put(2, ValueType.NUMBER);  
    		}
		}
    	
    	addWithSplit(result.syntactics, patternId);
    	addWithSplit(result.semantics, patternId);   	
    	addWithSplit(result.indexs, sb.toString()); 
    	
    	/*
    	String indexs = sb.toString(); 
    	String luaExpression = MybatisHelp.toLuaExpressionMapper.getLuaExpression(patternId, patternId, indexs);
    	
    	luaExpression = replaceNumInLua(elements, numberArgument, luaExpression);
	    return luaExpression;*/
    }
    
	private final boolean isNum(List<SemanticNode> list) {
		for (SemanticNode sn : list)
			if (sn.type == NodeType.NUM)
				return true;

		return false;
	}

	private  void getLuaResults(SyntacticPattern synPattern, Map<Integer, List<SemanticNode>> elements, ToLuaExpressionV result) {		
    	
    	if(synPattern == null || elements == null ) return;
    	
    	//LinkedHashMap<Integer, ValueType> numberArgument = new LinkedHashMap<Integer,ValueType>();
    	LinkedHashMap<Integer, ValueType> indexArgument = new LinkedHashMap<Integer,ValueType>();
    	for (Integer i = 1, n = synPattern.getSyntacticElementMax() ;i<n;i++) {
    		SyntacticElement synElement = synPattern.getSyntacticElement(i);
    		if(synElement.getType() == SyntElemType.ARGUMENT){
    			SemanticArgument semArgument= synElement.getArgument();
    			switch (semArgument.getType()) {
				case CONSTANT:
				case CONSTANTLIST:
				case ANY:
					if (semArgument.isContainsValueType(ValueType.NUMBER, ValueType.LONG_NUM, ValueType.DOUBLE_NUM,
							ValueType.PERCENTAGE))
						result.elements.add(elements.get(i));
						//numberArgument.put(i, semArgument.getValueType());
					break;	
				case INDEX:	
				case INDEXLIST:
					//2015/04/22 chenhao:semArgument.getValueType() 改为 null
					//indexArgument传到getArgumentUsedIndex方法里ValueType并没有用到
					indexArgument.put(i, null);
				default:
					break;
				}
    		}
		}
    	
    	//String semantics = getSemanticIds(synPattern);
    	//String indexs = getArgumentUsedIndex(elements, indexArgument);   	
    	addWithSplit(result.syntactics, synPattern.getId());
    	addWithSplit(result.semantics, getSemanticIds(synPattern));   	
    	addWithSplit(result.indexs, getArgumentUsedIndex(elements, indexArgument));  
    	
    	
    	/*
    	String luaExpression = MybatisHelp.toLuaExpressionMapper.
    			                 getLuaExpression(synPattern.getId(), semantics, indexs);
     	if(luaExpression == null)
    		return "";
    	
    	luaExpression = replaceNumInLua(elements, numberArgument, luaExpression);
    	return luaExpression;
    	*/

    }
	
	private final void addWithSplit(StringBuilder sb, String str){
		if(sb.length() == 0)
			sb.append(str);
		else
			sb.append("_&_" + str);
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-8 下午7:45:20
     * @description:   	
     * @param elements
     * @param numberArgument
     * @param luaExpression
     * @return
     */
    private final String replaceNumInLua(List<List<SemanticNode>> NumElements, String luaExpression) {
	    Pattern lpp = Pattern.compile("([A-Z])");
    	for (List<SemanticNode> list : NumElements) {
    		for (SemanticNode sn : list)
    			if(sn.type == NodeType.NUM){
    				NumNode nn = (NumNode) sn;
    				Matcher m = lpp.matcher(luaExpression);
    				if(!m.find())
    					return "";
    				luaExpression = luaExpression.replaceAll(m.group(1), (int)nn.getFrom()+"");
    				
    				break;
    			}
		}
	    return luaExpression;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-9-30 下午1:36:32
     * @description:   	
     * @param synPattern
     */
    private  String getSemanticIds(SyntacticPattern synPattern) {
    	
    	return synPattern.getSemanticBind().getSemanticBindToIds();
    	
	    /*StringBuilder sb = new StringBuilder();
    	for(SemanticBindTo sbt : synPattern.getSemanticBind().getSemanticBindTos()){
    		if(sb.length() >0)
    			sb.append("&" + sbt.getBindToId());
    		else
    			sb.append(sbt.getBindToId());
    	}
    	return sb.toString();*/
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-9-30 上午11:24:42
     * @description:   	
     * @param elements
     * @param indexArgument
     */
    private  String getArgumentUsedIndex(Map<Integer, List<SemanticNode>> elements, 
    		LinkedHashMap<Integer, ValueType> indexArgument) {
	    StringBuilder sb = new StringBuilder();
    	for (Integer pos : indexArgument.keySet()) {
			getOneElementIndexs(elements, sb, pos);
		}

    	return sb.toString();
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-8 下午7:21:48
     * @description:   	
     * @param elements
     * @param sb
     * @param pos
     */
    private final void getOneElementIndexs(Map<Integer, List<SemanticNode>> elements, StringBuilder sb, Integer pos) {
	    for (SemanticNode sn : elements.get(pos)) {
	    	if(sn!= null && sn.type == NodeType.FOCUS){
	    		FocusNode fn = (FocusNode) sn;
	    		ClassNodeFacade cn = fn.getIndex();
	    		if(cn !=null)
	    			if(sb.length()>0)
	    				sb.append("_&_" + QueryIndexWithProp.getIndexWithProps(cn));
	    			else
	    				sb.append(QueryIndexWithProp.getIndexWithProps(cn));
	    	}
	    }
    }
    

    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }
}

class ToLuaExpressionV{
	StringBuilder indexs ;
	StringBuilder syntactics;
	StringBuilder semantics;
	List<List<SemanticNode>> elements;
	
	public ToLuaExpressionV(){
		indexs = new StringBuilder();
		syntactics = new StringBuilder();
		semantics = new StringBuilder();
		elements = new ArrayList<List<SemanticNode>>();
	}
}

