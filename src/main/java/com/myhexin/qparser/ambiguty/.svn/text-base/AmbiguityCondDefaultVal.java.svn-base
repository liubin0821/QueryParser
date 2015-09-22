package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class AmbiguityCondDefaultVal extends AmbiguityCondAbstract{
	/**
     * init
     */
	public AmbiguityCondDefaultVal(){
		super("defaultval");
	}
	
	/**
     * 匹配一个条件。
     *
     * @param nodes query node list
     * @param start start
     * @param end end 
     * @param bStart boundary start
     * @param bEnd boundary end 
     * @param cNode 可能的class node
     * @return int 返回匹配结果
     *              －1 完全不合适，应该删除
     *              0 － 100 正确的可能性，100为最大
     */
    public double matchNode(ArrayList<SemanticNode> nodes, int start, int end, int bStart , int bEnd, ClassNodeFacade cNode, int cPos, String alias)
    {
    	//System.out.println("type:"+type_);
        double ret = 50;
        
        AmbiguityCondInfoDefaultVal acid = (AmbiguityCondInfoDefaultVal) getCondInfo(cNode, alias, type_);
        if(acid == null)
        	return ret;
        boolean isDefault = acid.defaultVal;
        
        // 附加的分数
        double scoreAdd = isDefault ? 10 : 0;
        //System.out.println("default score add:"+scoreAdd);
        ret = ret + scoreAdd;
        return ret <= 100 ? ret : 100;
    }
    
    /**
     * parse config file and set config conditionInfo to ClassNodeFacade
     */ 
	public void parseConfig(Aliases alias, ClassNodeFacade cn,List<?> list){
		AmbiguityCondInfoDefaultVal acid = new AmbiguityCondInfoDefaultVal();
		
		acid.defaultVal = alias.getIsDefault();
				
		addCondInfo(cn, alias.getLabel(), type_, acid);
		return;
    }
	
    /**
     * parse config file and set config conditionInfo to ClassNodeFacade
     */ 
	public void parseConfig(Node confNode, ClassNodeFacade cn, String alias)
    {
		AmbiguityCondInfoDefaultVal acid = new AmbiguityCondInfoDefaultVal();
		NodeList children = confNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeName().equals("default")) {
				acid.defaultVal = true;
			}
		}
		addCondInfo(cn, alias, type_, acid);
		return;
    }
}
