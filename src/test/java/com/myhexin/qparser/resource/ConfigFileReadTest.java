package com.myhexin.qparser.resource;

import org.springframework.context.ApplicationContext;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

import junit.framework.TestCase;

public class ConfigFileReadTest extends TestCase{
	public void testReadFile(){
		ApplicationContextHelper.loadApplicationContext();
		HttpConfigFile file=new HttpConfigFile();
		System.out.println(file.readStr("dzc.txt"));
	}

}
