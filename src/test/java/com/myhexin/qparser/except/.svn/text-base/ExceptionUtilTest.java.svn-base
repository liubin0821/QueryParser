package com.myhexin.qparser.except;

public class ExceptionUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			throw new Exception("asda");
		}catch(Exception e)
		{
			try{
				throw new Exception(e);
			}catch(Exception e1)
			{
				String s = ExceptionUtil.getStackTrace(e1);
				System.out.println(s);
			}
		}
	}

}
