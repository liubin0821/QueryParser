package com.myhexin.qparser.iterator;

import com.myhexin.qparser.define.EnumDef.DescNodeType;

public class DescNodeText extends DescNode{
	private String pubText;
	
	public DescNodeText(String text, DescNodeType type){
		super(text, type);
		this.pubText = text;
	}
	
	public String getPubText() {
		return pubText;
	}
	
	public void setPubText(String pubText) {
		this.pubText = pubText;
	}
}
