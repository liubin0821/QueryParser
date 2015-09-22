package com.myhexin.qparser.util.backtest.remotetest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackTestRemoteTest {

	public static List<String> loadQueries(String file) throws Exception {
		
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = null;
		int i = 0;
		List<String> list = new ArrayList<String>();
		
		//FileOutputStream fos = new FileOutputStream("D:/wwwwwwwwww/querys_part1.txt");
		while ((s = br.readLine()) != null) {
			// System.out.println(s1);
			list.add(s);
			//fos.write((s1+"\n").getBytes() );
			if (i++ > 10000) {
				break;
			}
		}
		//fos.close();
		br.close();
		return list;
	}

	public static void runTest(String url, String file) throws Exception {
		// TODO Auto-generated method stub
		List<String> list = loadQueries(file);
		//while(true){
			ExecutorService pool = Executors.newFixedThreadPool(1);
			for(int i=0;i<100;i++){
				pool.execute(new RemoteCallThread(url, list));
			}
			/*pool.execute(new RemoteCallThread(url, list));
			pool.execute(new RemoteCallThread(url, list));
			pool.execute(new RemoteCallThread(url, list));
			pool.execute(new RemoteCallThread(url, list));*/
			pool.shutdown();
		//}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//String url = "http://192.168.23.52:9100/cfgdate?param=[{\"index\":\"净利润\",\"prop\":\"报告期\"}]";
		String url = "http://192.168.23.52:9100/backtestcond?xml=1&query=";
		//String url = "http://192.168.23.52:9100/relatedquery?qType=stock&query=";
		//String url = "http://192.168.23.52:9100/parser?qType=stock&q=";
		//String url = "http://192.168.23.52:9100/lightparser?channel=report&q=";
		//String url = "http://192.168.23.52:9100/chunkparser?q=";
		//String url = "http://192.168.23.52:9100/configcheck";
		//String url = "http://192.168.23.52:9100/queryparser";
		//String url = "http://192.168.23.52:9100/scoreparser";
//		String url = "http://192.168.23.52:9100/miparser";
//		String url = "http://192.168.23.52:9100/parserresult";
		
		String file = "D:/wwwwwwwwww/querys_part1.txt";
		
		runTest(url, file);
	}
}
