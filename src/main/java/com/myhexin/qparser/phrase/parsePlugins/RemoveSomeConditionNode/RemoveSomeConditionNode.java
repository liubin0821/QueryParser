package com.myhexin.qparser.phrase.parsePlugins.RemoveSomeConditionNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.dao.RemovenodeinfoMapper;
import com.myhexin.DB.mybatis.mode.Removenodeinfo;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class RemoveSomeConditionNode {

	private static HashMap<String, Removenodeinfo> removeNodeInfos = null;// 单例模式
	static {
		loadDate();
	}

	private static final Pattern skipList = Pattern.compile("含|包含|包括|是|有|含有|为|中有| ");
	
	public static void process(ArrayList<SemanticNode> nodes) {

		for (int i = 0; i < nodes.size(); i++) {
			if (removeNodeInfos.containsKey(nodes.get(i).getText())) {// 配置文件有该信息
				Removenodeinfo rnf = removeNodeInfos.get(nodes.get(i).getText());
				// 如果前一个节点的preProp 是配置文件配置信息
				if (rnf.getPreprop() != null && i - 1 >= 0){
					StrNode strNode = null; 
					SemanticNode sn = nodes.get(i - 1);
					strNode = getStrNode(sn);
					if(strNode !=null && 
						isNeedDelete(nodes,i - 2,rnf.getPrelable())){
						String subType =  rnf.getPreprop();
						if (strNode.subType.contains(subType)) {
							nodes.remove(i--);
							continue;
						}
					}
				} else if(rnf.getPostprop() != null){
					SemanticNode sn = getPostNode(nodes,i+1);
					if(sn != null && sn.type == NodeType.FOCUS){
						FocusNode fn = (FocusNode) sn;
						ClassNodeFacade cn = fn.getIndex();
						if(fn.hasIndex() && cn.hasProp(rnf.getPostprop())){
							nodes.remove(i--);
							continue;
						}
						
						//查看是够有能删除 线 的
						ArrayList<FocusNode.FocusItem> itemList = fn.getFocusItemList();
						int j = 0;
						int size = itemList.size();
						while (j < size) {
							FocusNode.FocusItem item = itemList.get(j);
							j++;
							if (item.getType() == FocusNode.Type.INDEX) {
								ClassNodeFacade tempIndex = item.getIndex();
								if (cn == null)
									continue;
								if(tempIndex.hasProp(rnf.getPostprop())){
									fn.setIndex(tempIndex);
									nodes.remove(i--);	
									break; 
								}
							}
						}
								
						
					}
				}
			}
		}
	}


    private static SemanticNode getPostNode(ArrayList<SemanticNode> nodes, int i) {
	    
    	while(i < nodes.size()){
    		SemanticNode sn = nodes.get(i);
    		//应该连在一起的才处理
    		//if(skipList.matcher(sn.text).matches())
    		//	continue;
    		return sn;
    	}
	    return null;
    }


	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-10 上午11:20:55
     * @description:   	
     * @param nodes
     * @param i
	 * @param text 
     * 
     */
    private static boolean isNeedDelete(ArrayList<SemanticNode> nodes, int i, String text) {
    	if (text == null || text == "") {
			return true;
		}
    	
    	for(int k = i; k>=0; k--){
			SemanticNode sn = nodes.get(k);
			if (skipList.matcher(sn.getText()).matches()) {
				continue;
			}else if(sn.getText().equals(text))
				return true;
			break;
		}
    	return false;
    }


	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-10 上午9:38:08
     * @description:   	
     * @param strNode
     * @param sn
     * @return
     * 
     */
    private static final StrNode getStrNode(SemanticNode sn) {
    StrNode strNode = null;
	    if (sn.type == NodeType.STR_VAL) {
	    	strNode = (StrNode)sn;
	    }else if(sn.type == NodeType.FOCUS && ((FocusNode)sn).hasString()){
	    	strNode = ((FocusNode)sn).getString();
	    }
	    return strNode;
    }
	

    public static void loadDate() {
		if (removeNodeInfos == null) {
			reloadDate();
		}
    }
    
	@SuppressWarnings("unchecked")
	public static void reloadDate() {
		List<Removenodeinfo> RList = null;
		MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
		RList = mybatisHelp.getRemovenodeinfoMapper().selectAll();

        
		HashMap<String, Removenodeinfo> temp = new HashMap<String, Removenodeinfo>();
        for (Removenodeinfo removenodeinfo : RList) {
        	temp.put(removenodeinfo.getLable(), removenodeinfo);
		}
        removeNodeInfos=temp;
	}

}
