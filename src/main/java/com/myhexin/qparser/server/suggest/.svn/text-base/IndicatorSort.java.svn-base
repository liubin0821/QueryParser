package com.myhexin.qparser.server.suggest;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.pattern.TransInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

public class IndicatorSort {
	public static int MAX_INDEX_LEN = 50;
    public static String contextSkipTriger_ = "0123456789零一二三四五六七八九十壹贰叁肆伍陆柒捌玖";
    public static String halfSkipToken_ = "年月日天周季个十百千万亿";
    
	public ArrayList<String> tokenList_ = null;
	public int nowTokenPos_;

	public boolean seggerCandWords(String candidateStr) {
		try {
			nowTokenPos_ = 0;
			tokenList_ = new ArrayList<String>();
			String encoded = java.net.URLEncoder.encode(candidateStr, "utf-8");
			if( Param.IFIND_SUGGEST_SEG_URL == null ){
				Param.IFIND_SUGGEST_SEG_URL = "http://192.168.23.215:12350/ltp?app=2&s=";
			}
			String strUrl = Param.IFIND_SUGGEST_SEG_URL + encoded;
			URLConnection conn = new URL(strUrl).openConnection();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = reader.readLine();
			for (String token : line.split("\t")) {
				int pos = token.lastIndexOf('/');
				String word = token.substring(0, pos);
				tokenList_.add(word);
			}
			reader.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// call segger service at least 1
	public int getJumpStep() {
        int res = 1;
        
        if( (nowTokenPos_ > tokenList_.size())||(nowTokenPos_<0) ){
        	return res;
        }else{
        	res = tokenList_.get(nowTokenPos_).length();
        	nowTokenPos_++;
        }
        return res;
	}

	public int contextSkip( String fullSentence, String queryStr ){
		int res = 0;
		try{
			int testPos = fullSentence.length() - queryStr.length() - 1;
			if( testPos < 0 ){ 
				return res;
			}else if( (contextSkipTriger_.indexOf(fullSentence.charAt(testPos)) >= 0) && 
					(halfSkipToken_.indexOf(fullSentence.charAt(testPos+1)) >= 0) ){
		    	res = 1;
		    }
		    return res;
	    }catch( Exception e ){
			return res;
		}
	}
	
	public int getAlphabetBegPos(String fullSentence) {
		String acceptWords = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if ((fullSentence == null) || (fullSentence.isEmpty())) {
			return -2;
		}
		int pos = fullSentence.length();
		for (int i = (fullSentence.length() - 1); i >= 0; i--) {
			if ((acceptWords.indexOf(fullSentence.charAt(i)) >= 0)) {
				pos = i;
			} else {
				break;
			}
		}
		return pos;
	}

	// get the position of last stop word
	public int getStopWordPos(String fullSentence) {
		if ((fullSentence == null) || fullSentence.isEmpty()) {
			return -2;
		}
		if (TransInfo.IFIND_SUGGEST_STOP_CHARS == null) {
			return -1;
		}
		for (int i = (fullSentence.length() - 1); i >= 0; i--) {
			if (TransInfo.IFIND_SUGGEST_STOP_CHARS.indexOf(fullSentence.charAt(i)) >= 0) {
				return i;
			}
		}
		// means that we didn't find any sotp words
		return -1;
	}

	public Document formDocument(String query) {
		try {
			if ((query == null) || (query.isEmpty())) {
				return null;
			}
			if (Param.IFIND_SUGGEST_URL == null) {
				Param.IFIND_SUGGEST_URL = "http://192.168.0.46:12347/solr/select/?version=2.2&start=0&rows=100&indent=on&fl=display&q=";
			}
			String aimURL = Param.IFIND_SUGGEST_URL
					+ URLEncoder.encode(query, "utf-8");
			if (Param.DEBUG_SUGGEST) {
				System.out.println(aimURL);
			}
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document resDocument = dBuilder.parse(aimURL);
			return resDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String arrayListToWebStr(ArrayList<String> preAS) {
		try {
			if (preAS == null) {
				return null;
			}
			String resStr = "";
			for (int i = 0; i < preAS.size(); i++) {
				resStr = resStr + preAS.get(i) + "<br>";
				if (i > 14) {
					break;
				}
			}
			return resStr;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String mergeSentence(String rawSentence, String aimIndicate,
			String queryString) {
		try {
			if ((rawSentence == null) || rawSentence.isEmpty()) {
				return "";
			}
			if ((aimIndicate == null) || aimIndicate.isEmpty()) {
				return rawSentence;
			}
			if ((queryString == null) || queryString.isEmpty()) {
				return rawSentence;
			}
			if (!rawSentence.endsWith(queryString)) {
				return rawSentence;
			}

			String resStr = "";
			String pureRawSentence = rawSentence.substring(0,
					rawSentence.length() - queryString.length());
			int transLength = 0;
			
			for (int i = pureRawSentence.length() - 1; i >= 0; i--) {
				char nowChar = pureRawSentence.charAt(i);
				if (aimIndicate.indexOf(nowChar) >= 0) {
					transLength++;
				}else{
					break;
				}
			} 
			
			int startTransPos = pureRawSentence.length() - transLength;
			resStr = pureRawSentence.substring(0, startTransPos) + "\t\t"
					+ aimIndicate + "\n";
			return resStr;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public ArrayList<String> indiSort(Document rawDoc, boolean queryAreLetters ) {
		if (rawDoc == null) {
			return null;
		}
		ArrayList<IndAndWeight> aIndexList = new ArrayList<IndAndWeight>();
		String queryStr = "";

		NodeList aNodeList = rawDoc.getElementsByTagName("str");
		Node nowNode = null;
		String nowName = "";
		String nowStr = "";
		for (int i = 0; i < aNodeList.getLength(); i++) {
			nowNode = aNodeList.item(i);
			if (nowNode == null) {
				continue;
			}
			nowStr = nowNode.getTextContent();
			nowName = nowNode.getAttributes().getNamedItem("name")
					.getTextContent();
			if (nowName.equals("q")) {
				queryStr = nowStr;
			} else if (nowName.equals("display")) {
				aIndexList.add(new IndAndWeight(nowStr, 0));
			}
		}
		//if query string contain letters, we don't do any sort
		if( queryAreLetters ){
			ArrayList<String> resArrayList = new ArrayList<String>();
			for( int i = 0; i < aIndexList.size(); i++ ){
				resArrayList.add(aIndexList.get(i).indStr_);
			}
			return resArrayList;
		}
		Iterator<IndAndWeight> indIterator = aIndexList.iterator();
		IndAndWeight nowIAW = null;
		int totalWeight = 0;

		int startWeight = 0;
		int linkWeight = 0;
		int lengthWeight = 0;
		int orderWeight = 0;
		int startWeightMul = 150;
		int linkWeightMul = 50;
		int lengthWeightMul = 30;
		int orderWeightMul = 5;

		int queryStrLength = queryStr.length();
		int indStrLength = 0;
		int nowQueryStrPos = 0;
		int nowIndStrPos = 0;
		int preMatchPos = -2;
		while (indIterator.hasNext()) {
			nowIAW = (IndAndWeight) indIterator.next();
			if (nowIAW == null) {
				break;
			}
			// Now start the weight calculate
			indStrLength = nowIAW.indStr_.length();
			lengthWeight = MAX_INDEX_LEN - indStrLength;

			int j = 0;
			char nowQueryChar = queryStr.charAt(j);
			for (int i = 0; i < indStrLength; i++) {
				char nowIndChar = nowIAW.indStr_.charAt(i);
				if (nowIndChar == nowQueryChar) {
					if (0 == i) {
						startWeight++;
					}
					if ((i - preMatchPos) == 1) {
						linkWeight++;
					}
					preMatchPos = i;
					orderWeight++;
					j++;
					if (j == queryStrLength) {
						j = 0;
					} else {
						nowQueryChar = queryStr.charAt(j);
					}
				}
			}

			totalWeight = startWeight * startWeightMul + linkWeight
					* linkWeightMul + lengthWeight * lengthWeightMul
					+ orderWeight * orderWeightMul;
			nowIAW.indWeight_ = totalWeight;
			totalWeight = 0;
			startWeight = 0;
			linkWeight = 0;
			lengthWeight = 0;
			orderWeight = 0;
			nowQueryStrPos = 0;
			nowIndStrPos = 0;
		}

		Object[] comObjects = aIndexList.toArray();
		ArrayList<String> resArrayList = new ArrayList<String>();

		Arrays.sort(comObjects);
		
		HashMap<String, Integer> tempHashMap  = new HashMap<String, Integer>();
		
		String nowResStr = null;
		for (int i = 0; i < comObjects.length; i++) {
			IndAndWeight temp = (IndAndWeight) comObjects[i];
			nowResStr = temp.indStr_;
			
			if( nowResStr != null && !tempHashMap.containsKey(nowResStr) ){
				resArrayList.add(temp.indStr_);
				tempHashMap.put(nowResStr, 0);
			}
			// logger_.info(temp.indStr_ + ":" + temp.indWeight_);
		}
		return resArrayList;
		// return rawDoc;
	}

	public String docToStr(Document souDoc) {
		try {
			DOMSource domSource = new DOMSource(souDoc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String arrayListToStr(ArrayList<String> preAS) {
		try {
			if (preAS == null) {
				return null;
			}
			String resStr = "";
			for (int i = 0; i < preAS.size(); i++) {
				resStr = resStr + preAS.get(i) + "\n";
			}
			return resStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private class IndAndWeight implements Comparable<IndAndWeight> {
		public IndAndWeight(String indStr, int indWeight) {
			this.indStr_ = indStr;
			this.indWeight_ = indWeight;
		}

		public int compareTo(IndAndWeight o) {
			if (this.indWeight_ > o.indWeight_) {
				return -1;
			} else {
				return 1;
			}
		}

		public String indStr_;
		public int indWeight_;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		IndicatorSort aIS = new IndicatorSort();
		aIS.getStopWordPos("2011年配股预案价上限大于");
//        System.out.println(aIS.seggerCandWords("年相对发行价涨跌幅"));
		// System.out.println(aIS.mergeSentence("那只股票主营业务", "单季度主营业务利润",
		// "营业务"));
		// System.out.println(aIS.getAlphabetBegPos("123我s d"));
		// while(true){
		// System.out.println("Please input key words.");
		// Scanner aScanner = new Scanner(System.in);
		// String inputStr = aScanner.nextLine();
		// if( inputStr.equals("esc") ){
		// break;
		// }
		// Document tempDocument = aIS.formDocument( inputStr );
		// if( tempDocument == null ){
		// System.out.println("Forming document is null.");
		// continue;
		// }
		// ArrayList<String> sortArrayList = aIS.indiSort( tempDocument );
		// if( sortArrayList == null ){
		// System.out.println("Sorting document is null.");
		// continue;
		// }
		// String resStr = aIS.arrayListToStr( sortArrayList );
		// if( resStr == null ){
		// System.out.println("Result string is null.");
		// continue;
		// }else{
		// System.out.println( resStr );
		// }
		// String resStr = aIS.arrayListToWebStr( sortArrayList );
		// if( resStr == null ){
		// System.out.println("Result string is null.");
		// continue;
		// }else{
		// System.out.println( resStr );
		// }
		// }
	}
}
