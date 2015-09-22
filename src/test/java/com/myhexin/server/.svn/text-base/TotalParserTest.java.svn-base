package com.myhexin.server;

import java.net.URLDecoder;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.chunk.ChunkQueryResult;

public class TotalParserTest {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		ApplicationContextHelper.loadApplicationContext();
		String query = "医药行业本月涨跌幅排名前十";
		String chunk2 = "%7B%22chunk%22%3A%5B%7B%22value%22%3A%22%E5%8C%BB%E8%8D%AF%E8%A1%8C%E4%B8%9A_%26_%E6%9C%AC%E6%9C%88%E6%B6%A8%E8%B7%8C%E5%B9%85%E6%8E%92%E5%90%8D%E5%89%8D%E5%8D%81%22,+%22prob%22%3A0.985628%7D%5D%7D";
		String chunk = URLDecoder.decode(chunk2, "utf-8");
		System.out.println(chunk);
		String qType = "STOCK";
		boolean isDebug = true;
		
		 ChunkQueryResult chunkQueryResult = TotalParser.parserQuery(query, chunk,qType, isDebug);
		 System.out.println(chunkQueryResult.getChunk());
	}

}
