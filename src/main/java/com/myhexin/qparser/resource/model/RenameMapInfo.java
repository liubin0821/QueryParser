package com.myhexin.qparser.resource.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.IndexRenameRule;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.phrase.util.Consts;


/**
 * 配置表 configFile.parser_rename_rule
 * 对转出condition时,对一些指标重命名
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-12
 *
 */
public class RenameMapInfo implements ResourceInterface{
	private static RenameMapInfo instance = new RenameMapInfo();
	
	/*
	 * map 套 map
	 * Map<abs_指数领域， Map<old_name, new_name>>
	 * Map<abs_股票领域， Map<old_name, new_name>> 
	 */
	private Map<String, Map<String,String>> renameMap = null;
	
	private Map<String,String> all_renameMap = null;
	private RenameMapInfo() {
		
	}
	
	public static RenameMapInfo getInstance() {
		return instance;
	}
	
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	
	@Override
	public void reload() {
		List<IndexRenameRule> ruleList = mybatisHelp.getDateParserInfoMapper().getRenameRuleList();
		Map<String, Map<String,String>> temp_renameMap = new HashMap<String, Map<String,String>>();
		Map<String,String> temp_allMap = new HashMap<String, String>();
		for(IndexRenameRule r : ruleList ) {
			if(r.getDomain()==null) {
				temp_allMap.put(r.getOriginal_name(), r.getNew_rename());
			}else{
				Map<String, String> nameMap1 = temp_renameMap.get(r.getDomain());
				if(nameMap1==null) {
					nameMap1 = new HashMap<String, String>();
					temp_renameMap.put(r.getDomain(), nameMap1);
				}
				nameMap1.put(r.getOriginal_name(), r.getNew_rename());
				
				//把股票领域的包含进来
				if(r.getDomain().equals(Consts.CONST_absStockDomain) && temp_allMap.get(r.getOriginal_name())==null){
					temp_allMap.put(r.getOriginal_name(), r.getNew_rename());
				}
			}
		}
		all_renameMap = temp_allMap;
		renameMap = temp_renameMap;
	}
	
	/*private String removeNumeric(String name) {
		//处理5日线上穿10日线这种情况,把数字去掉,配置的时候，也不带数字,配置=?日线上穿?日线
		StringBuilder buf =new StringBuilder();
		for(int i=0;i<name.length();i++) {
			char c = name.charAt(i);
			if(c>='0' && c<='9') {
				continue;
			}else{
				buf.append(c);
			}
		}
		return buf.toString();
	}*/
	
	
	private static Pattern p1 = Pattern.compile("^([0-9]{1,3})(日|周|月|年)(线|均线)(上穿|金叉)([0-9]{1,3})(日|周|月|年)(线|均线)$");
	private static Pattern p2 = Pattern.compile("^([0-9]{1,3})(日|周|月|年)(线|均线)(上移|下移)$");
	public String getNewName(String name, String domain) {
		if(domain==null) {
			return getNewName(name);
		}else{
			Map<String, String> nameMap = renameMap.get(domain);
			if(nameMap!=null) {
				//处理5日线上穿10日线这种情况,把数字去掉,配置的时候，也不带数字,配置=?日线上穿?日线
				if(Consts.CONST_absZhishuDomain.equals(domain) && (p1.matcher(name).matches() || p2.matcher(name).matches())) {
					return "指数@"+name;
				}else{
					String newName  = nameMap.get(name);
					if(newName!=null) {
						return newName;
					}else if (Consts.CONST_absZhishuDomain.equals(domain) && name.indexOf("指数")<0) {
						return "指数@"+name; //改进一下,不用每个指数领域的都要配置一个.
						//配置不能删除的原因是,想"涨跌幅"要转成"涨跌幅:不复权"
					}
				}
			}
			return name;
		}
	}
	
	
	public String getNewName(String name) {
		String newName = all_renameMap.get(name);
		if(newName!=null) {
			return newName;
		}
		return name;
	}
}
