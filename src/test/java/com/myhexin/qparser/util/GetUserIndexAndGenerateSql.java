package com.myhexin.qparser.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import com.google.gson.Gson;

public class GetUserIndexAndGenerateSql {
	public static void main(String[] args) throws Exception {
		String url = "http://192.168.23.52:9100/userindex?index=%s&q=%s";
		URL u = new URL(URLEncoder.encode(String.format(url, "成长快","连续3年归属母公司股东的净利润(同比增长率)大于25%"),"utf-8") );
		InputStream in = u.openConnection().getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int n=-1;
		while( (n=in.read(buf))>0) {
			bos.write(buf, 0, n);
		}
		
		String s = new String(bos.toByteArray());
		//System.out.println(URLDecoder.decode(s, "utf-8"));
		System.out.println(s);
		
		Map<?,?> jmo = new Gson().fromJson(s, Map.class);
		String result = (String)jmo.get("result");
		String index_name = (String)jmo.get("index_name");
		String query = (String)jmo.get("query");
		String unit = (String)jmo.get("unit");
		
		System.out.println(index_name);
		System.out.println(query);
		System.out.println(unit);
		System.out.println(result);
		
		String sql = "INSERT INTO configFile.parser_user_index(index_name, unit_str, query_text, node_result) VALUES('%s','%s','%s','%s');";
		String new_sql = String.format(sql, index_name,unit,query,result);
		System.out.println(new_sql);
	}
}
