package com.myhexin.qparser.node;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;


public final class InstNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		InstNode rtn = new InstNode();
	    	rtn.info_=info_;
			super.copy(rtn);
			return rtn;
	}
	private InstNode() {}
	
    public InstNode(String text, ClassNodeFacade onto) {
        super(text);
        type = NodeType.INST;
        classOnto_ = onto; 
    }
    public InstNode(String text) {
        super(text);
        type = NodeType.INST;
    }
    public boolean hasProp(SemanticNode prop){
        
        return classOnto_.hasProp((PropNodeFacade) prop);
    }
    
    public List<SemanticNode> ofWhat(){
        return classOnto_.getOfWhat();
    }
    
    public PropNodeFacade getNumDateProp(){
        return classOnto_.getNumDateProp();
    }
    public boolean hasClassOnto(ClassNodeFacade node){
        Set<ClassNodeFacade> sc =  info_.keySet();
        boolean has = false;
        has = sc.contains(node);
        return has;
    }
    
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException{
    }
    
    public ClassNodeFacade classOnto_ = null;
    public HashMap<ClassNodeFacade,HashMap<String,String>> info_ = 
        new HashMap<ClassNodeFacade,HashMap<String,String>>();
}
