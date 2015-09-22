package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.tech.TechMisc;

class AmbiguityCondProps extends AmbiguityCondAbstract{

	private static final int propsSize = 10; //属性数量不应该影响到评分
    /**
     * init
     */
	public AmbiguityCondProps()
	{
        super("props");
	}
	
	private boolean isPropEqual(SemanticNode sNode, PropNodeFacade pn) {
		 boolean isPropEqual = false;
         if(sNode.type == NodeType.DATE && pn.isDateProp()) {
             isPropEqual = true;
         } else if(sNode.type == NodeType.STR_VAL && pn.isStrProp()) {
             StrNode stringNode = (StrNode) sNode;
             for (String st : stringNode.subType) {
             	if (pn.subTypeContain(st)) {
             		isPropEqual = true;
             		break;
             	}
             }
         } else if(sNode.type == NodeType.TECH_PERIOD && pn.getText().matches(TechMisc.TECH_ANALY_PERIOD)) {
             TechPeriodNode techPeriodNode = (TechPeriodNode) sNode;
             for (SemanticNode ofwhat : techPeriodNode.ofwhat) {
                 if (ofwhat.type == NodeType.PROP && pn.isTextEqual((PropNodeFacade) ofwhat)) {
                 	isPropEqual = true;
                 	break;
                 }
             }
         }
         return isPropEqual;
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
		// 信号包括：
		// 1、是否有属性匹配上 
		// 2、属性在boundary内外匹配上的个数
		// 3、属性匹配上的远近
    	//System.out.println("type:"+type_);
        double ret = 50;
        List<PropNodeFacade> propList = cNode.getAllProps();
        if (propList == null)
        	return ret;
        int boundaryIn = 0;		// 边界内有几个匹配上
        int boundaryOut = 0;	// 边界外有几个匹配上
        int containNum = 0;		// 属性列表中有几个匹配上
        double distances = 0;
        // 以下关于属性是否匹配的判断逻辑是否存在问题？
        // 1、指标的属性中，带_下划线的是什么意思？带$、!、+、*的又是什么意思？
        // 2、绑定属性到指标中有if(pn.text.charAt(0) == '_')continue;，而且对于是否同类型有更严格的判断，是否应该处理？
        // 3、type="DOUBLE"类型的在这里其实没有做匹配，是否存在问题？
        for(PropNodeFacade pn: propList)
        {
        	if(pn.isValueProp()) {
        		
        		//2015.01.26 
        		// 如果存在属性为_无值，即为UNKNOWN类型，只能是FREE_VAR的情况
        		/*if(pn.getValueType() == PropType.UNKNOWN) {
        			BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
        	        if (!bNode.getSyntacticPatternId().equals(IMPLICIT_PATTERN.FREE_VAR.toString())){
        	        	return -1;
        	        }
        		}*/
        		continue;
        	}
        	boolean isContain = false;
        	
        	//i=0改动,为的是今日 上证指数 收盘价,收盘价离今日太远,被消歧了
        	//TODO lxf 改成0是不准确的, 因为离的远的DateNode可能是修饰上证指数的, 而不是远的收盘价的
        	int i=end;
        	if(end>nodes.size()-1) {
        		i = nodes.size() - 1;
        	}
        	for(;i>=0;i--) {
        		 SemanticNode sNode = nodes.get(i);
        		 if(i<cPos && ( sNode==null || isSepWord(sNode.getText()))) {
        			 break;
        		 }
        		 
        		 boolean isPropEqual = isPropEqual(sNode, pn);
        		 if (isPropEqual) {
 	            	isContain = true;
 	            	
 	            	int distance = i > cPos ? i-cPos : cPos-i;
 	            	//System.out.println("i-cPos:" + i + "-" + cPos);
 	            	if (i < bStart || i > bEnd)
 	            		distance = distance-1;
 	            	distances += 1.0/distance;
 	            	
 		            if (i >= bStart && i <= bEnd) 
 		            	boundaryIn++;
 		            else 
 		            	boundaryOut++;
 	            }
        	}
        	
        	
        	/*for(int i=start; i < end || (end == nodes.size()-1 && i == end); i++)
	        {
	            SemanticNode sNode = nodes.get(i);
	            boolean isPropEqual = isPropEqual(sNode, pn);
	            if(isPropEqual==false) {
	            	if(start>0) { //再往前找一下有没有propEqual的指标
	            		for(int j=start-1;j>=0;j--)
		    	        {
	            			SemanticNode spNode = nodes.get(i);
	        	            boolean new_isPropEqual = isPropEqual(spNode, pn);
	        	            if(new_isPropEqual) {
	        	            	isPropEqual = new_isPropEqual;
	        	            	break;
	        	            }
		    	        }
	            	}
	            }
	            
	            
	            if (isPropEqual) {
	            	isContain = true;
	            	
	            	int distance = i > cPos ? i-cPos : cPos-i;
	            	//System.out.println("i-cPos:" + i + "-" + cPos);
	            	if (i < bStart || i > bEnd)
	            		distance = distance-1;
	            	distances += 1.0/distance;
	            	
		            if (i >= bStart && i <= bEnd) 
		            	boundaryIn++;
		            else 
		            	boundaryOut++;
	            }
	        }*/
        	if (isContain)
        		containNum++;
        }
        
        //change by wyh 2014.12.02
        if (containNum > 0)	// 只要匹配上一个，则有50分的基础分
        	ret = 60;
        // 附加的分数
        double scoreAdd = 15.0*containNum/propsSize + 10.0*boundaryIn/propsSize + 5.0*boundaryOut/propsSize;
        scoreAdd += 10.0*distances;
        ret = ret + scoreAdd;
        return ret <= 100 ? ret : 100;
    }


}
