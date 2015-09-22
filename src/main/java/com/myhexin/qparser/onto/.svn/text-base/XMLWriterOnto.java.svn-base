package com.myhexin.qparser.onto;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.xmlreader.XmlReader;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

/**
 * 用于从stock_onto_src.xml文件中取出包含在data/stock/indexs_need_list.txt的指标列表
 * 将指标列表保存到data/stock/stock_onto_dest.xml文件中
 */
public class  XMLWriterOnto{
	
	public static HashSet<String> needIndexs = null;
	public static String indexsListFileName = "data/stock/indexs_need_list.txt";
	public static String indexsListSrcFile = "data/stock/stock_onto_src.xml";
	public static String indexsListDestTempFile = "data/stock/stock_onto_dest_null.xml";
	public static String indexsListDestFile = "data/stock/stock_onto_dest.xml";
	
	static {
		try {
			loadIndexsList(Util.readTxtFile(indexsListFileName, true));
		} catch (DataConfException e) {
			e.printStackTrace();
		}
	}
	
	// 加载需要导入的指标列表
    public static void loadIndexsList(List<String> lines) {
    	needIndexs = new HashSet<String>();
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String word = lines.get(iLine).trim();
            needIndexs.add(word);
        }
    }
    
    // 导入指标列表
    public static void saveIndexsList() {
        try {
        	DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder db=factory.newDocumentBuilder();
            Document xmldoc=db.parse(new File(indexsListSrcFile));
            Element root=xmldoc.getDocumentElement();
            ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "class");
    		for (int i = 0; i < nodes.size(); i++) {
    			Node node = nodes.get(i);
    			String label = XmlReader.getAttributeByName(node, "label");
    			if (needIndexs.contains(label)) {
    				ArrayList<Node> fields = XmlReader.getChildElementsByTagName(node, "field");
    				if (!(fields != null && fields.size() > 0)) {
	    				// 创建新节点
						Element element = xmldoc.createElement("field");
						element.setAttribute("label", "stock");
						node.appendChild(element);
    				}
    			} 
    			else {
    				root.removeChild(node).normalize();
    			}
    		}
            //output(xmldoc);
            saveXml(indexsListDestTempFile, xmldoc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 将Document输出到文件
    public static void saveXml(String fileName, Document doc) {
        TransformerFactory transFactory=TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            DOMSource source=new DOMSource();
            doc.normalize();
            source.setNode(doc);
            StreamResult result=new StreamResult();
            result.setOutputStream(new FileOutputStream(fileName));
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }   
    }
    
    // 取出xml文件中的空白行
	public static void clearWhitespace() {
		String filename1 = indexsListDestTempFile;
		String filename2 = indexsListDestFile;
		File file = new File(filename1);
		InputStream is = null;
		BufferedReader br = null;
		String tmp;
		FileWriter writer = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			writer = new FileWriter(filename2, false);
			while ((tmp = br.readLine()) != null) {
				if (tmp.equals(""))
					;
				else {
					writer.write(tmp + "\n");
				}
			}
			writer.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public static void main(String[] args) {
    	saveIndexsList();
    	clearWhitespace();
    }
}
