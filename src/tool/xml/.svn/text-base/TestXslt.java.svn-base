package xml;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class TestXslt{
	public static void main(String argv[]){
		String src = "src\\tool\\xml\\test\\semanticPatternMap_.xml";
		String dest = "src\\tool\\xml\\test\\result.xml";
		String xslt = "src\\tool\\xml\\test\\semanticPatternMap_.xsl";

		File src2 = new File(src);
		File dest2 = new File(dest);
		File xslt2 = new File (xslt);

		Source srcSource = new StreamSource(src2);
		Result destResult = new StreamResult(dest2);
		Source xsltSource = new StreamSource(xslt2);

		try{
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(srcSource,destResult);
		}catch(TransformerConfigurationException e){
			e.printStackTrace();
		}catch(TransformerFactoryConfigurationError e){
			e.printStackTrace();
		}catch(TransformerException e){
			e.printStackTrace();
		}
	}
}