package xml;

public class TestGetObject {

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-22 下午4:10:57
	 * @description:
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String src = "src\\tool\\xml\\test\\indexGroupMap_.xml";
		String dest = "src\\tool\\xml\\test\\result.xml";
		String  xslt= "src\\tool\\xml\\test\\indexGroupMap_.xsl";
		
		GetXmlWithXslt.translate(dest, xslt, src);
		
		//生成对象
		Object m = new GetObjectFromXml111("src\\tool\\xml\\test\\result.xml").getObject("IndexGroupMap_8");
		//写入到xml文件
		new GetXmlFromObject111("src\\tool\\xml\\test\\programeCreate.xml").createXML(m);
	}
}
