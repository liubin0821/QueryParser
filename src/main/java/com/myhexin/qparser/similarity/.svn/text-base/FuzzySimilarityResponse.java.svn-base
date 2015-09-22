package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.Param;
/**
 * 相似问句模糊匹配返回的结果，extends from {@link SimilarityResponse}
 * @author luwenxing
 */
public class FuzzySimilarityResponse extends SimilarityResponse{
	/** 返回的doc集 */
	private List<Document> docList ;
	/** 相似交互返回的结果个数，该值在配置文件中配置 */
	private int top_k = 5; //Param.SIMILITY_FUZYY_TOPK ;
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SimilarityResponse.class.getName());
	
	public List<Document> docList(){
		if(docList == null) {
			docList = Collections.emptyList() ;
		}
		return this.docList ;
	}
	
	public boolean isEmpty() {
		return this.docList == null || this.docList.isEmpty();
	}
	
	public String toString(){
		if(this.docList == null) { return "null" ;}
		StringBuilder builder = new StringBuilder("[") ;
		for(Document doc : this.docList) {
			if(builder.length() == 1) {
				builder.append(doc.DEGroupID).append(",").append(doc.score()) ;
			} else {
				builder.append("; " + doc.DEGroupID).append(",").append(doc.score()) ;
			}
		}
		return builder.append("]").toString() ;
	}
	
	/**
	 * 只有当返回的分值 = 1时，才能确定使用
	 */
	public String reponseA() {
		if(this.isEmpty())
			return null;
		if(this.docList.get(0).score() < 1.0) {
			return null ;
		}
		return this.docList.get(0).DEGroupID ;
	}
	
	/** 前K个A 
	 * @param topK
	 * @return A集合
	 */

	public List<Document> topA(int topK){
		List<Document> rltList = new ArrayList<Document>(topK) ;
		if(this.docList != null){
			for(Document doc : this.docList) {
				if(--topK < 0) {
					break ;
				}
				rltList.add(doc) ;
			}
		}
		return rltList ;
	}
	
	/** 大于等于value的A, value > 1时，会返回空集
	 * @param value
	 * @return A集合
	 */
	public List<Document> topA(double value){
		List<Document> rltList = new ArrayList<Document>() ;
		if(this.docList != null){
			for(Document doc : this.docList) {
				if(doc.score() < value) {
					break ;
				} else {
					rltList.add(doc) ;
				}
			}
		}
		return rltList ;
	}
	
	/** 暂时定为TOP_K */
	public List<Document> topA(){
		return this.topA(top_k) ;
	}
	
	@SuppressWarnings("unchecked")
	public void parseJson(){
		List<HashMap<Object, Object>> jsonDocList = ((List<HashMap<Object, Object>>)this.response.get("docs")) ;
		docList = new ArrayList<Document>() ;
		if(jsonDocList.isEmpty()) {
			return ;
		}
		for(Map<Object, Object> map : jsonDocList) {
			Document doc = new Document() ;
			doc.UID = (String) map.get("UID") ;
			doc.DEGroupID = (String) map.get("DEGroupID") ;
			doc.Nodes = (String) map.get("Nodes") ;
			doc.positionscore = (Map<Object, Object>)map.get("positionscore") ;
			//在这先不去重，生成候选集的时候再去重
			docList.add(doc) ;
		}
	}
	
    private boolean hasDocumentOfDEGroupID(Document doc) {
        if (docList == null)
            return false;
        for (Document curDoc : docList) {
            if (doc.DEGroupID.equals(curDoc.DEGroupID))
                return true;
        }
        return false;
    }

    public static class Document {
		String UID ;
		String DEGroupID ;
		String Nodes ;
		Map<Object, Object> positionscore ;
		
		private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
	            .getLogger(Document.class.getName());
		
		public String text(){
			return this.DEGroupID ;
		}
		
		public double score() {
			double score ;
			try {
				score = Double.parseDouble(this.positionscore.get("Nodes").toString()) ;
			} catch (Exception e){
				logger_.warn(e.getMessage()) ;
				score = -1.0 ;
			}
			return score ;
		}
		
		public String toString(){
			return this.UID ;
		}
		
		public String getUID(){
		    return this.UID;
		}
	}
}
