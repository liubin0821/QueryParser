package com.myhexin.qparser.util.condition;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConditionByHttp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader= new BufferedReader(new InputStreamReader(new FileInputStream("C:/files.txt")));
		String s =null;
		while( (s=reader.readLine())!=null) {
			if(s.trim().length()>0) {
				String r = readBytes(s);
				System.out.println(s + "\n" + r);
			}
		}
		reader.close();
	}

	static String URL_PARSER = "http://192.168.23.52:9100/condition?qType=ALL&query=";
	public static String readBytes(String query) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		URL urll;
		InputStream inputStream = null;
		try {
			urll = new URL(URL_PARSER + URLEncoder.encode(query, "utf-8"));
			HttpURLConnection uc = (HttpURLConnection) urll.openConnection();
			inputStream = uc.getInputStream();
			byte[] b = new byte[8192];
			int n = 0;
			while ((n = inputStream.read(b)) > 0) {
				os.write(b, 0, n);
			}
			inputStream.close();
			
			return new String(os.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
