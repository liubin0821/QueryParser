package com.myhexin.qparser.util.lightparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;

/**
 * 从stock/lightparser_type.xml读取lightparser信息
 * 
<Type label="NEWS">
    <Description>新闻</Description>
    <SubType label="ASTOCK">股票简称|股票代码</SubType>
    <SubType label="CONCEPT">所属概念</SubType>
  </Type>
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-24
 *
 */
public class LightParserTypeConf {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LightParserTypeConf.class.getName());
	
	private static List<String> lightParserTypes = null;
	private static HashMap<String, LightParserType> lightParserTypeMap = null;
	
	public static void init() {
		lightParserTypes = new ArrayList<String>(); //类型
		lightParserTypeMap = new HashMap<String, LightParserType>();//类型and子类型
	}
	
	public static void reload(List<String> lightParserTypesTemp, HashMap<String, LightParserType> lightParserTypeMapTemp) {
		lightParserTypes = lightParserTypesTemp;
		lightParserTypeMap = lightParserTypeMapTemp;
	}
	
	public static void loadLightParserTypeConf(Document doc) {
		lightParserTypes = LightParserTypeXMLReader.loadLightParserTypes(doc);
		lightParserTypeMap = LightParserTypeXMLReader.loadLightParserTypeMap(doc);
	}
	
	public static List<String> getLightParserTypes() {
		return lightParserTypes;
	}
	
	public static HashMap<String, LightParserType> getLightParserTypeMap() {
		return lightParserTypeMap;
	}
}
