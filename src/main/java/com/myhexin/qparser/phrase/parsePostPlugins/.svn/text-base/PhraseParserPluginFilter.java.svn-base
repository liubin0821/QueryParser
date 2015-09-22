package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.util.Consts;

public class PhraseParserPluginFilter extends PhraseParserPluginAbstract{
	
	private static final String[] filterPatternIds = {"1639","1626","383","3275","1745"};
	
    public PhraseParserPluginFilter() {
        super("Filter_Some_Index");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return doFilters(nodes);
    }
    
    public ArrayList<ArrayList<SemanticNode>> doFilters(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	if(filterIndex(nodes))
    		nodes.get(0).score *= 0.9;
    	else if(isEvent(nodes) ) {
    		nodes.get(0).score *= 0.8;
    	}
    	//added by huangmin for 老系统无法使用新解析的结果问题
    	else if (isSpecialPattern(nodes)) {
    		nodes.get(0).score *= 0.9;
    	}
    	//added end
    	rlist.add(nodes);
		return rlist;
    }

	private boolean isEvent(List<SemanticNode> nodes) {
    	SemanticNode node = nodes.get(0);
    	if(node.getType() == NodeType.ENV) {
    		Environment envNode = (Environment)node;
    		String domain = envNode.getFirstDomain();
    		if(domain!=null && domain.equals(Consts.CONST_absXinxiDomain)) {
    			return true;
    		}
    	}
    	return false;
    }
	
    
	
	
    public Boolean filterIndex(List<SemanticNode> nodes) {//过滤指数
    	if (nodes == null || nodes.size() == 0)
    		return false;
    	
    	//检查一下是不是指数领域
    	/*SemanticNode node = nodes.get(0);
    	if(node.getType() == NodeType.ENV) {
    		Environment envNode = (Environment)node;
    		String domain = envNode.getFirstDomain();
    		if(domain!=null && domain.equals("abs_指数领域")) {
    			return true;
    		}
    	}*/
    	
    	
    	 IndexIteratorImpl it = new IndexIteratorImpl(nodes);
         while(it.hasNext()){
        	 FocusNode fn=it.next();
        	 ClassNodeFacade cn = fn.getIndex();
        	 if(cn.getCategorysAll().contains("指数"))
        		 return true;
         }
         return false;
    }
    
    /**
     * 针对一些特殊的句式，比如 index比index减少percentage以上 之类的，新解析的结果虽然正确，但是无法被老系统识别，因此需要降分，走老解析。
     * 
     * @param nodes
     * @return
     */
    private boolean isSpecialPattern(ArrayList<SemanticNode> nodes) {
    	if(nodes != null && nodes.size() > 0){
	    	for(SemanticNode snNode : nodes) {
	    		//判断是否是特殊的句式id，这些id是否可以考虑存入数据表或者文件之类的？
		    	if(snNode instanceof BoundaryNode ) {
		    		BoundaryNode bNode =  (BoundaryNode)snNode;
		    		String patternId = bNode.getSyntacticPatternId();
		    		boolean isStart = bNode.isStart();
		    		if(isStart && StringUtils.isNotEmpty(patternId) && ArrayUtils.contains(filterPatternIds, patternId)) {
		    			return true;
		    		}
		    	}
	    	}
    	}
    	return false;
    }
}

