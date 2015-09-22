package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IndexCalcExpr implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  int id;
	private String index_name;
	private String calc_expr;
	private String prop_calc_expr;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIndex_name() {
		return index_name;
	}
	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}
	public String getCalc_expr() {
		return calc_expr;
	}
	public void setCalc_expr(String calc_expr) {
		this.calc_expr = calc_expr;
	}
	public String getProp_calc_expr() {
		return prop_calc_expr;
	}
	public void setProp_calc_expr(String prop_calc_expr) {
		this.prop_calc_expr = prop_calc_expr;
		load(prop_calc_expr);
	}
	
	//日期计算
	public String getDatePropValue(String indexName, String propName, String propValue) {
		//String expr = propCalcExprMap.get(indexName + "." + propName);
		//System.out.println("Date Prop Name : " + indexName + "." + propName);
		//System.out.println("Date Calculation expr: " + expr);
		/*if(expr!=null){
			return SuffixExprBuilder.stringToDateCalc(expr, propName, propValue);
		}else{
			return null;
		}*/
		return null;
	}
	
	private Map<String, String> propCalcExprMap = new HashMap<String, String>();
	public void load(String exprs) {
		if(exprs!=null && exprs.length()>0) {
			String[] exprarr = exprs.split(";");
			if(exprarr!=null && exprarr.length>0) {
				for(String s : exprarr) {
					String[] expr1 = s.split("=");
					if(expr1!=null && expr1.length==2) {
						propCalcExprMap.put(expr1[0].trim(), expr1[1].trim());
					}
				}
			}
		}
	}
	
}
