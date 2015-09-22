package bench;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

import conf.tool.ToolUtil;

/***
 *在bench中用到的静态功能函数 
 * @author Administrator
 *
 */

public class BenchUtil {

	/**
	 * @param args
	 * @throws DataConfException 
	 */
	
	/***
	 * 下，以时间的方式命名
	 * @param ptnSourceFile
	 * @throws DataConfException
	 */
	public static void saveLastVersionPattern(String ptnSourceFile) throws DataConfException {
		
		// TODO Auto-generated method stub
		String pre = new SimpleDateFormat("yyyyMMdd-HH-mm").format(new Date());
		String outFile = "./bench-pattern/query2pattern.bak/" + pre + "_query_patern.txt" ;
		List<String> fileList = Util.readTxtFile(ptnSourceFile) ;
		Set<String> outSet = new HashSet<String>(10000) ;
		int begin_ptn = "queryPattern\":\"".length() ; 
		int begin_query = "text\":\"".length() ;
		for(String s : fileList){
			String query = s.substring(s.indexOf("text")+ begin_query, s.indexOf("\",\"treeCode")) ;
			String ptn = s.substring(s.indexOf("queryPattern")+ begin_ptn, s.indexOf("\",\"synonyms")) ;
			outSet.add(query + "\t\t" + ptn) ;
		}
		List<String> outList = new ArrayList<String>() ;
		outList.add("ptnSourceFile: " + ptnSourceFile.substring(ptnSourceFile.lastIndexOf("/")+1)) ;
		outList.addAll(outSet) ;
		ToolUtil.write2Txt(outList, outFile) ;			
	}
	
	/***
	 * 保留treecode 和 ptn
	 * @param ptnSourceFile
	 * @throws DataConfException
	 */
	public static void saveTreeCodeAndPtn(String ptnSourceFile) throws DataConfException {
		
		// TODO Auto-generated method stub
		String pre = new SimpleDateFormat("yyyyMMdd-HH-mm").format(new Date());
		String outFile = "./bench-pattern/query2pattern.bak/" + pre + "_treecode_patern.txt" ;
		List<String> fileList = Util.readTxtFile(ptnSourceFile) ;
		Set<String> outSet = new HashSet<String>(10000) ;
		int begin_ptn = "queryPattern\":\"".length() ; 
//		int begin_query = "text\":\"".length() ;
		int begin_treecode = "treeCode\":".length() ;
		for(String s : fileList){
			String query = s.substring(s.indexOf("treeCode")+ begin_treecode, s.indexOf(",\"tree\":")) ;
			String ptn = s.substring(s.indexOf("queryPattern")+ begin_ptn, s.indexOf("\",\"synonyms")) ;
			outSet.add(query + "\t" + ptn) ; 
		}
		List<String> outList = new ArrayList<String>() ;
		outList.add("ptnSourceFile: " + ptnSourceFile.substring(ptnSourceFile.lastIndexOf("/")+1)) ;
		outList.addAll(outSet) ;
		ToolUtil.write2Txt(outList, outFile) ;
	}
	
	/***
	 * 对比两个query-pattern文件的结果，将相异的结果输出到默认目录./bench-pattern/query2pattern.bak/
	 * 下，文件以时间的方式命名
	 * @param beforeFile
	 * @param nowFile
	 * @throws DataConfException
	 */
	public static void diff(String beforeFile, String nowFile) throws DataConfException{
		List<String> beforeList = Util.readTxtFile(beforeFile) ;
		List<String> nowList = Util.readTxtFile(nowFile) ;
		
		String beforeFileName = beforeFile.substring(beforeFile.lastIndexOf("/")+1) ;
		String nowFileName = nowFile.substring(nowFile.lastIndexOf("/")+1) ;
		
		String pre = new SimpleDateFormat("yyyyMMdd-HH-mm").format(new Date());
		String outFile = "./bench-pattern/query2pattern.bak/" + pre + "diff.txt" ;
		
		
		HashMap<String, String> beforeMap = new HashMap<String, String>() ;
		HashMap<String, String> nowMap = new HashMap<String, String>() ;
		HashSet<String> allQuery = new HashSet<String>() ;
		List<String> outList = new ArrayList<String>() ;
		List<String> outList_sigal = new ArrayList<String>() ;
		
		
		for(String s : beforeList){
			if(s.startsWith("ptnSourceFile"))
				continue ;
			String[] ss = s.split("\t\t") ;
			beforeMap.put(ss[0], ss[1]) ;
			allQuery.add(ss[0]) ;
		}
		
		for(String s : nowList){
			if(s.startsWith("ptnSourceFile"))
				continue ;
			String[] ss = s.split("\t\t") ;
			nowMap.put(ss[0], ss[1]) ;
			allQuery.add(ss[0]) ;
		}
		
		outList.add("beforeFile: " + beforeFileName + "    total: " + beforeMap.size()) ;
		outList.add("nowFile: " + nowFileName + "    total: " + nowMap.size()) ;
		outList.add("") ;
		
		int iSame = 0 ;
		int iSigal = 0 ;
		for (String query : allQuery) {
			if (nowMap.containsKey(query) && beforeMap.containsKey(query)
					&& nowMap.get(query).equals(beforeMap.get(query)))
				continue;
			else if(nowMap.containsKey(query) && beforeMap.containsKey(query)){
				outList.add("query: " + query) ;
				outList.add(beforeFileName +": " + beforeMap.get(query)) ;
				outList.add(nowFileName +": " + nowMap.get(query)) ;
				outList.add("") ;
				iSame ++ ;
			}
			else 
			{
				outList_sigal.add("query: " + query) ;
				outList_sigal.add(beforeFileName +": " + beforeMap.get(query)) ;
				outList_sigal.add(nowFileName +": " + nowMap.get(query)) ;
				outList_sigal.add("") ;
				iSigal ++ ;
			}
		}
		outList.addAll(outList_sigal) ;
		outList.add("") ;
		
		outList.add("diff with same query: " + iSame) ;
		outList.add("diff the increment query: " + iSigal) ;
		outList.add("diff total: " + (iSame + iSigal)) ;
		ToolUtil.write2Txt(outList, outFile) ;		
	}
	
	public static void main(String[] args) throws DataConfException {
		// TODO Auto-generated method stub
		long begintime = System.currentTimeMillis();
//		saveLastVersionPattern("./bench-pattern/query2pattern.bak/20120924_all_correct-quries.txt.0924run.raw_ptn.txt");
		saveTreeCodeAndPtn("./bench-pattern/query2pattern.bak/3000query.txt.raw") ;
//		diff("./bench-pattern/query2pattern.bak/20120920-14-38_query_patern.txt",
//				"./bench-pattern/query2pattern.bak/20120920-15-05_query_patern.txt");
		
//		diff("./bench-pattern/query2pattern.bak/20120920-15-05_query_patern.txt",
//		"./bench-pattern/query2pattern.bak/20120924-17-00_query_patern.txt");
		
//		diff("./bench-pattern/query2pattern.bak/20121015-15-28_query_patern.txt",
//		"./bench-pattern/query2pattern.bak/20121016_all_query_pattern.txt");

		long endtime = System.currentTimeMillis();
		System.out.println("耗时" + (endtime - begintime) / 1000.0 + "秒...");
	}
}
