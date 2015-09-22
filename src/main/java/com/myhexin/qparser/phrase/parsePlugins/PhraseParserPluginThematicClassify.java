package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.ThematicClassify.ThematicBasicInfos;
import com.myhexin.qparser.phrase.parsePlugins.ThematicClassify.ThematicCondition;
import com.myhexin.qparser.phrase.parsePlugins.ThematicClassify.ThematicConfInfo;
import com.myhexin.qparser.phrase.parsePlugins.ThematicClassify.ThematicMsg;

/**
 * 
 * thematic: 主题的,主旋律,题目的，语干的
 * 
 * TODO 重写
 * ThematicMsg 这个数据结构不对, 导致代码嵌套循环太多了
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-27
 *
 */
public class PhraseParserPluginThematicClassify extends
        PhraseParserPluginAbstract {
	
	public PhraseParserPluginThematicClassify() {
		super("Thematic_Classify");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		//Environment ENV = annotation.get(ParserKeys.EnvironmentKey.class);
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return thematicClassify(nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> thematicClassify(
	        ArrayList<SemanticNode> nodes) {
		
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		nodes.get(0).setThematics (getThematicType(nodes));
		
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		listEnv.put("thematic", nodes.get(0).getThematics().toString(), true);
    	}
		//nodes.get(0).thematic = nodes.get(0).thematics.toString();
		rlist.add(nodes);
		return rlist;
	}
	
	public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
        	if(qlist.get(i).get(0).type==NodeType.ENV){
        		Environment listEnv = (Environment) qlist.get(i).get(0);
        		if(listEnv.containsKey("thematic"))
        			sb.append(String.format("[match %d]: %s\n", i, listEnv.get("thematic", String.class, false)));
        	}
        }
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }

	private List<String> getThematicType(ArrayList<SemanticNode> nodes) {

		ArrayList<String> resultList = new ArrayList<String>();
		
		//参数对象
		ThematicBasicInfos tbi = new ThematicBasicInfos(nodes);

		
		//遍历配置
		for (ThematicMsg tm : ThematicConfInfo.thematicMsgs) {
			try {
				if(getOneThematic(nodes,tm,tbi))
					resultList.add(tm.name);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}
		return resultList;
	}


	public static boolean getOneThematic(ArrayList<SemanticNode> nodes, ThematicMsg tm, ThematicBasicInfos tbi)
	        throws UnexpectedException {
		if(tm.type.equals("just"))
			return getJustOneThematic(tm, tbi);
		else
		   return getOtherOneThematic(tm, tbi);
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-1 上午10:29:26
     * @description:  just 类型的 Thematic 	
     * 
     */
    private static boolean getJustOneThematic(ThematicMsg tm,
            ThematicBasicInfos tbi) {
    	
	    if (tbi.unknowns.size()>0 || tbi.textIndexMap.size()>1
	    		|| tbi.syntactics.size() > 1 || tbi.semantics.size()>1
	    		|| tbi.dates.size() > 0) {
			return false;
		}

	    //所有的指标 主题中都包含了
	    for (String index : tbi.textIndexMap.keySet()) {
	    	if(!isContain(tm, index))
	    		return false;
		}
	    
	    //所有的句式主题中都包含了
	    for (String syntactic : tbi.syntactics) {
	    	if(!isContain(tm, syntactic))
	    		return false;
		}
	    	    
	    //所有的语义主题中都包含了
	    for (String semantic : tbi.semantics) {
	    	if(!isContain(tm, semantic))
	    		return false;
		}
	    
	    return true;
    }


    private static boolean isContain(ThematicMsg tm, String somthing) {
	    for (ThematicCondition condition : tm.mustCondition) {
	    	if (condition.isContain(somthing)) {
	    		return true;
	    	}
	    }
	    for (ThematicCondition condition : tm.otherCondition) {
	    	if(condition.isContain(somthing))
	    		return true;
	    }
	    
	    return false;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-1 上午10:27:33
     * @description:  single  multiple all 类型的 Thematic
     * 
     */
    private static boolean getOtherOneThematic(ThematicMsg tm,
            ThematicBasicInfos tbi) {
	    int count = 0;
		//必须满足的条件
		for (ThematicCondition tc : tm.mustCondition) {
			if(!tc.isMatch(tbi))
				return false;
			count++;
		}
		
		if(isEnough(tm, count)) return true;
		
		//非必须满足的条件
		for (ThematicCondition tc : tm.otherCondition) {
			if(tc.isMatch(tbi)){
				count++;
				if(isEnough(tm, count)) return true;
			}
		}
		
		//全部匹配上
		if (count == tm.mustCondition.size()+tm.otherCondition.size()) {
			return true;
		}

		return false;
    }


    private static final boolean isEnough(ThematicMsg tm, int count) {
	    if ((count>=1 && tm.type.equals("single") ||
			 count>=2 && tm.type.equals("multiple"))) {
			return true;
		}
	    return false;
    }
}

