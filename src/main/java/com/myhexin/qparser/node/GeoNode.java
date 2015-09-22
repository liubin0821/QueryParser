package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;

/**
 * 这个类就没用
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-23
 *
 */
public final class GeoNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		 	GeoNode rtn = new GeoNode();
	    	rtn.zipcode=zipcode;
			super.copy(rtn);
			return rtn;
	}
	private GeoNode(){}
    public GeoNode(String text) {
        super(text);
        type = NodeType.GEO;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
        String msg = "Geo 词典信息信息错误";
        if (!k2v.containsKey("zipcode")) {
            throw new BadDictException(msg, NodeType.GEO, text);
        }
        zipcode = k2v.get("zipcode");
    }

    public String zipcode = null;

}
