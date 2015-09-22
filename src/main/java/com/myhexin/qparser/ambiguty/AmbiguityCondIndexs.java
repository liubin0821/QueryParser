package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.resource.ResourceInst;

public class AmbiguityCondIndexs extends AmbiguityCondAbstract{

    /**
     * init
     */
	public AmbiguityCondIndexs()
	{
        super("indexs");
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
        double ret = 50;
        
        if(cNode==null || cNode.getText()==null) {
        	return ret;
        }
        
        if(cNode.getText().equals(alias))
        {
            ret += 5;
        }
        
        //比较是否是同一个领域, added  by 黄敏
        List<ClassNodeFacade> superClassList = cNode.getSuperClass();
        if(superClassList != null && superClassList.size() > 0 ) {
    		Environment env = (Environment)(nodes.get(0));
    		String domain = env.getFirstDomain();
    		StringBuilder sb = new StringBuilder("");
    		matchDomain( domain, superClassList, sb );
        	if( StringUtils.isNotBlank(domain) && "true".equals(sb.toString())  ) {
    			ret += 10;
    		}
        }
        
        //根据configFile.dict_index_scores配置的指标分数,给予加分
        Map<String, Integer> dictIndexScoreMap = ResourceInst.getInstance().getDictIndexScoreMap();
        Map<String, List<Integer> > dictTextTypeIdMap = ResourceInst.getInstance().getDictTextTypeIdMap();
        //System.out.println("dictIndexScoreMap=" + dictIndexScoreMap);
        //System.out.println("cNode: " + cNode.getText());
        //System.out.println("alias: " + alias);
        if(dictIndexScoreMap!=null) {
        	 for(int i=start;i<=end && i<nodes.size();i++) {
        		String nodeText = null;
        		SemanticNode node = nodes.get(i);
        		//System.out.println("node: " + node.toString());
        		if(node.isStrNode() ) {
              		nodeText = node.getText();
              	}else if(node.isFocusNode() ) {
              		FocusNode fNode = (FocusNode)node;
              		if(fNode.getString()!=null ) {
              			nodeText = fNode.getString().getText();
              		}else{
              			if(fNode.hasIndex() && fNode.getIndex()!=null)
              				nodeText = fNode.getIndex().getText();
              			else{
              				nodeText = fNode.getText();
              			}
              		}
              	}/*else if(node.isBoundaryNode() && ((BoundaryNode)node).isStart() ) {
             		BoundaryNode bNode = (BoundaryNode)node;
             		
             	}*/
        		//System.out.println("nodeText: " + nodeText);
             	if(nodeText==null) continue;
             	
             	if(nodeText!=null) {
             		Integer score = dictIndexScoreMap.get(nodeText + "_&_" + cNode.getText());
             		if(score==null && dictTextTypeIdMap!=null) {
             			//根据typeId找到ScoreMap
             			List<List<Integer>> typeIdsList = new ArrayList<List<Integer>>();
             			List<Integer> typeIds = dictTextTypeIdMap.get(nodeText);
             			if(typeIds !=null && typeIds.size()  > 0) {
             				typeIdsList.add(typeIds);
             			}else if(typeIds == null && (nodeText.indexOf(",") > 0 || nodeText.indexOf(".") > 0)) { //有可能是多个指标用分隔符分开的情况
             				String sem = (nodeText.indexOf(",") > 0)? "," : ".";
             				String[] indexes = nodeText.split(sem);
             				for(String index : indexes) {
             					List<Integer> ttypeIds = dictTextTypeIdMap.get(index);
             					if(ttypeIds !=null && ttypeIds.size()  > 0) {
                     				typeIdsList.add(ttypeIds);
                     			}
             				}
             			}
             			if(typeIdsList.size() > 0) {
             				for(List<Integer> typeIdEle : typeIdsList) {
	             				Integer maxScore = 0;
	             				for(Integer typeId : typeIdEle) {
	             					Integer theScore = dictIndexScoreMap.get("_typeid_" + typeId + "_&_" + cNode.getText());
	                     			if(theScore!=null && theScore>maxScore) {
	                     				maxScore = theScore;
	                     			}
	                     		}
	             				score = maxScore;
             				}
             			}
             		}
                 	if(score!=null && score>0) {
                 		ret += score;
                 	}	
             	}
             }
        }//加分结束
       
        
        return ret;
    }
	
	/**
	 * 匹配当前的domain是否是语义树中定义的一个节点。
	 * 
	 * @author huangmin
	 * 
	 * @param domain
	 * @param superClassList
	 * @param sb
	 * @return
	 */
	private void matchDomain (String domain, List<ClassNodeFacade> superClassList, StringBuilder sb) {
		if(StringUtils.isNotBlank(domain) ) {
			if( "true".equals(sb.toString()) ) {
				return;
			}
			for ( ClassNodeFacade classNode : superClassList) {
				List<ClassNodeFacade> resSuperClassList = classNode.getSuperClass();
				if(domain.equals(classNode.getText())) {
					sb.append("true") ;  //匹配到domain，终止递归
				}else if(resSuperClassList != null && resSuperClassList.size() > 0) {
					matchDomain(domain, resSuperClassList, sb );
				}
			}
		}
	}	

}
