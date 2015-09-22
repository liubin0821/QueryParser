package com.myhexin.qparser.onto;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;

abstract class OntoNode extends SemanticNode {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(OntoNode.class);
	OntoNode() {}

	OntoNode(String text) {
		super(text);
	}
    void setURI(String uri) {
        if (type == NodeType.PREDICT) {
            uri = String.format("\"%s\"^^xsd:string", uri);
        } else {
            this.uri = uri;
        }
    }
    
    /**
     * 此类节点在内存中只能有一个实例，不可拷贝，直接返回this
     */
    /*@Override
    OntoNode clone() {
    	logger_.warn("[WARNING] OntoNode "+ this.text+" clone");
    	logger_.warn(ModifyLog.getStackTrace());
        return (OntoNode) super.clone();
    }*/

    public String uri = null;
}
