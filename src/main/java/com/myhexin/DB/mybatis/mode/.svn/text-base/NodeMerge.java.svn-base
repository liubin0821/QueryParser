package com.myhexin.DB.mybatis.mode;

import java.util.regex.Pattern;
import java.io.Serializable;

public class NodeMerge implements Serializable{
	private static final long serialVersionUID = 1L;
	public void setId(int id){}
	private String mergePattern; //用于匹配node.text1,node.text2的正则或文本
	private String convertPattern; //日期,时间,数字格式
	private String node_type; //Date, Num or Time
	private boolean is_regex_bool; //mergePattern是不是正则
	private String change_to_text; //文本转换
	private String calc_expr;
	private Pattern pa;
	private Pattern pb;

	public String getChange_to_text() {
		return change_to_text;
	}

	public void setChange_to_text(String change_to_text) {
		this.change_to_text = change_to_text;
	}

	public String getCalc_expr() {
		return calc_expr;
	}

	public void setCalc_expr(String calc_expr) {
		this.calc_expr = calc_expr;
	}

	public boolean isRegex() {
		return is_regex_bool;
	}

	public void setIs_regex(String is_regex) {
		if(is_regex!=null && is_regex.equals("Y")) {
			is_regex_bool = true;
		}else{
			is_regex_bool = false;
		}
	}

	public String getNode_type() {
		return node_type;
	}

	public void setNode_type(String node_type) {
		this.node_type = node_type;
	}

	public String getMergePattern() {
		return mergePattern;
	}

	public void setMergePattern(String mergePattern) {
		this.mergePattern = mergePattern;
	}

	public String getConvertPattern() {
		return convertPattern;
	}

	public void setConvertPattern(String convertPattern) {
		this.convertPattern = convertPattern;
	}

	public boolean build() {
		if(mergePattern!=null) {
			String[] patterns = mergePattern.split("_&_");
			if(patterns!=null && patterns.length==2) {
				try{
					pa = Pattern.compile(patterns[0]);
					pb = Pattern.compile(patterns[1]);
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	
	public boolean match(String text1, String text2) {
		if(pa.matcher(text1).matches() && pb.matcher(text2).matches()) {
			return true;
		}else{
			return false;
		}
	}
}
