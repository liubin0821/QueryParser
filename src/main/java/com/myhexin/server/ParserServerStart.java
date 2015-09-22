package com.myhexin.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.xml.XmlConfiguration;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.util.OnlineIpInfo;
import com.myhexin.tool.OSinfo;

public class ParserServerStart {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ParserServerStart.class.getName());
	
	public static void main(String args[]) {
		logger_.info("Try Start Jetty...");
		// 加载xml文件，初始spring容器
		//ApplicationContextHelper.loadApplicationContext();

	   
		
		Server server = new Server();
		server.setHandler(new DefaultHandler());
		XmlConfiguration cfg = null;
		try {
			//提取jar中webapp、jetty文件夹
			if(OSinfo.isWindows()){
				Runtime.getRuntime().exec("jar xf *.jar webapp/");
			}
			else if(OSinfo.isLinux()){
			    Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","jar xf *.jar webapp/"});
			}
			
			Thread.sleep(500);
			
			cfg = new XmlConfiguration(Thread.currentThread().getClass().getResourceAsStream("/jetty/jetty.xml"));
			cfg.configure(server);	
			
			server.start();
			logger_.info("Jetty Started...");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
