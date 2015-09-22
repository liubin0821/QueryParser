package dict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Util;

public class WordFreqCount {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DataConfException 
	 * @throws TBException 
	 */	
	
	public static void main(String[] args) throws IOException, DataConfException {
		// TODO Auto-generated method stub
		
//		test("每股净资产20元以上的股票有哪些") ;
//		countAll("分词问句.txt" , "未去重统计结果.txt") ;
//		countDistinct("分词问句.txt" , "已去重统计结果.txt") ;		
		
//		getIndexFrequency(".//stockpick_query_notStockBegin.txt") ;
		
		
		getIndexFrequencyForZhiwei("E://sprint//sprint23//stockpick_rawquery_20120814_20120820.txt" ,
				"E://sprint//sprint23//seg_stockpick_rawquery_20120814_20120820.txt") ;
		
		System.exit(0) ;
		String inFileName = "分词问句.txt" ;
		String outFileName1 = "未去重统计结果.txt" ;
		String outFileName2 = "已去重统计结果.txt" ;
		
		ArrayList<String>  outList1 = new ArrayList<String>() ;
		ArrayList<String>  outList2 = new ArrayList<String>() ;
		
		ArrayList<String> senList = Util.readTxtFile(inFileName);
		
		//统计未去重指标
		{
//			HashMap<String, Integer> map1 = new HashMap<String, Integer>() ;
			Collection<String> c = new LinkedList<String>() ;
			for(String sen : senList)
			{
				if(sen.contains("\t") == false)
					continue ;
				else
				{
					String sIndex = sen.substring(sen.indexOf("\t")) ;
					c.add(sIndex) ;
				}
			}
			for(Map.Entry<String, Integer> en : count(c))
			{
				outList1.add(en.getKey() + ": " + en.getValue()) ;
			}
			WordFreqCount.writeToTxt(outFileName1, outList1) ;
		}
		
		//统计去重后的结果
		{
//			HashMap<String, Integer> map2 = new HashMap<String, Integer>() ;
			Collection<String> c = new TreeSet<String>() ;
			for(String sen : senList)
			{
				if(sen.contains("\t") == false)
					continue ;
				else
				{
					String sIndex = sen.substring(sen.indexOf("\t")) ;
					c.add(sIndex) ;
				}
			}
			for(Map.Entry<String, Integer> en : count(c))
			{
				outList2.add(en.getKey() + ": " + en.getValue()) ;
			}
			WordFreqCount.writeToTxt(outFileName2, outList2) ;
		}



	}		
	
	/***
	 * 统计频率
	 * @param c
	 * @return
	 */
	private static List<Map.Entry<String, Integer>> count(Collection<String> c)
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>() ;
		for(String s : c)
		{
			for(String sub : s.trim().split(" "))
			{
				if(map.containsKey(sub))
					map.put(sub, map.get(sub)+1) ;
				else map.put(sub, 1) ;
			}
		}
		
		Set<Map.Entry<String, Integer>> set = map.entrySet() ;
		ArrayList<Map.Entry<String, Integer>> rankList = 
			new ArrayList<Map.Entry<String, Integer>>(set.size()) ;
		for(Map.Entry<String, Integer> en : set)
		{						
			if(rankList.isEmpty())
			{
				rankList.add(en) ;
			}
			else
			{	
				int begin = 0;
				int end = rankList.size();
				int pos = end / 2; 
				
				while(true)
				{
					if(en.getValue() <= rankList.get(pos).getValue())
					{
						if(pos == rankList.size()-1 
								|| en.getValue() > rankList.get(pos + 1).getValue())
						{
							rankList.add(pos + 1, en) ;
							break ;
						}
						else
						{
							begin = pos ;
							pos = (end - begin)/2 + begin ;
						}
					}
					else if(en.getValue() > rankList.get(pos).getValue())
					{
						if(pos == 0
								|| en.getValue() <= rankList.get(pos-1).getValue())
						{
							rankList.add(pos, en) ;
							break ;
						}
						else
						{
							end = pos ;
							pos = (end - begin)/2 + begin ;
						}
					}
				}
			}										
		}		
		return rankList ;
	}
	
	private static void countDistinct(String inFileName, String outFileName)
		throws IOException, DataConfException
	{
		ArrayList<String> list = Util.readTxtFile(inFileName) ;
		Set<String> ts = new TreeSet<String>() ;
		System.out.println(list.size());
		ts.addAll(list) ;
		System.out.println(ts.size());
		ArrayList<String> outList = new ArrayList<String>() ;
		for(Map.Entry<String, Integer> en : count(ts))
		{
			outList.add(en.getKey() + ": " + en.getValue()) ;
		}
		WordFreqCount.writeToTxt(outFileName, outList) ;
	}
	
	/***
	 * 统计所有指标出现的频率
	 * @throws DataConfException 
	 */
	private static void countAll(String inFileName, String outFileName) throws IOException, DataConfException
	{
		ArrayList<String> list = Util.readTxtFile(inFileName) ;						
		ArrayList<String> outList = new ArrayList<String>() ;
		for(Map.Entry<String, Integer> en : count(list))
		{
			outList.add(en.getKey() + ": " + en.getValue()) ;
		}
		WordFreqCount.writeToTxt(outFileName, outList) ;
	}
	
	private static void getIndexFrequency(String fileName) throws DataConfException
	{
//		String fileName = ".//stockpick_query_notStockBegin.txt" ;
		ArrayList<String> list = Util.readTxtFile(fileName) ;
		ArrayList<String> segList = new ArrayList<String>(list.size()) ;
		
		//获取要分词的问句
		for(int index = 0 ; index < list.size() ; ++index)
		{
			String s = list.get(index) ;						
			s = s.substring(s.lastIndexOf("\t")).trim() ;
			list.set(index, s) ;
			System.out.println(index + ":  " + s);
		}
		
//		Set<String> ts = new TreeSet<String>() ;
//		ts.addAll(list) ;
		

		//分词
//		LinkedList<Query> queryList = new LinkedList<Query>() ;
		QueryParser qp = QueryParser.getParser("./conf/qparser.conf") ;
		for(int index = 0 ; index < list.size() ; ++index)
		{
			String s = list.get(index) ;
			
			try
			{
				Query query = new Query(s) ;
				System.out.println("正在处理第" + index + "条: " + query.text);

				query.setType(Query.Type.STOCK) ;
				qp.parse(query) ;
				StringBuilder sb = new StringBuilder(s + "\t") ;
				for(SemanticNode sn : query.getNodes())
				{
//					System.out.println(sn);
//					System.out.println("__NodeType:" + sn.type + "  NodeText:" + sn.text);
					if(sn.type == NodeType.CLASS)
						sb.append(sn.text + " ") ;
				}	
				segList.add(sb.toString().trim());
			}catch(Exception e )
			{
				e.printStackTrace() ;
			}
			
		}
		
		String sOutput = "分词问句.txt" ;
		try {
			writeToTxt(sOutput , segList) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void getIndexFrequencyForZhiwei(String fileName , String outputFile) throws DataConfException{

		ArrayList<String> fileList = Util.readTxtFile(fileName) ;
		ArrayList<String> segList = new ArrayList<String>(fileList.size()) ;
		ArrayList<String> list = new ArrayList<String>(fileList.size()) ;
		
		//获取要分词的问句
		for(int index = 0 ; index < fileList.size() ; ++index)
		{
			String s = fileList.get(index) ;		
			if(s.contains("\t")) {
				s = s.substring(0, s.indexOf("\t")).trim() ;
			}
			else {
				s = "" ;
			}
			list.add(s) ;

			System.out.println(index + ":  " + s);
		}
		
//		Set<String> ts = new TreeSet<String>() ;
//		ts.addAll(list) ;
		

		//分词
//		LinkedList<Query> queryList = new LinkedList<Query>() ;
		QueryParser qp = QueryParser.getParser("./conf/qparser.conf") ;
		for(int index = 0 ; index < list.size() ; ++index)
		{
			String s = list.get(index) ;
			
			try
			{
				Query query = new Query(s) ;
				System.out.println("正在处理第" + index + "条: " + query.text);

				query.setType(Query.Type.STOCK) ;
				qp.parse(query) ;
				StringBuilder sb = new StringBuilder(index + ":") ;
				ArrayList<SemanticNode> nodes = query.getNodes();
				for(SemanticNode sn : nodes)
				{
//					System.out.println(sn);
//					System.out.println("__NodeType:" + sn.type + "  NodeText:" + sn.text);
					if(sn.type == NodeType.CLASS)
						sb.append(sn.text + " ") ;
				}	
				segList.add(sb.toString().trim());
			}catch(Exception e )
			{
				e.printStackTrace() ;
			}
			
		}
		
		ArrayList<String> rtn = new ArrayList<String>(fileList.size()) ;
		for(int i=0,j=0 ; i < fileList.size() && j < segList.size() ; ){
			String seg = segList.get(j) ;
			int key = Integer.parseInt(seg.substring(0, seg.indexOf(":"))) ;
			String value =  seg.substring(seg.indexOf(":")+1) ;
			
			if(i == key){
				rtn.add(fileList.get(i)+"\t"+value) ;
				++i; ++j ;
			}
			else {
				rtn.add(fileList.get(i)) ;
				++i ;
			}

		}
		
//		String sOutput = "分词问句.txt" ;
		try {
			writeToTxt(outputFile , rtn) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	/**
	 * 写入txt文件
	 * @param sDir 写入文件路径
	 * @param list 需写入文件的内容
	 * @throws IOException 
	 */
	private static void writeToTxt(String sDir , List<String> list)throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(sDir)));
		for(String s : list)
		{
			bw.write(s) ;
			bw.newLine() ;
		}
		bw.close() ;
	}
}
