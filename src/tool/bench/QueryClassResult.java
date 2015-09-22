/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench;

import java.io.Serializable;

/**
 * the Class QueryClassResult
 *
 */
public class QueryClassResult implements Serializable{
	/** */
	private static final long serialVersionUID = -4361448069059127247L;
	/** */
	public String qid = "0";
    /** 问句*/
    public String text;
	/** 问句语义树编码*/
	public int treeCode = -1;
	/** 问句语义树泛化形式*/
	public String tree = "";
	/** pattern编号*/
	public int patternCode = -1;
	/** 问句pattern形态*/
	public String queryPattern = "";
    /** 问句的同义词*/
    public String synonyms = "";
	/** 问句中指标*/
	public String indexs = "";
	/** 问句中计算词语*/
	public String operators = "";
    /** 问句中时间日期*/
    public String dates = "";
    /** 问句的未识别词语*/
    public String unknowns = "";
    /** 问句的patterns*/
    public String semanticPatterns = "";
    /** 问句pattern的新旧, 默认为旧 */
    public boolean isNew = true ;  
}
