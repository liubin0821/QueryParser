package xml;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class GetXmlWithXslt {

	/*
	 * 把srcFile指定的文件,按照xsltFile指定的模板转换, 保存到destFile指定的文件下
	 */
	public static void translate(String destFile, String xsltFile,
			String srcFile) {
		String src_ = "src\\tool\\xml\\test\\semanticPatternMap_.xml";
		String dest_ = "src\\tool\\xml\\test\\result.xml";
		String xslt_ = "src\\tool\\xml\\test\\semanticPatternMap_.xsl";
		if (srcFile == null || srcFile.equals("")) {
			System.err.println("src不能为空");
		} else if (destFile == null || destFile.equals("")) {
			System.err.println("dest不能为空");
		} else if (xsltFile == null || xsltFile.equals("")) {
			System.err.println("xslt不能为空");
		}
		src_ = srcFile;
		dest_ = destFile;
		xslt_ = xsltFile;

		File src2 = new File(src_);
		File dest2 = new File(dest_);
		File xslt2 = new File(xslt_);

		Source srcSource = new StreamSource(src2);
		Result destResult = new StreamResult(dest2);
		Source xsltSource = new StreamSource(xslt2);

		try {
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(srcSource, destResult);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
