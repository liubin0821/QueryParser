package com.myhexin.qparser.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.myhexin.qparser.util.lightparser.LightParser;
import com.myhexin.qparser.util.lightparser.LightParserServletParam;

/**
 * 研究报告语义支持测试(代码在PhraseParserPluginSearchReport)
 * 输入："研报相关测试问句.txt"中的研报相关的问句
 * 判断：LightParser返回的结果应该是一个json格式的字符串
 * 输出：查看日志中的report_search字段
 */
public class LightParserServletTest {

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LightParserServletTest.class.getName());
	private final String QueryFileName = "研报相关测试问句.txt";
	
	private boolean testLightParser(String query){
		boolean isJson = true;
		LightParserServletParam requestParam = LightParserServletParam.getParam(query, "report");
		
		StringWriter out = new StringWriter();
		LightParser.createLightParserJson(requestParam, out);
		String lightParserResult = out.toString();
		logger_.info(String.format("LightParser:\tQuery:%s\tResult:%s",query,lightParserResult));
		try {  
			isJson = new JsonParser().parse(lightParserResult).isJsonObject();
			if(!isJson)
				logger_.error("Not a json object");
        } catch (JsonParseException e) {  
        	logger_.error(String.format("Excepion:",e.getMessage()));
        }  
		return isJson;
	}
	
	private List<String> readFileContent(String fileName){
		List<String> index=new ArrayList<String>();
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	index.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            logger_.error(String.format("Excepion:", e.getMessage()));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		return index;
	}
	
	@Test
	public void test() {
		List<String> queryList = readFileContent(QueryFileName);
		boolean allResultIsJson = true;
		for(String query : queryList){
			boolean isJson = testLightParser(query);
			if(allResultIsJson && !isJson) allResultIsJson = false;
		}
		Assert.assertTrue(allResultIsJson);
	}

}
