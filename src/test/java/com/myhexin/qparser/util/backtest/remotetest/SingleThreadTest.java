package com.myhexin.qparser.util.backtest.remotetest;

import java.util.List;

public class SingleThreadTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String url = "http://192.168.23.52:9100/backtestcond?xml=1&query=";
		String file = "D:/wwwwwwwwww/querys_part1.txt";
		List<String> list = BackTestRemoteTest.loadQueries(file);
		for(int i=0;i<300;i++)
			new Thread(new RemoteCallThread(url, list)).start();
	}

}
