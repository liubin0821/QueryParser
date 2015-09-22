package com.myhexin.server.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.server.processor.ParserProcessorAbstract;
import com.myhexin.server.service.ParserServiceAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:24:19
 * @description Phrase解析服务类
 */
public class PhraseParserService extends ParserServiceAbstract {
	
	/**
	 * @description 无参构造函数，建议不要删除（Spring中可能会用到）
	 */
	public PhraseParserService() {
	}
	
	/**
	 * @descrption Phrase解析服务服务函数具体实现
	 * @return 返回类型ParserResult，成员result变量中保存String类型（json格式）结果
	 */
	@Override
	public ParserResult serve(ParserParam param) {
		String queryText = param.getQueryText();
		String queryType = param.getQueryType();
		Gson json = new Gson();
		
		ParserResult result = processor.process(param);
		
        List<String> retStandard = ((ParseResult)result.getResult()).standardQueries;
        List<String> standard = new ArrayList<String>();
        List<Integer> scores = ((ParseResult)result.getResult()).standardQueriesScore;
        for(int i=0; i<retStandard.size(); i++)
        {
            int score = scores.get(i);
            if(score >= 80)
                standard.add(retStandard.get(i));
        }
        if(standard.size() == 0)
            standard.add(queryText);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("errno", result.getErrno());
        map.put("errmsg", result.getErrmsg());
        map.put("result", standard);        
        
		// 返回值
		ParserResult parserResult = new ParserResult();
		parserResult.setResult(json.toJson(map));
		return parserResult;
	}	
	
	public static void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		ArrayList<String> qlist = new ArrayList<String>();

		try {
			// 解析问句读取
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("query.txt"), "utf-8"));
			String s = null;
			boolean flag = true;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				if (s.startsWith("#") || s.length() == 0) {
					continue;
				} else if (s.startsWith("break")) {
					break;
				} else if (s.startsWith("/*")) {
					flag = false;
				} else if (s.endsWith("*/")) {
					flag = true;
					continue;
				}
				if (flag) {
					qlist.add(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// phraseParser配置于qphrase.xml文件
		PhraseParserService service = (PhraseParserService) ApplicationContextHelper.getBean("phraseParserService");
		
		for (String query : qlist) {
			long beforeTime = System.currentTimeMillis();
			ParserParam param = new ParserParam();
			param.setQueryText(query.toLowerCase());
			try {
				System.out.println(service.processor.process(param).getLog());
			} catch (Exception e) {
				e.printStackTrace();
			}
			long afterTime = System.currentTimeMillis();
			long timeDistance = afterTime - beforeTime;
			System.out.println("time:" + timeDistance);
		}
	}

	public static void main(String[] args) {
		test();
	}
}
