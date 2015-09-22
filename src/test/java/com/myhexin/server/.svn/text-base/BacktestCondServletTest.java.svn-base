package com.myhexin.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;


public class BacktestCondServletTest {

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(BacktestCondServletTest.class.getName());
	private final String QueryFileName = "queryAll.txt";//文件
	
	private void doTest(){
		getBacktestCond("pe>pb");
	}
	
	private void doTestFromFile(String fileName){
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            int id=0;
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	String[] strs = tempString.split("_###_");
            	if(strs.length==3 && strs[2]!=null && strs[2].length()>0){
            		System.out.println(++id);
            		getBacktestCond(strs[2]);
            	}
            }
            reader.close();
        } catch (IOException e) {
            logger_.error(String.format("Excepion:", e.toString()));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	logger_.error(String.format("Excepion:", e1.toString()));
                }
            }
        }
	}
	
	private void getBacktestCond(String query){
		logger_.info("query:"+query);
		List<BackTestCondAnnotation> annotationList = ConditionParser.compileToCond(query, null, null, null);
		String result = getResultJson(query, annotationList);
		logger_.info("result:"+result);
	}
	
	private String getResultJson(String query, List<BackTestCondAnnotation> jsonResults) {
		BackTestCondAnnotation jsonResult = null;
		if(jsonResults!=null && jsonResults.size()>0) {
			jsonResult = jsonResults.get(0);
		}else{
			jsonResult = new BackTestCondAnnotation();
		}
		return jsonResult.getResultCondJson();
    }
	
	@Test
	public void test() {
		//doTest();
		doTestFromFile(QueryFileName);
	}

}
