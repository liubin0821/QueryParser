package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoFacadeUtil;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindStrToIndex;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.ChangeToStandardStatementMultResult;

public class PhraseParserPluginBindIndexToIndex extends PhraseParserPluginAbstract{

	
    public PhraseParserPluginBindIndexToIndex() {
        super("Bind_Index_To_Index");
    }
    
    public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		return bindIndexToIndex(annotation.getNodes());
    }

    private ArrayList<ArrayList<SemanticNode>> bindIndexToIndex(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        //出现如下为题:董事长姓刘、法人代表姓易 --> 董事长_&_法人代表的姓名包含刘_&_法人代表的姓名包含易 
        
        //替换方案
        ArrayList<FocusNode> indexs = new ArrayList<FocusNode>();
        IndexIteratorImpl it = new IndexIteratorImpl(nodes);
        while(it.hasNext())//得到指标列表
        	indexs.add(it.next());
        
        int span ;//跨度
        int size = indexs.size();
		for (span = 1; span < size; span++) {
			for (int i = 0; i + span < size; i++) {			
				if (bindIndexToIndex(nodes,indexs.get(i), indexs.get(i + span)))
					continue;
				if (bindIndexToIndex(nodes,indexs.get(i + span), indexs.get(i)))
					continue;
			}
		}
        tlist.add(nodes);
        return tlist;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-17 下午3:59:26
     * @description:  指标到指标的绑定   cn1绑定到cn2
     * 				  只尝试绑定fn1的getindex 到fn2的所有index		
     * 
     */
	private final boolean bindIndexToIndex(ArrayList<SemanticNode> nodes,FocusNode fn1, FocusNode fn2) {
		//绑定focusNode默认的指标
		ClassNodeFacade indexTo = fn2.getIndex();
		if(indexTo!=null && bindNodeToIndexProps(nodes,fn1, fn2,indexTo)){
			indexTo.setIsBoundValueToThis(true);
			fn1.setIsBoundToIndex(true);
			//fn1.setBoundToIndexProp(pn);
			
			return true;
		}  
		
		//已经有值绑定到指标上,不能再做选择
		//if(indexTo.isBoundValueToThis)
		//	return false;
		
		//绑定到可能存在的指标
		ArrayList<FocusNode.FocusItem> itemList = fn2.getFocusItemList();
		int j = 0;
		int size = itemList.size();
		while (size>1 && j < size) {
			FocusNode.FocusItem item = itemList.get(j);
			j++;
			if (item.getType() == FocusNode.Type.INDEX) {
				ClassNodeFacade cn2 = item.getIndex();				
				//cn2.clear();	
				if(indexTo!=null && !item.isCanDelete() && 
					ChangeToStandardStatementMultResult.canAsPossibleResult(cn2, indexTo) && //
					bindNodeToIndexProps(nodes,fn1, fn2,cn2)){					
					cn2.setIsBoundValueToThis(true);
					//fn2.setIndex(cn2); //fixed by huangmin, it should not be to change the default index binding although index bind to index 
					fn1.setIsBoundToIndex(true);
					return true;
				}
			}
		}
		
		return false;
	}
    
    /**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-1-6 下午5:03:34
	 * @description:   	
	 * 
	 */
	public static boolean bindNodeToIndexProps(ClassNodeFacade cn1,
			ClassNodeFacade bindTo) {
		List<PropNodeFacade> propList = bindTo.getClassifiedProps(PropType.INDEX);
		if(propList.size()==0) return false;
		
		if(bindClassNodeToProps(null,null,cn1,null,bindTo)) return true;
		
		return false;
	}
	
	
	public static boolean bindNodeToIndexProps(ArrayList<SemanticNode> nodes,FocusNode fn1,
			FocusNode fnTo,ClassNodeFacade bindTo) {
		
		ClassNodeFacade cn1 = fn1.getIndex();
		
		if(bindClassNodeToProps(nodes,fn1,cn1, fnTo, bindTo)) 
			return true;
		
		//if (fn1.isFromStrInstance()) {
			ArrayList<FocusNode.FocusItem> itemList = fn1.getFocusItemList();
			for (int j = 0, size = itemList.size(); size > 1 && j < size; j++) {
				FocusNode.FocusItem item = itemList.get(j);
				if (item.getType() == FocusNode.Type.INDEX) {
					ClassNodeFacade cn1Other = item.getIndex();
					if (ChangeToStandardStatementMultResult.canAsPossibleResult(cn1Other, cn1) && //
							bindClassNodeToProps(nodes,fn1,cn1Other,fnTo, bindTo)) {
						fn1.setIndex(cn1Other);
						return true;
					}
				}
			}
		//}
		
		return false;
	}

    @SuppressWarnings("unchecked")
    private static final boolean bindClassNodeToProps(ArrayList<SemanticNode> nodes,
    		FocusNode fn1,ClassNodeFacade cn1,FocusNode fnTo,ClassNodeFacade bindTo) {
    	
    	List<PropNodeFacade> propList = bindTo.getClassifiedProps(PropType.INDEX);
    	
	    for (PropNodeFacade pn : propList) {
			if (pn.getText()==null ) 
				continue;
			if (pn.getValue() != null) {
				if (pn.getValue().getType() == NodeType.CLASS) {
					if(cn1.isSuperClass((ClassNodeFacade) pn.getValue())){
						BindStrToIndex.bindNodeToProp(cn1, pn, bindTo);
						return true;
					} else if(!((ClassNodeFacade)pn.getValue()).isSuperClass( cn1)) {
						//指标补全  比如: 开盘价>最高价指数   补出一个指数来
						if(fn1 != null && canBindIndexNodeToPropBySubType(cn1,pn)){
							if(!fn1.getIndex().getText().equals(cn1.getText()))
								fn1.setIndex(cn1);
							
							//this copy don't copy index's values
							FocusNode fnToCopy= (FocusNode)NodeUtil.copyNode(fnTo); //fnTo.copy();
							fnToCopy.setIndex(OntoFacadeUtil.copyWithValues(fnTo.getIndex())); //need value
							
							dealWithCopyNodeBind(cn1,fnToCopy,bindTo,pn);
								
							//这个一定成立, 为了防止出错
							if(nodes.get(0).type==NodeType.ENV){
								Map<SemanticNode,FocusNode> completionIndexOfIndexProp;
								Environment listEnv = (Environment) nodes.get(0);
								if(listEnv.containsKey("completionIndexOfIndexProp"))
									completionIndexOfIndexProp=listEnv.get("completionIndexOfIndexProp",Map.class, false);
								else{
									completionIndexOfIndexProp=new HashMap<SemanticNode, FocusNode>();
									listEnv.put("completionIndexOfIndexProp", completionIndexOfIndexProp, false);
								}
								completionIndexOfIndexProp.put(fn1, fnToCopy);
							}
						}
						continue;
					}
						
				}
			}
			if (pn.isValueProp())
				continue;
			
			if(bindIndexNodeToPropBySubType(cn1, pn, bindTo))
				return true;
		}
	    return false;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-12-17 上午11:14:54
     */
    private static final void dealWithCopyNodeBind(ClassNodeFacade cn1, FocusNode fnToCopy, ClassNodeFacade bindTo,PropNodeFacade pn) {
    	
    	ClassNodeFacade index = fnToCopy.getIndex();
    	if(index==null || !index.getText().equals(bindTo.getText())){
    		ArrayList<FocusNode.FocusItem> itemList = fnToCopy.getFocusItemList();
			for (int j = 0, size = itemList.size(); size > 1 && j < size; j++) {
				FocusNode.FocusItem item = itemList.get(j);
				if (item.getType() == FocusNode.Type.INDEX) 
					if(item.getIndex().getText().equals(bindTo.getText())){
						fnToCopy.setIndex(item.getIndex());
						index=fnToCopy.getIndex();
						break;
					}
			}
    	}
    	
    	if(index==null) return;
    	
    	for(PropNodeFacade pn2 : bindTo.getClassifiedProps(PropType.INDEX))
    		if(pn2.getText().equals(pn.getText())){
    			BindStrToIndex.bindNodeToProp(cn1, pn2, bindTo);
    			return;
    		}
    	
    	
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-25 下午4:31:00
     * @description:  
     * 
     */
    private static final boolean bindIndexNodeToPropBySubType(ClassNodeFacade cn1, PropNodeFacade pn, SemanticNode pnParent) {
    	if(pn==null) return false;
    	
    	for (String subType : pn.getSubType()) {
    		if(cn1.getCategorysAll().contains(subType)){
    	    	BindStrToIndex.bindNodeToProp(cn1, pn, pnParent);
    	    	return true;
    	    }
		}
    	return false;
	    
    }
    
    private static final boolean canBindIndexNodeToPropBySubType(ClassNodeFacade cn1, PropNodeFacade pn) {
    	if(pn==null) return false;
    	
    	for (String subType : pn.getSubType()) {
    		if(cn1.getCategorysAll().contains(subType)){
    	    	return true;
    	    }
		}
    	return false;
	    
    }
    
    //把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }
}
