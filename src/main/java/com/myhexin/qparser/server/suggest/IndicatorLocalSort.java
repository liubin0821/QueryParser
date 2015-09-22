package com.myhexin.qparser.server.suggest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.pattern.TransInfo;

public class IndicatorLocalSort {
	private static int MAX_INDEX_LEN = 50;
	private static String contextSkipTriger_ = "0123456789零一二三四五六七八九十壹贰叁肆伍陆柒捌玖";
	//pin yin
	private static String halfSkipToken_ = "年月日天周季个十百千万亿";
	public static String acceptWords = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int sugWordSearchBeginLen_;
	private int maxIndicateCount_;
	private IndicatorIndex myIndIndex_;

	private ArrayList<String> tokenList_ = null;
	private int nowTokenPos_;

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

	public IndicatorLocalSort() {
		sugWordSearchBeginLen_ = 5;
		maxIndicateCount_ = 15;
		myIndIndex_ = new IndicatorIndex();
		myIndIndex_.initSearch();
	}

	private int getAlphabetBegPos(String fullSentence) {
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

	public boolean seggerCandWords(String candidateStr) {
		try {
			nowTokenPos_ = 0;
			tokenList_ = new ArrayList<String>();
			String encoded = java.net.URLEncoder.encode(candidateStr, "utf-8");
			if (Param.IFIND_SUGGEST_SEG_URL == null) {
				Param.IFIND_SUGGEST_SEG_URL = "http://192.168.23.215:12350/ltp?app=2&s=";
			}
			String strUrl = Param.IFIND_SUGGEST_SEG_URL + encoded;
			URLConnection conn = new URL(strUrl).openConnection();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
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
	private int getJumpStep() {
		int res = 1;
		if (( null == tokenList_)||(nowTokenPos_ >= tokenList_.size()) || (nowTokenPos_ < 0)) {
			return res;
		} else {
			res = tokenList_.get(nowTokenPos_).length();
			nowTokenPos_++;
		}
		return res;
	}

	// get the position of last stop word
	private int getStopWordPos(String fullSentence) {
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

	private String mergeSentence(String rawSentence, String aimIndicate,
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

			for (int i = pureRawSentence.length()-1; i >= 0; i--) {
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

	private ArrayList<String> getIndicatorsArrayList(String query) {
		String queryRes = myIndIndex_.yyQuery(query);
		if ((null == queryRes) || queryRes.isEmpty()) {
			return null;
		}
		ArrayList<String> resList = new ArrayList<String>();
		String[] indicators = queryRes.split("\n");
		for (int i = 0; i < indicators.length; i++) {
			resList.add(indicators[i]);
		}
		return resList;
	}

	public int contextSkip(String fullSentence, String queryStr) {
		int res = 0;
		try {
			int testPos = fullSentence.length() - queryStr.length() - 1;
			if (testPos < 0) {
				return res;
			} else if ((contextSkipTriger_
					.indexOf(fullSentence.charAt(testPos)) >= 0)
					&& (halfSkipToken_
							.indexOf(fullSentence.charAt(testPos + 1)) >= 0)) {
				res = 1;
			}
			return res;
		} catch (Exception e) {
			return res;
		}
	}

	public ArrayList<String> indiSort(ArrayList<String> preArrayList, String queryStr) {
		if (preArrayList == null) {
			return null;
		}
		ArrayList<IndAndWeight> aIndexList = new ArrayList<IndAndWeight>();
		for (int i = 0; i < preArrayList.size(); i++) {
			aIndexList.add(new IndAndWeight(preArrayList.get(i), 0));
		}
		Iterator<IndAndWeight> indIterator = aIndexList.iterator();
		IndAndWeight nowIAW = null;
		int totalWeight = 0;

		int containWeight = 0;
		int startWeight = 0;
		int linkWeight = 0;
		int lengthWeight = 0;
		int orderWeight = 0;
		int containWeightMul = 1000;
		int startWeightMul = 1000;
		int linkWeightMul = 500;
		int lengthWeightMul = 300;
		int orderWeightMul = 100;

		int queryStrLength = queryStr.length();
		int indStrLength = 0;
		int preMatchPos = -2;
		while (indIterator.hasNext()) {
			//ini alll weight
			totalWeight = 0;
			startWeight = 0;
			linkWeight = 0;
			lengthWeight = 0;
			orderWeight = 0;
			containWeight = 0;
			
			nowIAW = (IndAndWeight) indIterator.next();
			if (nowIAW == null) {
				break;
			}
			// Now start the weight calculate
			String nowIndStr =  nowIAW.indStr_;
			indStrLength = nowIndStr.length();
			lengthWeight = MAX_INDEX_LEN - indStrLength;
			for( int i = 0; i < queryStrLength; i++ ){
				char tempChar = queryStr.charAt(i);
				if( nowIndStr.indexOf(tempChar) >= 0 ){
					containWeight++;
				}
			} 
			//if don't contain half query characters we don't match
			if( containWeight < queryStrLength  ){
				indIterator.remove();
				continue;
			}
			//begin to calculate all weights
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

			totalWeight = containWeight * containWeightMul + startWeight * startWeightMul + linkWeight
					* linkWeightMul + lengthWeight * lengthWeightMul
					+ orderWeight * orderWeightMul;
			
			//set the weight 
			nowIAW.indWeight_ = totalWeight;
		}

		Object[] comObjects = aIndexList.toArray();
		ArrayList<String> resArrayList = new ArrayList<String>();

		Arrays.sort(comObjects);
		for (int i = 0; i < comObjects.length; i++) {
			IndAndWeight temp = (IndAndWeight) comObjects[i];
			resArrayList.add(temp.indStr_);
		}
		return resArrayList;
	}

	private String spilitWithSpc( String rawStr ){
		if( (null == rawStr)||( rawStr.isEmpty()) ){ return "";}
		StringBuilder aSB = new StringBuilder();
	    for( int i = 0; i < rawStr.length(); i++ ){
			aSB.append(rawStr.charAt(i)).append(" ");
		}
	    String resStr = aSB.toString();
	    return resStr;
	}
	
	private String englishQuery(String rawSentence, String query) {
		StringBuilder resSB = new StringBuilder();
		ArrayList<String> indList = getIndicatorsArrayList(query);
		
		String oldQuery = query;
		System.out.println("Old query:" + query);
		if( (null == indList)||( indList.isEmpty()) ){
			query = spilitWithSpc(oldQuery);
			System.out.println("New query:"+query);
			indList = getIndicatorsArrayList(query);
		}
		
		if( null == indList ){ return "";}
		for (int i = 0; (i < indList.size())&&(i<this.maxIndicateCount_); i++) {
			resSB.append(mergeSentence(rawSentence, indList.get(i), oldQuery));
		}
		String resStr = resSB.toString();
		return resStr;
	}

	private String chineseQuery(String fullSentence) {
		String resStr = "";
		String queryString = "";
		if( (null == fullSentence)||(fullSentence.isEmpty()) ){
			return resStr;
		}
		// try to find some stop words
		int stopWordPos = getStopWordPos(fullSentence);
		// if the sentence end with stop words return direct
		if ((stopWordPos + 1) >= fullSentence.length()) {
			return "";
		}

		// test if the stop word pos is to long
		if (fullSentence.length() - stopWordPos > sugWordSearchBeginLen_) {
			// it is to long
			if (fullSentence.length() <= sugWordSearchBeginLen_) {
				queryString = fullSentence;
			} else {
				queryString = fullSentence.substring(fullSentence.length()
						- sugWordSearchBeginLen_);
			}
		} else {
			// it is in the test limitation
			queryString = fullSentence.substring(stopWordPos + 1);
		}

		// try to find some words that may can skip by context first
		int skipLen = contextSkip(fullSentence, queryString);
		// add back code here
		queryString = fullSentence.substring(fullSentence.length()
				- queryString.length() + skipLen);

		// now we entry the common area
		if (!seggerCandWords(queryString)) {
			return "";
		}
		
		boolean hasRes = false;
		while (hasRes == false) {
			ArrayList<String> indList = getIndicatorsArrayList(queryString);
			// if there are no response, we should jump a word
			if (null == indList) {
				if( queryString.length() > 1 ){
					queryString = queryString.substring(getJumpStep());
					continue;
				}else{
					break;
				}
			}
			ArrayList<String> resArrayList = indiSort(indList, queryString);
			resStr = "";
			if (resArrayList.size() > 0) {
				for (int i = 0; i < maxIndicateCount_
						&& i < resArrayList.size(); i++) {
					resStr += mergeSentence(fullSentence, resArrayList.get(i),
							queryString);
				}
				hasRes = true;
			} else {
				if (queryString.length() > 1) {
					queryString = queryString.substring(getJumpStep());
				} else {
					break;
				}
			}
		}
		return resStr;
	}

	public String getResStr(String fullSentence) {
		String resStr = "";
		try{
		    int alpbetPos = this.getAlphabetBegPos(fullSentence);
		    if ((alpbetPos >= 0) && (alpbetPos < fullSentence.length())) {
			    resStr = englishQuery(fullSentence,
					    fullSentence.substring(alpbetPos));
		    } else {
			    resStr = chineseQuery(fullSentence);
		    }
		    return resStr;
	    }catch( Exception e ){
		    return "";
	    }
	}
}
