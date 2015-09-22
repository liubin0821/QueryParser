package com.myhexin.server.test;

/**
 * csv工具
 * @author wangjiajia
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class ReadCsv {
	private String filename = null;

	private BufferedReader bufferedreader = null;

	private List list = new ArrayList();

	public ReadCsv() {

	}

	/**
	 * 根据csv的文件名字得到该csv每行数据，保存成list
	 * 
	 * @param filename
	 *            csv文件全称
	 * @throws IOException
	 */
	public ReadCsv(String filename) throws IOException {
		this.filename = filename;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
		bufferedreader = new BufferedReader(isr);
		String stemp = null;
		while ((stemp = bufferedreader.readLine()) != null) {
			list.add(stemp);
		}
	}

	public List getList() throws IOException {
		return list;
	}

	public int getRowNum() {
		return list.size();
	}

	public int getColNum() {
		if (!list.toString().equals("[]")) {
			if (list.get(0).toString().contains(",")) {
				return list.get(0).toString().split(",").length;
			} else if (list.get(0).toString().trim().length() != 0) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public String getRow(int index) {
		if (this.list.size() != 0)
			return (String) list.get(index);
		else
			return null;
	}

	public String getCol(int index) {
		if (this.getColNum() == 0) {
			return null;
		}
		StringBuffer scol = new StringBuffer();
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				temp = it.next().toString();

				scol = scol.append(temp.split(",")[index] + ",");
			}
		} else {
			for (Iterator it = list.iterator(); it.hasNext();) {
				temp = it.next().toString();
				scol = scol.append(temp + ",");
			}
		}
		String str = new String(scol.toString());
		str = str.substring(0, str.length() - 1);
		return str;
	}

	/**
	 * 根据行列得到指定行列的字符串
	 */
	public String getString(int row, int col) {
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			temp = list.get(row).toString().split(",")[col];
		} else if (colnum == 1) {
			temp = list.get(row).toString();
		} else {
			temp = null;
		}
		return temp;
	}

	public void CsvClose() throws IOException {
		this.bufferedreader.close();
	}

	/**
	 * 得到第一行指定文字对应下一行的字符串
	 */
	public String getCsvString(int caseNum, String csvName, String name)
			throws IOException {
		String string = null;
		ReadCsv cu = new ReadCsv(csvName);
		string = getCsvString(caseNum, cu, csvName);
		return string;
	}

	/**
	 * 得到第一行指定文字对应下一行的字符串
	 */
	public String getCsvString(int caseNum, ReadCsv cu, String name)
			throws IOException {
		String string = null;
		List tt = cu.getList();
		String title = (String) tt.get(0);
		String str[] = title.split(",");
		int i = 0;
		for (; i < str.length; i++) {
			if (name.equals(str[i])) {
				break;
			}
		}
		String caseList = (String) tt.get(caseNum);
		str = caseList.split(",");
		string = str[i];
		cu.CsvClose();
		return string;
	}

	/**
	 * 得到第一行指定文字对应下一行，返回list
	 */
	public List<String> getCsvList(int caseNum, String csvName, String name)
			throws IOException {
		List<String> list = new ArrayList<String>();
		ReadCsv cu = new ReadCsv(csvName);
		list = getCsvList(caseNum, cu, csvName);
		return list;
	}

	/**
	 * 得到第一行指定文字对应下一行，返回list
	 */
	public List<String> getCsvList(int caseNum, ReadCsv cu, String name)
			throws IOException {
		String string[];
		List<String> list = new ArrayList<String>();
		List<String> tt = cu.getList();
		String title = (String) tt.get(0);
		String str[] = title.split(",");
		int i = 0;
		for (; i < str.length; i++) {
			if (name.equals(str[i])) {
				break;
			}
		}
		String caseList = (String) tt.get(caseNum);
		str = caseList.split(",");
		if (str[i].contains(";")) {
			string = str[i].split(";");
			for (int j = 0; j < string.length; j++) {
				list.add(string[j]);
			}
		} else {
			list.add(str[i]);
		}
		cu.CsvClose();
		return list;
	}

	public void createCsvTest1(HttpServletResponse Response) throws IOException {
		ReadCsv cu = new ReadCsv("test.csv");
		List tt = cu.getList();
		String str[];
		for (Iterator itt = tt.iterator(); itt.hasNext();) {
			String fileStr = itt.next().toString();
			str = fileStr.split(",");
			for (int i = 0; i <= str.length - 1; i++) { // 拆分成数组 用于插入数据库中
				System.out.print("str[" + i + "]=" + str[i] + " ");
			}
			System.out.println("");
		}
		cu.CsvClose();
	}

	public static void main(String[] args) throws IOException {
		ReadCsv test = new ReadCsv();
		HttpServletResponse response = null;
		test.createCsvTest1(response);
	}
}
