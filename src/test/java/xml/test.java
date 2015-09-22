/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-11-5 下午3:43:45
 * @description:   	
 * 
 */
package xml;

import java.util.regex.Pattern;

public class test {

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-11-5 下午3:43:45
	 * @description:   	
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//GetXmlWithXslt.translate("My.xml", "src/test/xml/stock_phrase.xsl", "data/stock/stock_phrase.xml");
		//String s = "5日线上穿10日线";
		String s = "11日线上穿223日线";
		//Pattern p = Pattern.compile("([0-9]{1,2})");
		Pattern p = Pattern.compile("^(.*)([0-9]{1,3})(日|周|月|年)(线|均线)(.*)$");
		if(p.matcher(s).matches()) {
			System.out.println("ok");
		}else{
			System.out.println("failed");
		}
		
		//System.out.println(s.replaceAll("([0-9]{1,5})", "?"));
	}

}
