package com.myhexin.qparser.util.condition;

import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;

public class ConditionBuilderTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = "D:\\query.txt";
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		List<String[]> queries = null; //sampling.getQueries(filePath);
		String query = "";
		String domain = null;
		Calendar backtestTime = null;
		for (int i = 0, length = queries.size(); i < length; i++) {

			query = queries.get(i)[1];
			System.out.println("-------total query number:" + length + " current: " + (i + 1));
			if (query != null && query.length() > 0) {
				// 调解析parser
				ParseResult pr = ConditionParser.parse(query, "STOCK", domain, backtestTime);
				try {
					List<BackTestCondAnnotation> list = ConditionBuilder.buildCondition(pr, query, null);
					for (BackTestCondAnnotation s : list) {
						System.out.println(query + " " + s.getResultCondJson());
					}
				} catch (BacktestCondException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		
		// 解析, 转condition

	}

	

}
