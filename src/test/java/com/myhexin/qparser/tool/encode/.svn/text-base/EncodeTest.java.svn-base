package com.myhexin.qparser.tool.encode;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EncodeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("resource")
	@Test
	public void testIsMessyCode() {
		assertEquals(false, Encode.isMessyCode(null));
		assertEquals(false, Encode.isMessyCode(""));
		try {
			BufferedReader br = null;
			String str = "";
			// 该文件保存的问句均为乱码
			String messCodeFile = "./src/test/com/myhexin/qparser/tool/encode/messy_code.txt";
			br = new BufferedReader(new InputStreamReader(new FileInputStream(messCodeFile), "UTF-8"));
			while ((str = br.readLine()) != null) {
				if (!Encode.isMessyCode(str))
					System.out.println(str);
				assertEquals(true, Encode.isMessyCode(str));
			}
			
			// 该文件保存的问句都不是乱码
			String noMessCodeFile = "./src/test/com/myhexin/qparser/tool/encode/no_messy_code.txt";
			br = new BufferedReader(new InputStreamReader(new FileInputStream(noMessCodeFile), "UTF-8"));
			while ((str = br.readLine()) != null) {
				if (Encode.isMessyCode(str))
					System.out.println(str);
				assertEquals(false, Encode.isMessyCode(str));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMain(){
		//String[] args = {"./src/test/com/myhexin/qparser/tool/encode/y.txt"};
		String queryText = "pe";
		Encode.isMessyCode(queryText);
		//Encode.main(args);
	}

}
