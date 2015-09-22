package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginChangeNewIndexToOldIndex extends
        PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginChangeNewIndexToOldIndex.class.getName());
	
	public PhraseParserPluginChangeNewIndexToOldIndex() {
		super("Change_New_Index_To_Old_Index");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return ChangeNewIndexToOldIndex(ENV,nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> ChangeNewIndexToOldIndex(
			Environment ENV,ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		//处理非默认指标
		dealWithNomalIndex(nodes,ENV);
		
		//默认指标
		dealWithDefaultIndex(nodes,ENV);
		tlist.add(nodes);
		return tlist;
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-8 下午3:24:49
     * @description:   	
     * @param nodes
     * 
     */
    private void dealWithNomalIndex(ArrayList<SemanticNode> nodes,Environment ENV) {
	    for (int i = 0; i < nodes.size(); i++) {
			//不是指标
			if (nodes.get(i).getType() != NodeType.FOCUS
			        || !((FocusNode) nodes.get(i)).hasIndex())
				continue;

			FocusNode fn = (FocusNode) nodes.get(i);
			ClassNodeFacade index = fn.getIndex();
				//一个指标变多个  比如董事长
				if (index.getCategorysAll().contains("abs_人")) {
					ArrayList<SemanticNode> fnList = changeOneIndexToMany(fn,
                            index,ENV);					
					//一个指标变成多个(大于1)指标是才需要处理
					if (fnList.size() > 1) {
						//移除原始BoundaryNode
						if (removeOldBoundaryNode(nodes, i))
							i--;//成功移除start i--
						BoundaryNode start = createStartBoundaryNode();
						BoundaryNode end = createEndBoundaryNode();
						fnList = setIndexListWithSyntactic(fnList, start, end);
					}					
					//替换原来指标
					if (fnList.size() > 0) {
						nodes.remove(i);
						nodes.addAll(i, fnList);
						i += (fnList.size() - 1);
					}					
				}
				//人物的属性指标  比如年龄
				else if (index.getFieldsAll().contains("person")) {
					String oldIndex = getOldIndexText(index);
					FocusNode fnOldIndex = null;
                    try {
	                    fnOldIndex = ParsePluginsUtil.getFocusNodeOldIndex(oldIndex, (Query.Type) ENV.get("qType",false));
                    } catch (UnexpectedException e) {  
	                    e.printStackTrace();
                    }
					if (fnOldIndex == null ) {
						//logger_.warn(Param.ALL_ONTO_FILE_OLD_SYSTEM+"中指标:"+ oldIndex + "未定义");
						continue;
					}
					setIndexPropValue(fnOldIndex.getIndex(),index);
					fnOldIndex.getIndex().setIsChangeToOld(true);//标记是转换到老系统的指标
					nodes.remove(i);
					nodes.add(i,fnOldIndex);
				}
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-8 下午3:22:50
     * @description:   	
     * @param nodes
     * 
     */
    private void dealWithDefaultIndex(ArrayList<SemanticNode> nodes,Environment ENV) {
	    for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getType() != NodeType.BOUNDARY
			        || !((BoundaryNode) nodes.get(i)).isStart())
				continue;
			BoundaryNode.SyntacticPatternExtParseInfo info = ((BoundaryNode) nodes.get(i)).getSyntacticPatternExtParseInfo(false);
			ArrayList<Integer> elelist;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos == -1) {
						SemanticNode defaultNode = info.absentDefalutIndexMap
						        .get(j);
						if (defaultNode == null
						        || defaultNode.getType() != NodeType.FOCUS
						        || !((FocusNode) defaultNode).hasIndex())
							continue;

						FocusNode fn = (FocusNode) defaultNode;
						ClassNodeFacade index = fn.getIndex();

						if (index.getFieldsAll().contains("person")) {
							String oldIndex = getOldIndexText(index);
							FocusNode fnOldIndex = null;
							try {
								fnOldIndex = ParsePluginsUtil.getFocusNodeOldIndex(oldIndex, (Query.Type) ENV.get("qType",false));
							} catch (UnexpectedException e) {
								e.printStackTrace();
							}
							if (fnOldIndex == null) {
								//logger_.warn(Param.ALL_ONTO_FILE_OLD_SYSTEM + "中指标:" + oldIndex + "未定义");
								continue;
							}
							setIndexPropValue(fnOldIndex.getIndex(), index);
							fnOldIndex.getIndex().setIsChangeToOld(true);//标记是转换到老系统的指标
							fn.setIndex(fnOldIndex.getIndex());
						}
					}
				}
			}
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-8 下午3:17:38
     * @description:   	
     * @param index
     * @return
     * 
     */
    private String getOldIndexText(ClassNodeFacade index) {
	    //得到主体  比如董事长
	    for (PropNodeFacade pn : index.getClassifiedProps(PropType.INDEX)) {
	    	if ((pn.getText().contains("主体") ||pn.getText().contains("abs_人")) && pn.getValue() !=null) {
	    		return pn.getValue().getText() + index.getText();
	    	}
	    }
	    return "";
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-8 下午3:09:02
     * @description:   	
     * @param fn
     * @param index
     * @return
     * @throws UnexpectedException
     * 
     */
    private ArrayList<SemanticNode> changeOneIndexToMany(FocusNode fn,
            ClassNodeFacade index,Environment ENV){
	    ArrayList<SemanticNode> fnList = new ArrayList<SemanticNode>();
	    boolean isFirst = true;
	    for (PropNodeFacade pn : index.getAllProps()) {
	    	if (pn.getValue() == null)
	    		continue;
	    	String propName = pn.getText().startsWith("_") ? pn.getText().substring(1) : pn.getText();
	    	String oldIndex = index.getText() + propName;
	    	FocusNode fnOldIndex = null;
            try {
	            fnOldIndex = ParsePluginsUtil.getFocusNodeOldIndex(oldIndex, (Query.Type) ENV.get("qType",false));
            } catch (UnexpectedException e) {  
	            e.printStackTrace();
            }
	    	if (fnOldIndex == null ) {
	    		//logger_.warn(Param.ALL_ONTO_FILE_OLD_SYSTEM+"中指标:"+ oldIndex + "未定义");
	    		continue;
	    	}
	    	
	    	//第一个FocusNode保留原来的文本 只是把index变了
	    	if(isFirst){
	    		isFirst=false;
	    		fnOldIndex.setText(fn.getText());
	    	}
	    	
	    	setIndexPropValue(fnOldIndex.getIndex(),pn);
	    	fnOldIndex.getIndex().setIsChangeToOld(true);//标记是转换到老系统的指标
	    	fnList.add(fnOldIndex);
	    }
	    return fnList;
    }


    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-7 上午11:25:14
     * @description:   	
     * @param nodes
     * @param i
     * 
     */
    private boolean removeOldBoundaryNode(ArrayList<SemanticNode> nodes, int i) {
    	boolean result = false; //移除BOUNDARY start i--
    	
    	//移除end
    	for(int j = i+1; j<nodes.size() ; j++)
    		if(nodes.get(j).getType() == NodeType.BOUNDARY){
    			if(((BoundaryNode)nodes.get(j)).isEnd())
    				nodes.remove(j);
    			break;
    		}
    	
    	
    	//移除start
    	for(int j = i-1; j>=0 ; j--)
    		if(nodes.get(j).getType() == NodeType.BOUNDARY){
    			if(((BoundaryNode)nodes.get(j)).isStart()){
    				nodes.remove(j);
    				result = true;
    			}
    			break;
    		}
    	

    	return result;
    }

	/**
     * @author: 	    吴永行 
     * @return 
     * @dateTime:	  2014-2-7 上午10:25:04
     * @description:  一个变成多个的指标,需要把 BoundaryNode相应添加	
     * 
     */
    private ArrayList<SemanticNode> setIndexListWithSyntactic(ArrayList<SemanticNode> fnList,
            BoundaryNode start, BoundaryNode end) {
    	ArrayList<SemanticNode> result = new ArrayList<SemanticNode>();
    	for (int i = 0; i < fnList.size(); i++) {
    		result.add(start);
    		result.add(fnList.get(i));
    		result.add(end);
		}
    	return result;
    }

	private BoundaryNode createEndBoundaryNode() {	    
    	BoundaryNode boundary = new BoundaryNode();
        boundary.setType(BoundaryNode.END, IMPLICIT_PATTERN.FREE_VAR.toString());
		return boundary;
    }


    private BoundaryNode createStartBoundaryNode() {
    	
    	BoundaryNode boundary = new BoundaryNode();
        boundary.setType(BoundaryNode.START, IMPLICIT_PATTERN.FREE_VAR.toString());
        BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
        boundary.extInfo.addElementNodePos(1, 1);
        boundary.extInfo.presentArgumentCount++;
		return boundary;
    }

	private void setIndexPropValue(ClassNodeFacade indexTo, PropNodeFacade pn) {
    	String propName = pn.getText().startsWith("_") ? pn.getText().substring(1) : pn.getText();
    	//只有一个属性直接赋值
    	if(indexTo.getAllProps().size()==1){
    		indexTo.getAllProps().get(0).setValue(pn.getValue());
    		return;
    	}
    	
    	//先设置值属性
    	if(indexTo.getPropOfValue()!=null){
    		if (indexTo.getPropOfValue().getType() == pn.getType() && indexTo.getPropOfValue().getValue() ==null)
    		indexTo.getPropOfValue().setValue(pn.getValue());
    		return;
    	}
    	//然后才设置其他属性
    	for (PropNodeFacade indexToPn : indexTo.getAllProps()) {
			if (indexToPn.getType() == pn.getType() && indexToPn.getValue() ==null) {
				indexToPn.setValue(pn.getValue());
				return;
			}
		}
    }
    
    private void setIndexPropValue(ClassNodeFacade indexTo, ClassNodeFacade indexFrom) {
    	
    	for(PropNodeFacade pn : indexFrom.getAllProps()){
    		if(pn.getValueType()==PropType.INDEX )
    			continue;
    		setIndexPropValue(indexTo, pn);
    	}
    }
}
