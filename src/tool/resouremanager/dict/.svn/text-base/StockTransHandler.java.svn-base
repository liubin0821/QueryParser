package resouremanager.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resouremanager.dataIO.HttpDataGetterResult;
import resouremanager.dataIO.HttpPostDataGetter;
import resouremanager.pub.AbstractRMHandlerResult;
import resouremanager.util.PinyinToolkit;
import resouremanager.util.RMUtil;



/***
 * 将股票简拼、全拼、曾用名添加到stock_trans_dict </br>
 * 简拼和曾用名来自外部获取数据,http://172.20.0.52/thsft/newdagudong/stocksInfo.txt ,数据格式如下： </br>
 * 000001	平安银行	payh	A	1	S深发展A(20061009),深发展A(20070620),平安银行(20120802),平安银行股份有限公司(00000000) 
 * 一个简拼可能对应对个股名，我们只取第一个，不保证正确，正确性由尹越那边处理</br>
 * 全拼有pinyin4j包生成，只取第一个全拼，正确性由尹越那边处理 </br>
 * stock_trans_dict数据格式：</br>
 * #payh/*Cate=posttreat;Seg=;Value=trans:平安银行;$
 * 
 * @author lu
 *
 */

public class StockTransHandler extends DictHandler {

	/**
	 * @param args
	 */
	private final String url ;
	
	/** stocksInfo.txt */
	private List<String> stockIfoList = new ArrayList<String>(0) ;
	
	/** 拼音简称 */
	private HashMap<String, String> pyShortMap = new HashMap<String, String>() ;
	
	/** 全拼 */
	private HashMap<String, String> pinyinMap = new HashMap<String, String>() ;
	
	/** 曾用名 */
	private HashMap<String, String> usedNameMap = new HashMap<String, String>() ;
	
	public StockTransHandler(String url) {
		this.url = url ;
	}
	
	public Result handle() {
		// TODO Auto-generated method stub
		HttpDataGetterResult remoteData = getData() ;
		stockIfoList = remoteData.data() ;
		extractPinyinAandUsedName() ;
		
		List<String> resList = new ArrayList<String>(pyShortMap.size() + pinyinMap.size() + usedNameMap.size()) ;
		
		// 简拼
		for(Map.Entry<String,String> en : pyShortMap.entrySet()){
			resList.add(this.toModel(en.getKey(), en.getValue())) ;
		}
		
		// 全拼
		for(Map.Entry<String,String> en : pinyinMap.entrySet()){
			resList.add(this.toModel(en.getKey(), en.getValue())) ;
		}
		
		// 曾用名
		for(Map.Entry<String,String> en : usedNameMap.entrySet()){
			resList.add(this.toModel(en.getKey(), en.getValue())) ;
		}
	
		return new Result(resList) ;
	}
	
	/***
	 * 从远程获取stockInfo
	 * @return
	 */
	private HttpDataGetterResult getData(){
		HttpPostDataGetter getter = new HttpPostDataGetter(this.url) ;
		return getter.getData() ;
	}
	
	/***
	 * 提取拼音和曾用名如从</br>
	 * 000001	平安银行	payh	A	1	S深发展A(20061009),深发展A(20070620),平安银行(20120802),平安银行股份有限公司(00000000)</br>
	 * 提取 平安银行	payh	S深发展A(20061009),深发展A(20070620),平安银行(20120802),平安银行股份有限公司(00000000)</br>
	 * 调用PinyinToolkit获取全拼
	 * 结果存在三个Map中：pyShortMap, pinyinMap, usedNameMap
	 */
	private void extractPinyinAandUsedName(){
		if(stockIfoList.isEmpty())
			return ;
		
		for(String line : stockIfoList){
			String[] ss = line.split("\t") ;
			String stockName = ss[1] ; 
			String jianpin = ss[2].toLowerCase() ;
			String usedName = ss[5].replaceAll("\\(\\d*\\)", "") ;
			
			// 简拼
			if(!pyShortMap.containsKey(jianpin))
				pyShortMap.put(jianpin, stockName) ;
			
			// 曾用名
			for(String sigalName : usedName.split(",")){
				if(!usedNameMap.containsKey(sigalName))
					usedNameMap.put(sigalName, stockName) ;				
			}
			
			// 全拼
			String quanpin = PinyinToolkit.cn2Spell(stockName) ;    //调用PinyinToolkit获取全拼
			if(!pinyinMap.containsKey(quanpin)){
				pinyinMap.put(quanpin, stockName) ;
			}			
		}
	}
	private String toModel(String from, String to){
		return "#" + from + "/*Cate=posttreat;Seg=;Value=trans:" + to + ";$" ;
	}
	
	public static class Result extends AbstractRMHandlerResult{
		
		public List<String> resultList = new ArrayList<String>(0) ;
		
		public Result(){}
		
		public Result(List<String> list){
			this.resultList = list ;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder() ;
			for(String s : resultList){
				sb.append(s + "\n") ;
			}
			sb.deleteCharAt(sb.length()-1) ;
			return sb.toString() ;
		}
		
		public void write2Txt(String fileName){
			RMUtil.write2Txt(this.resultList, fileName) ;
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StockTransHandler handler = new StockTransHandler("http://172.20.0.52/thsft/newdagudong/stocksInfo.txt") ;
		Result res = handler.handle() ;
		System.out.println(res.toString());
		res.write2Txt("stock_trans.dict") ;
	}

}
