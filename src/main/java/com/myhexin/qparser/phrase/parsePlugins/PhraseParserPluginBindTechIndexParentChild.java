package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.model.TechIndexParentChildInfo;


/**
 * 技术指标字母线的绑定
 * 
 * kdj的j值, 把kdj绑定到j值, 
 * j值
 *  |-kdj
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-6
 *
 */
public class PhraseParserPluginBindTechIndexParentChild extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginBindTechIndexParentChild.class.getName());

	
	public PhraseParserPluginBindTechIndexParentChild() {
        super("Bind_TechIndex_ParentChild");
    }

	private final static String PARENT_PROPNAME_1 = "主体";
	private final static String PARENT_PROPNAME_2 = "_主体";
	
	@Override
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		for(int i=0;i<nodes.size();i++) {
			SemanticNode node = nodes.get(i);
			if(node.isFocusNode()==false ) continue;
			//是focusNode 并且是多线指标

			FocusNode childNode = (FocusNode) node;
			if(TechIndexParentChildInfo.getInstance().isChildTechIndexName(childNode ) ) {
				//只往前面找父节点指标
				if(i>0) {
					for(int j=0;j<i;j++) {
						SemanticNode pre_node = nodes.get(j);
						
						//已经绑定了,跳出循环,不再往前找
						/*if(pre_node.isBoundToIndex()==true) {
							break;
						}*/
						
						if(pre_node.isFocusNode()==false) continue;
						

						FocusNode preParentNode = (FocusNode) pre_node;
						String parentIndexName = preParentNode.getText();
						if(preParentNode.hasIndex() && preParentNode.getIndex()!=null) {
							parentIndexName = preParentNode.getIndex().getText();
						}
						
						//检查2个指标是不是多线的子母线关系
						if(childNode.hasIndex() && childNode.getIndex()!=null && TechIndexParentChildInfo.getInstance().isParentChildMatch(preParentNode, childNode) ) {
							List<PropNodeFacade> propList = childNode.getIndex().getAllProps();
							if(propList!=null) {
								for(PropNodeFacade prop: propList) {
									if(prop.isValueProp()) continue;
									
									//是的话,绑定到主体属性
									if(parentIndexName.equals(prop.getText()) || (prop.getType() == NodeType.PROP 
										&& (prop.getText().equals(PARENT_PROPNAME_1) || prop.getText().equals(PARENT_PROPNAME_2))) ) {
										preParentNode.setIsBoundToIndex(true);
										prop.setValue(preParentNode);
										preParentNode.setBoundToIndexProp(childNode, prop);
									}
								}
							}
						}
					}
				}
			}
		}
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		tlist.add(nodes);
		return tlist;
	}
	
}
