/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.pattern;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.node.SemanticNode;

/**
 * 正则Pattern替换
 */
public class PatternInfo implements Comparable<PatternInfo> { 
	private int patSize = 0;
	private Pattern pattern;
	private String text;
	private double priority = 0;
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	private boolean isRepAll;
	private boolean hasStart = false;
	private boolean hasEnd = false;
	private ArrayList<SemanticNode> repInfos;
	
	/**
	 * @param repInfos the repInfos to set
	 */
	public void setRepInfos(ArrayList<SemanticNode> repInfos) {
		this.repInfos = repInfos;
	}

	/**
	 * @param other
	 * @return pattern一样则返回true
	 */
	public boolean equals(PatternInfo other){
		if(pattern.equals(other.pattern)){
			return true;
		}
		return false;
	}
	
	/**
	 * @return patSize
	 */
	public int getPatSize(){
		return patSize;
	}
	
	/**
	 * @param patSize
	 */
	public void setPatSize(int patSize){
		this.patSize = patSize;
	}
	
	
	/**
	 * 检查当前pattern是否匹配的泛化后的用户问句
	 * @param queryPatStr 泛化后的问句
	 * @return 匹配则返回正确
	 */
	public Matcher matcher(String queryPatStr){
		return pattern.matcher(queryPatStr);
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return the repInfos
	 */
	public ArrayList<SemanticNode> getRepInfos() {
		return repInfos;
	}

	/**
	 * @return the isRepAll
	 */
	public boolean isRepAll() {
		return isRepAll;
	}
	
	/**
	 * @param isRepAll
	 */
	public void setIsRepAll(boolean isRepAll){
		this.isRepAll = isRepAll;
	}

	/**
	 * @return the hasStart
	 */
	public boolean isHasStart() {
		return hasStart;
	}

	/**
	 * @param hasStart the hasStart to set
	 */
	public void setHasStart(boolean hasStart) {
		this.hasStart = hasStart;
	}

	/**
	 * @return the hasEnd
	 */
	public boolean isHasEnd() {
		return hasEnd;
	}

	/**
	 * @param hasEnd the hasEnd to set
	 */
	public void setHasEnd(boolean hasEnd) {
		this.hasEnd = hasEnd;
	}

	/**
	 * @return the text
	 */
	public String toString() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * 根据Pattern的匹配优先级进行比较。Pattern节点长的优先匹配；
	 * 长度相等时，节点匹配信息精确的优先级高。例如：index(净利润) num(%) > index(number) num(%)
	 * > index() num(%) > index() num(%)? > index() num() > index() num()?
	 */
	public int compareTo(PatternInfo other){
		if(other == null){
			return 1;
		}
		if(patSize > other.patSize){
			return 1;
		}else if(patSize < other.patSize){
			return -1;
		}else if(priority > other.priority){
			return 1;
		}else if(priority < other.priority){
			return -1;
		}
		return 0;
	}

	/**
	 * 普通匹配分值
	 */
	public void matchPriority(){
		this.priority += 1;
	}
	
	/**
	 * 可选匹配分值
	 */
	public void optionPriority(){
		this.priority += 0.5;
	}
	
	/**
	 * 非匹配分值
	 */
	public void notMatchPriority(){
		this.priority += 0;
	}

	
	/**
	 * 具体指标文本分值
	 */
	public void textPriority(){
		this.priority += 2;
	} 
	
	/**
	 * 具有特定属性的优先级
	 */
	public void attrPriority(){
		this.priority += 1.5;
	}
	
	/**
	 * 类型分值
	 */
	public void typePriority(){
		this.priority += 1;
	}
	
	/**
	 * end分值
	 */
	public void endPriority(){
		this.priority += 3;
	}
	
	/**
	 * start分值
	 */
	public void startPriority(){
		this.priority += 3;
	}
}
