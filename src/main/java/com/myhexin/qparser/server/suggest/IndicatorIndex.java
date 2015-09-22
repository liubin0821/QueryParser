package com.myhexin.qparser.server.suggest;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.util.Util;

public class IndicatorIndex {
	private static String LETTERS = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(IndicatorIndex.class.getName());
	private String srcDoc_;
	private String indexDir_;
	private Analyzer analyzer_;
	private  IndexSearcher searcher_;

	public IndicatorIndex() {
		indexDir_ = Param.IFIND_SUGGEST_INDICATOR_INDEX_DIR;
		srcDoc_ = Param.IFIND_SUGGEST_INDICATOR_INDEX_DOC;
		analyzer_ = new StandardAnalyzer(Version.LUCENE_30);
	}
	public IndicatorIndex(String srcDoc, String indexDir) {
		srcDoc_ = srcDoc;
		indexDir_ = indexDir;
		analyzer_ = new StandardAnalyzer(Version.LUCENE_30);
	}

	//Never forget to call this method
	public boolean initSearch(){
		try{
			searcher_ = new IndexSearcher(FSDirectory.open(new File(indexDir_)));
			return true;
		}catch( Exception e ){
			logger_.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean onlyEnglish(String query) {
		boolean res = true;
		for (int i = 0; i < query.length(); i++) {
			if (IndicatorIndex.LETTERS.indexOf(query.charAt(i)) < 0) {
				res = false;
				break;
			}
		}
		return res;
	}

	private String splitQuery(String query) {
		if (onlyEnglish(query)) {
			return query;
		}
		StringBuilder aSB = new StringBuilder();
		for (int i = 0; i < query.length(); i++) {
			aSB.append(query.charAt(i)).append(" ");
		}
		return aSB.toString();
	}

	public String yyQuery( String rawQuery ){
		try {
			if ((null == rawQuery)||(rawQuery.isEmpty())) {	return "";}
			
			String queryStr = this.splitQuery(rawQuery);
			String[] fields = { "indicator_name" };
			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_30, fields, analyzer_);
			Query query = queryParser.parse(queryStr);
			
			//start to search
            TopDocs topDocs = searcher_.search(query, 1000);
            
            StringBuilder resSB = new StringBuilder();
            
            for( ScoreDoc scoreDoc : topDocs.scoreDocs ){
            	int docSN = scoreDoc.doc;
            	Document doc = searcher_.doc( docSN);
            	resSB.append(doc.get("indicator_display")).append("\n");
            }
            String resStr = resSB.toString();
            return resStr;
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error(e.getMessage());
			return "";
		}
	}
	public String yyTestQuery(String rawQuery) {
		try {
			if ((null == rawQuery)||(rawQuery.isEmpty())) {	return "";}
			String queryStr = this.splitQuery(rawQuery);
			String[] fields = { "indicator_name" };
			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_30, fields, analyzer_);
			Query query = queryParser.parse(queryStr);
			System.out.println("Query Format:" + query);
			
			//start to search
			long start = System.currentTimeMillis();
            TopDocs topDocs = searcher_.search(query, 1000);
            
            System.out.println("Total count: " + topDocs.totalHits +
            		"  Total Time: " + ( System.currentTimeMillis() - start ) + "ms");
            
            StringBuilder resSB = new StringBuilder();
            
            for( ScoreDoc scoreDoc : topDocs.scoreDocs ){
            	int docSN = scoreDoc.doc;
            	Document doc = searcher_.doc( docSN);
            	resSB.append(doc.get("indicator_display")).append("\n");
            }
            String resStr = resSB.toString();
            System.out.println(resStr);
            return resStr;
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error(e.getMessage());
			return "";
		}
	}

	@SuppressWarnings("deprecation")
	public boolean yyMakeIndIndex() {
		try {
			File indFilePath = new File( this.indexDir_ );
			if( !indFilePath.exists() ){
				indFilePath.mkdir();
			}
			FSDirectory directory = FSDirectory.open(new File(this.indexDir_));
			IndexWriter iw = new IndexWriter(directory, this.analyzer_, true,MaxFieldLength.LIMITED);
			Document nowDocument = null;
			Field nowField = null;
			OntoXMLFactory ontoXMLFactory = new OntoXMLFactory(this.srcDoc_);
			String xpath = "//doc";
			NodeList nodeList = (NodeList) ontoXMLFactory.exectXPathStr(xpath);
			int nodeListCount = nodeList.getLength();
			for (int i = 0; i < nodeListCount; i++) {
				nowDocument = new Document();

				Node nowDocNode = nodeList.item(i);

				NodeList nowDocChildList = nowDocNode.getChildNodes();
				Node nowChildNode = null;
				int nowDocChildListLength = nowDocChildList.getLength();
				for (int j = 0; j < nowDocChildListLength; j++) {
					nowChildNode = nowDocChildList.item(j);
					NamedNodeMap nowNodeAttribute = nowChildNode
							.getAttributes();
					if (null == nowNodeAttribute) {
						continue;
					}
					String childName = nowNodeAttribute.getNamedItem("name")
							.getTextContent();
					if (null == childName) {
						continue;
					}
					String childValue = nowChildNode.getTextContent();
					if (childName.equals("id")) {
						nowField = new Field("indicator_id", childValue,
								Store.YES, Index.NOT_ANALYZED);
						nowDocument.add(nowField);
					} else if (childName.equals("name")) {
						nowField = new Field("indicator_name", childValue,
								Store.YES, Index.ANALYZED);
						nowDocument.add(nowField);
					} else if (childName.equals("display")) {
						nowField = new Field("indicator_display", childValue,
								Store.YES, Index.NOT_ANALYZED);
						nowDocument.add(nowField);
					}
				}
				iw.addDocument(nowDocument);
			}
			iw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error(e.getMessage());
			return false;
		}
	}
	
	public static void main(String[] args){
		ApplicationContextHelper.loadApplicationContext();
		IndicatorIndex  aII = new IndicatorIndex();
//		aII.yyMakeIndIndex();
		aII.initSearch();
		aII.yyQuery("");
	}
}
