package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class AmbiguityCondAbstract {

	public String type_ = null;
	
    /**
     * init
     */
	public AmbiguityCondAbstract(String t)
	{
		this.type_ = t;
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
        return 50;
    }

    /**
     * parse config file and set config conditionInfo to ClassNodeFacade
     */ 
	public void parseConfig(Aliases alias, ClassNodeFacade cn,List<?> list)
    {
        return;
    }
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-11-6 上午11:24:04
	 * @description:  为了兼容OntoXmlReaderOldSystem 	
	 */
	@Deprecated
	public void parseConfig(Node confNode, ClassNodeFacade cn, String alias) {

		return;
	}

    public void addCondInfo(ClassNodeFacade cn, String alias, String type, AmbiguityCondInfoAbstract condInfo)
    {
        AmbiguityCondInfoSet infoSet = cn.getAmbiguityCondInfoSet();
        if(infoSet == null)
            return;
        infoSet.addCondInfo(alias, type, condInfo);
    }
    
    public AmbiguityCondInfoAbstract getCondInfo(ClassNodeFacade cn, String alias, String type)
    {
        AmbiguityCondInfoSet infoSet = cn.getAmbiguityCondInfoSet();
        if(infoSet == null)
            return null;
        return infoSet.getCondInfo(alias, type);
    }
    public String toString()
    {
        return "Cond:"+type_;
    }
    
	// 判断是否为分隔符
	protected boolean isSepWord(String text) {
		if (text == null)
			return false;
		if (Pattern.matches("(\\s|,|;|\\.|。)", text))
			return true;
		return false;
	}
}
