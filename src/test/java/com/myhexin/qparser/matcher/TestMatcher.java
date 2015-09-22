package com.myhexin.qparser.matcher;

/**
 *matcher的测试脚本
 *@author wangjiajia 
 */
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.csv.ReadCsv;

import junit.framework.TestCase;

public class TestMatcher extends TestCase {
	public void test1() {
		matcher(1);
	}
	
	public void test2() {
		matcher(2);
	}
	
	public void test3() {
		matcher(3);
	}
	
	public void test4() {
		matcher(4);
	}
	
	public void test5() {
		matcher(5);
	}
	
	public void test6() {
		matcher(6);
	}
	
	public void test7() {
		matcher(7);
	}
	
	public void test8() {
		matcher(8);
	}
	
	public void test9() {
		matcher(9);
	}
	
	public void test10() {
		matcher(10);
	}
	
	public void test11() {
		matcher(11);
	}
	
	public void test12() {
		matcher(12);
	}
	
	public void test13() {
		matcher(13);
	}
	
	private void matcher(int num) {
		Matcher matcher = new Matcher();
		MatchResult ret = new MatchResult();
		List<String[]> matchers = new ArrayList<String[]>();
		String caseId = null;
		try {
			ReadCsv csv = new ReadCsv("data/matcher/test.csv");
			caseId = csv.getString(num, 0);
			String description = csv.getString(num, 1);
			System.out.println("**********" + caseId + "**********");
			System.out.println("*****description:" + description + "*****");
			String fname = csv.getCsvString(num, csv, "fname");
			String type = csv.getCsvString(num, csv, "type");
			List<String> nodes = csv.getCsvList(num, csv, "nodes");
			List<String> checks = csv.getCsvList(num, csv, "checks");
			String isMatch = csv.getCsvString(num, csv, "isMatch");
			ret = matcher.match(nodes, fname, Integer.parseInt(type));
			if (isMatch.equals("N")) {
				assertFalse(ret.isSuccess());
			} else {
				assertTrue(ret.isSuccess());
				matchers = ret.getMatchers();
				for (int i = 0; i < checks.size(); i++) {
					assertEquals(matchers.get(i)[1], checks.get(i));
				}
			}
		} catch (Exception e) {
			System.out.println(caseId + " is Fail!");
		}
	}

}
