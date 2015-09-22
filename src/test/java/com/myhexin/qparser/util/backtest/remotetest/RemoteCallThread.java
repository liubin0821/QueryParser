package com.myhexin.qparser.util.backtest.remotetest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RemoteCallThread implements Runnable {

	private List<String> list = new ArrayList<String>();
	private String url;
	public RemoteCallThread(String url, List<String> list) {
		this.list = list;
		this.url = url;
	}
	//private static String url = "http://localhost:9100/backtestcond?xml=1&url=api&query=";
	@Override
	public void run() {
		
		long c = 0;
		while(true){
			for(String s : list) {
				try{
					long start = System.currentTimeMillis();
				String q = URLEncoder.encode(s);
				String url_send = null;
				if(url.indexOf("cfgdate")>0) {
					url_send = url;
				}else{
					url_send = url + q;
				}
				
				URL u = new URL(url_send);
				URLConnection conn = u.openConnection();
				InputStream is = conn.getInputStream();
				byte[] buf = new byte[8192];
				int n = -1;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				while ((n = is.read(buf)) > 0) {
					bos.write(buf, 0, n);
				}
				is.close();
				long end = System.currentTimeMillis();
				String s1 = bos.toString();
				System.out.println(c + " [" +Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + "] : [time=" + (end-start) + "] : " + s1.length());
				
				Thread.sleep(60);
				c++;
				//list.add(s1);
				}catch(Exception e){System.out.println(e.getMessage());}
			}
			
			if(c>=Long.MAX_VALUE-1) {
				c=0;
			}
		}
	}
}
