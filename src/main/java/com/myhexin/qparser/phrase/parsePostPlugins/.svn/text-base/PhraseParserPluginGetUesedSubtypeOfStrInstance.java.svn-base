package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;


public class PhraseParserPluginGetUesedSubtypeOfStrInstance extends PhraseParserPluginAbstract{
	
	
    public PhraseParserPluginGetUesedSubtypeOfStrInstance() {
        super("Get_Uesed_Subtype_Of_Str_Instance");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return getUesedSubtypeOfStrInstance(ENV,nodes);
    }
    
    public static void getTheRightReferenceOfIndexPropValue(ArrayList<SemanticNode> nodes){
    	HashMap<NodeType, ArrayList<SemanticNode>> nodeMap = new HashMap<NodeType, ArrayList<SemanticNode>>();
    	fillNodeMap(nodes,nodeMap);
    	IndexIteratorImpl iterator = new IndexIteratorImpl(nodes);
    	while (iterator.hasNext()) {
    		FocusNode fn = iterator.next();
    		if(fn.hasIndex() && fn.getIndex()!=null)
    		for (PropNodeFacade pn : fn.getIndex().getClassifiedProps(PropType.STR)) {
				if(pn.getValue()!=null){
					SemanticNode sn = getCurrentListReferenceNode(nodeMap,pn.getValue());
					pn.setValue( (sn!=null?sn:pn.getValue()));
				}
					
			}
    	}
    }
    
    private static final SemanticNode getCurrentListReferenceNode(
            HashMap<NodeType, ArrayList<SemanticNode>> nodeMap, SemanticNode value) {
	    if(!nodeMap.containsKey(value.getType()))
	    	return null;
	    for(SemanticNode sn : nodeMap.get(value.getType()))
	    	if(sn.getText().equals(value.getText())){
	    		nodeMap.get(value.getType()).remove(sn);
	    		return sn;
	    	}
	    return null;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2015-1-14 下午3:18:58
     */
    private static final void fillNodeMap(ArrayList<SemanticNode> nodes,
            HashMap<NodeType, ArrayList<SemanticNode>> nodeMap) {
    	for (SemanticNode sn : nodes) {
			switch (sn.getType()) {
			case STR_VAL:
				addToNodeMap(nodeMap,sn.getType(),sn);
				break;
			case FOCUS:
				FocusNode fn = (FocusNode) sn;
				if(fn.hasString())
					addToNodeMap(nodeMap,sn.getType(),fn.getIndex());
				break;
			}
		}
    }
    private static final void addToNodeMap(HashMap<NodeType, ArrayList<SemanticNode>> nodeMap, NodeType type, SemanticNode sn) {
    	if(sn==null) return ;
    	
    	if(!nodeMap.containsKey(type))
    		nodeMap.put(type, new ArrayList<SemanticNode>());
    	nodeMap.get(type).add(sn);
    }

	public ArrayList<ArrayList<SemanticNode>> getUesedSubtypeOfStrInstance(Environment ENV, ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	getTheRightReferenceOfIndexPropValue(nodes);
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	
    	IndexIteratorImpl iterator = new IndexIteratorImpl(nodes);
    	while (iterator.hasNext()) {
    		FocusNode fn = iterator.next();
    		if(fn.hasIndex()){
    			ClassNodeFacade cn = fn.getIndex();
    			for (PropNodeFacade pn : cn.getClassifiedProps(PropType.STR)) {
					if(pn.getValue()!=null && pn.getValue().getType()==NodeType.STR_VAL){
						((StrNode)pn.getValue()).setDefaultIndexSubtype(pn);
					}
						
				}
    		}
    	}
    	rlist.add(nodes);
		return rlist;
    }

    
}

