package resouremanager.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;

public class RMUtil {
	/***
	 * 写入文件
	 * 
	 * @rm.param list
	 * @rm.param fileName
	 * @return
	 */
	public static boolean write2Txt(List<String> list, String fileName) {
		return write2Txt(list, new File(fileName));
	}

	public static boolean write2Txt(List<String> list, File file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String s : list) {
				bw.write(s + "\n");
			}
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
