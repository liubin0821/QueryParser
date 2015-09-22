package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.FakeDateType;
import com.myhexin.qparser.except.BadDictException;

public class FakeDateNode extends DateNode {

	private FakeDateNode(){}
	
    public FakeDateNode(String text) {
        super(text);
    }
    
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype)
            throws BadDictException {
        if (!k2v.containsKey("vague_type") || k2v.get("vague_type") == null
                || k2v.get("vague_type").length() == 0) {
            String err = String.format("Fake Date %s 词典错误", text);
            throw new BadDictException(err, type, text);
        }
        setVagueType(k2v.get("vague_type"));
    }
    public void setVagueType(String vagueType) {
        this.vagueType = vagueType;
    }

    public String getVagueType() {
        return vagueType;
    }
    private String vagueType = null;
    public FakeDateType fakeDateType = null;
}
