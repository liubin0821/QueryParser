package com.myhexin.qparser.matcher;

/**
 *从各种文件中读取关键字匹配关系
 *@author wangjiajia 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class KeywordDictLoader {

	/**
	 * 读取的关键字以list保存，格式为--关系词：关键字，关键字，关键字……
	 * 
	 * @param name
	 *            文件名字
	 * @param type
	 *            文件类型：1 为xml，2为txt，3为db
	 * @return
	 */
	public Map<String, String> keywordDictLoader(String name, int type) {
		List<String> list = new ArrayList<String>();
		switch (type) {
		case 1:
			list = readXmlLine(name);
			break;
		case 2:
			list = readTxtLine(name);
			break;
		case 3:
			list = readDbLine(name);
			break;
		}
		Map<String, String> keywordDict = keywordDict(list);
		return keywordDict;
	}

	/**
	 * 根据表名读取，并整合成list
	 * 
	 * @param name
	 * @return
	 */
	private static List<String> readDbLine(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据名字从xml中读取每一行数据保存成list
	 * 
	 * @param name
	 * @return
	 */
	private static List<String> readXmlLine(String xmlName) {
		List<String> list = new ArrayList<String>();
		try {
			File f = new File(xmlName + ".xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element foo;
			String s = null;
			for (Iterator it = root.elementIterator("LIST"); it.hasNext();) {
				foo = (Element) it.next();
				s = foo.elementText("RELATION").trim() + "："
						+ foo.elementText("KEYWORD").trim();
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return list;
	}

	/**
	 * 根据文本名字从文本中读取每一行数据保存成list
	 * 
	 * @param txtName
	 * @return
	 */
	private static List<String> readTxtLine(String txtName) {
		List<String> qlist = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(txtName + ".txt");
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			boolean flag = true;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				if (s.startsWith("#") || s.length() == 0) {
					continue;
				} else if (s.startsWith("break")) {
					break;
				} else if (s.startsWith("/*")) {
					flag = false;
				} else if (s.endsWith("*/")) {
					flag = true;
					continue;
				}
				if (flag) {
					String relation[] = s.split("：");
					if (relation[1].contains("，")) {
						String keywords[] = relation[1].split("，");
						for (int i = 0; i < keywords.length; i++) {
							s = relation[0] + "：" + keywords[i];
							qlist.add(s);
						}
					} else {
						qlist.add(s);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return qlist;
	}

	/**
	 * 关系词为key，关键字为vaules返回
	 * 
	 * @param list
	 * @return
	 */
	private static Map<String, String> keywordDict(List<String> list) {
		Map<String, String> keywordDict = new HashMap<String, String>();
		String key = null;
		String value = null;
		try {
			for (String line : list) {
				value = line.split("：")[0];
				key = line.split("：")[1];
				keywordDict.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keywordDict;
	}
}
