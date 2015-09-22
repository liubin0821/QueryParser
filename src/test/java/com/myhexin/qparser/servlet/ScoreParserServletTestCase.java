package com.myhexin.qparser.servlet;


import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

public class ScoreParserServletTestCase extends TestCase {

	private static String[] test_q_scores=  new String[]{
		"pe>10", "100",
		"pe","100"
		
	};
	
	
	public void testDoPost() {
		ScoreParserServlet servlet = new ScoreParserServlet();
		
		for(int i=0;i<test_q_scores.length;i+=2){
			String q = test_q_scores[i];
			String score = test_q_scores[i+1];
			int score_val = Integer.parseInt(score);
			
			HttpServletRequestMock req = new HttpServletRequestMock();
			req.setParameter("q", "pe>10");
			HttpServletResponseMock res = new HttpServletResponseMock();
			try {
				servlet.doGet(req, res);
			} catch (Exception e) {
				e.printStackTrace();
				assertEquals(1,2);
			}
			
			String ret = res.getOutput();
			int ret_val = Integer.parseInt(ret);
			System.out.println(q + "'s score = " + ret);
			assertEquals(ret_val, score_val);
		}
	}
}
