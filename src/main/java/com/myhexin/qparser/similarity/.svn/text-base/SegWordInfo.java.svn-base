package com.myhexin.qparser.similarity;

import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.util.Pair;

/**
 * 单个分词含有信息
 * @author luwenxing
 * @date	2013/06/18
 *
 */
public class SegWordInfo {
	/** 原始的分词信息 */
	String token ;
	
	/** 分词 */
	String word ;
	
	/** 信息 */
	String info ;
	
	/** 泛化后的npttern*/
	String npttern ;
	
	/** 该词具有的权值 */
	String weight ;
	
	 public static org.slf4j.Logger logger_ = 
		        org.slf4j.LoggerFactory.getLogger(SegWordInfo.class.getName());
	
	public void parse(){
		parseWordInfo() ;
	}
	
	private void parseWordInfo(){
		Pair<String, String> wordInfoPair  ;
		if(this.token.contains("/trans:")){
			try {
				wordInfoPair = createTransWordInfo(token) ;
			} catch (QPException e) {
				logger_.warn(e.getMessage()) ;
				return ;
			}
		} else {
			wordInfoPair = createWordInfo(token) ;
		}
		this.word = wordInfoPair.first ;
		this.info = wordInfoPair.second ;
	}
	
	/**
	 * 根据原始的分词结果获取词的word 和 info；在分词中是以'/'划分word和info
	 * @param token
	 * @return
	 */
	private Pair<String, String> createWordInfo(String token){
		int pos = token.lastIndexOf('/');
		if (pos == -1) {
			return  new Pair<String, String>(token, "");
		} else {
			String word = token.substring(0, pos);
			String info = (pos == token.length() - 1 ? "" : token
					.substring(pos + 1));
			return new Pair<String, String>(word, info) ;
		}
	}
	
	/** 
	 * 含有同义词的分词，根据"/trans:"来划分word和info
	 * @param token
	 * @return wordInfoPair
	 * @throws UnexpectedException
	 */
	private Pair<String, String> createTransWordInfo(String token) throws UnexpectedException{
		String[] tmpArr = token.split("/trans:") ;
		String word , info ;
		if(tmpArr.length ==2 ){
			word = tmpArr[0] ;
			info = tmpArr[1] ;
		} else {
			throw new UnexpectedException("token is not trans word") ;
		}
		return new Pair<String, String>(word, info) ;
	}
}
