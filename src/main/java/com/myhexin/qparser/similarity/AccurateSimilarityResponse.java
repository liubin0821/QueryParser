package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 相似问句精确匹配返回的结果，extends from {@link SimilarityResponse}
 * @author luwenxing
 */
public class AccurateSimilarityResponse extends SimilarityResponse {
	/** 返回的doc集 */
	private List<Document> docList ;
	
	public List<Document> docList(){
		if(docList == null) {
			docList = Collections.emptyList() ;
		}
		return this.docList ;
	}
	
	public boolean isEmpty(){
		return this.docList == null || this.docList.isEmpty() ;
	}
	
	/**
	 * 只有当返回的perfect = 1时，才能确定是精确匹配的结果
	 */
	public String reponseA(){
		if(this.isEmpty()) {
			return null ;
		}
		if(this.docList.get(0).score() < 1.0) {
			return null ;
		} 
		return this.docList.get(0).DEGroupID;
		
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 解析solr返回的json数据。
	 */
	public void parseJson(){
		List<HashMap<Object, Object>> jsonDocList = ((List<HashMap<Object, Object>>)this.response.get("docs")) ;
		docList = new ArrayList<Document>() ;
		if(jsonDocList == null || jsonDocList.isEmpty()) {
			return ;
		}
		for(Map<Object, Object> map : jsonDocList) {
			Document doc = new Document() ;
			doc.UID = (String) map.get("UID") ;
			doc.DEGroupID = (String) map.get("DEGroupID") ;
			doc.perfect = (String) map.get("perfect") ;
			docList.add(doc) ;
		}
	}
	
	/**
	 * json返回的doc对应的类
	 * @author Administrator
	 */
	public static class  Document {
		String UID ;
		String DEGroupID ;
		String perfect ;
		private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
	            .getLogger(Document.class.getName());
		
		public String toString(){
			return this.DEGroupID ;
		}
		
		public double score() {
			double score ;
			try {
				score = Double.parseDouble(this.perfect) ;
			} catch (NumberFormatException e) {
				logger_.warn(e.getMessage()) ;
				score = -1.0 ;
			}
			return score ;
		}
	}

}
