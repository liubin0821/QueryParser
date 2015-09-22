package com.myhexin.qparser.iterator;

import com.myhexin.qparser.define.EnumDef.DescNodeType;

public class DescNodeNum extends DescNode{
	private int num;	
	
	public DescNodeNum(String text,DescNodeType type) {
		super(text,type);
		this.num = Integer.valueOf(text.substring(1)).intValue();;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
}
