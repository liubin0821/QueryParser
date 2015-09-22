package com.myhexin.qparser.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.ExceptionUtil;

public class HttpConfigFile {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(HttpConfigFile.class);

	/**
	 * 1。http读取文件 2.计算文件MD5 3.http读取服务端文件MD5 4.比较2个MD5
	 * 5.md5不一样，logger.warning();
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] readBytes(String fileName) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String newMd5 = null;
		String oldMd5 = null;
		URL urll;
		InputStream inputStream = null;
		try {
			urll = new URL(Param.getHttp_config_file() + fileName);
			HttpURLConnection uc = (HttpURLConnection) urll.openConnection();
			inputStream = uc.getInputStream();
			byte[] b = new byte[8192];
			int n = 0;
			while ((n = inputStream.read(b)) > 0) {
				os.write(b, 0, n);
			}
			inputStream.close();
			newMd5 = calcMd5(os.toByteArray());
			oldMd5 = readMd5(fileName);
			if (newMd5==null || oldMd5==null || !newMd5.equals(oldMd5)) {
				logger_.warn(String.format("[WARNING] %s MD5 NOT MATCH",fileName));
			}
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error(String.format("[ERROR] read  Httpurl  wrong [%s]",Param.getHttp_config_file() + fileName));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String readStr(String fileName) {
		byte[] data = readBytes(fileName);
		if (data == null) {
			return null;
		} else {
			return new String(data);
		}
	}

	public static Document readDoc(String fileName) throws SAXException, IOException,ParserConfigurationException {
		byte[] data = readBytes(fileName);
		if (data == null) {
			return null;
		} else {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(new String(data))));
			return doc;
		}
	}

	/**
	 * 通过http call 拿到文件的md5
	 * 
	 * @param fileName
	 * @return
	 */
	private static String readMd5(String fileName) {
		return null;
	}

	private static String calcMd5(byte[] fileBytes) {
		try {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
					'9', 'A', 'B', 'C', 'D', 'E', 'F' };
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(fileBytes); // 跟新摘要
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 高四位
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			logger_.error(String.format("[ERROR] calcMd5 wrong , Error Message = [%s]",e.getMessage()));
			logger_.error(ExceptionUtil.getStackTrace(e));
			return null;
		}
	}
}
