/**
 * 
 */
package com.myhexin.qparser.phrase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;


/**
 * @author chenhao
 *
 */
public class TestPatternMatch {
	public static void main(String[] args) throws SQLException {
		String queryFilePath = "D:\\query.txt";
		String outputFilePath = "D:\\result.txt";
		String outputFilePath1 = "D:\\result1.txt";
		//		if (args.length > 0) {
		//			queryFilePath = args[0];
		//			outputFilePath = args[1];
		//		} else {
		//			System.exit(0);
		//		}

		ApplicationContextHelper.getApplicationContext();
		PhraseParser phraseParser = ApplicationContextHelper.getBean("phraseParser");
		PhraseParser phraseParserTest = ApplicationContextHelper.getBean("phraseParserUnitTest");
		ParseResult parserResult = null;
		ParseResult parserResultTest = null;
		String result = "";
		String resultTest = "";

		//Sampling sampling = new Sampling();

		//sampling.initProp();
		//List<String[]> list = sampling.getQueries(queryFilePath);
		ResultSet resultSet = null;
		try {
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://192.168.23.52:3306/configFile?zeroDateTimeBehavior=convertToNull", "qnateam",
					"qnateam");
			resultSet = connection.prepareStatement("SELECT ID,query FROM configFile.sampling_queries LIMIT 1019,2000")
					.executeQuery();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<String[]> list = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		String queryStr = "";
		long total = 0;
		long end = 0;
		long totalTest = 0;
		long endTest = 0;
		int score = 0;
		int scoreTest = 0;
		int scoreTotal = 0;
		int scoreTestTotal = 0;
		int countBetter = 0;
		int countWorse = 0;
		int oneHundred = 0;
		int oneHundredTest = 0;
		Query query = null;
		Query queryTest = null;
		ParserAnnotation annotation = null;
		ParserAnnotation annotationTest = null;
		int i = 0;
		for (; resultSet.next(); i++) {
			queryStr = resultSet.getString(2);
			query = new Query(queryStr);
			query.setType(Type.ALL);
			queryTest = new Query(queryStr);
			queryTest.setType(Type.ALL);
			annotation = new ParserAnnotation();
			annotation.setQuery(query);
			annotation.setQueryText(queryStr);
			annotation.setWriteLog(true);
			annotation.setQueryType(query.getType());

			annotationTest = new ParserAnnotation();
			annotationTest.setQuery(queryTest);
			annotationTest.setQueryText(queryTest.text);
			annotationTest.setWriteLog(true);
			annotationTest.setQueryType(queryTest.getType());
			//annotation.
			System.out.println("-------total query number:" + "?" + " current: " + (i + 1) + query.text);

			parserResult = phraseParser.parseForYunyin(annotation);
			parserResultTest = phraseParserTest.parseForYunyin(annotationTest);
			if (!parserResult.equals(resultTest)) {
				sb.append("----------not equal----------------------\r\n");
			}

			total += calcluateTotal(getMatchPatternDuration(parserResult.processLog));
			totalTest += calcluateTotal(getMatchPatternDuration(parserResultTest.processLog));
			sb.append("No:" + (i + 1) + " query: " + queryStr + "\r\n");
			score = getScore(parserResult.processLog);
			scoreTest = getScore(parserResultTest.processLog);

			scoreTotal += score;
			scoreTestTotal += scoreTest;
			if (scoreTest == 100) {
				oneHundredTest++;
			}

			if (score == 100) {
				oneHundred++;
			}

			if (score != 100 && scoreTest == 100) {
				sb1.append("No:" + (i + 1) + " query: " + queryStr + "-----------------------------------better\r\n");
				sb1.append("score:" + getScore(parserResult.processLog) + " matchpattern duration: "
						+ getMatchPatternDuration(parserResult.processLog) + " old:" + getSytaticId(parserResult.qlist)
						+ " " + parserResult.standardQueries + "\r\n");
				sb1.append("score:" + getScore(parserResultTest.processLog) + " matchpattern duration: "
						+ getMatchPatternDuration(parserResultTest.processLog) + " new:"
						+ getSytaticId(parserResultTest.qlist) + " " + parserResultTest.standardQueries + "\r\n");
				countBetter++;
			}
			if (score > scoreTest) {
				sb1.append("No:" + (i + 1) + " query: " + queryStr + "\r\n");
				sb1.append("score:" + getScore(parserResult.processLog) + " matchpattern duration: "
						+ getMatchPatternDuration(parserResult.processLog) + " old:" + getSytaticId(parserResult.qlist)
						+ " " + parserResult.standardQueries + "\r\n");
				sb1.append("score:" + getScore(parserResultTest.processLog) + " matchpattern duration: "
						+ getMatchPatternDuration(parserResultTest.processLog) + " new:"
						+ getSytaticId(parserResultTest.qlist) + " " + parserResultTest.standardQueries + "\r\n");
				countWorse++;
			}
			sb.append("score:" + getScore(parserResult.processLog) + " matchpattern duration: "
					+ getMatchPatternDuration(parserResult.processLog) + " old:"
					+ getSytaticId(parserResult.qlist) + " " + parserResult.standardQueries + "\r\n");
			sb.append("score:" + getScore(parserResultTest.processLog) + " matchpattern duration: "
					+ getMatchPatternDuration(parserResultTest.processLog) + " new:"
					+ getSytaticId(parserResultTest.qlist) + " " + parserResultTest.standardQueries + "\r\n");
		}

		sb.append("total:" + total + "ms\r\n");
		sb.append("totalTest:" + totalTest + "ms\r\n");
		sb.append("improve:" + (100 - (totalTest * 100) / total) + "%\r\n");
		sb.append("score:" + scoreTotal + "\r\n");
		sb.append("scoreTest:" + scoreTestTotal + "\r\n");
		sb.append("better:" + countBetter + "\r\n");
		sb.append("worse:" + countWorse + "\r\n");
		sb.append("percentage:" + 100 * oneHundred / i + "%\r\n");
		sb.append("percentageTest:" + 100 * oneHundredTest / i + "%\r\n");

		try {
			File file = new File(outputFilePath);
			OutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(sb.toString().getBytes());
			fileOutputStream.close();

			file = new File(outputFilePath1);
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(sb1.toString().getBytes());
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getMatchPatternDuration(String log) {
		int index = log.indexOf("Match_Syntactic_Patterns_Breadth_First_By_Chunk");
		if (index > 0) {
			index = log.indexOf("##", index - 15);
			if (index > 0) {
				return log.substring(index, index + 12);
			}
		}
		return "";
	}
	
	private static int getScore(String log) {
		int rtn = 0;
		try {
			int start = log.indexOf("score:");
			int end = log.indexOf("]", start);
			//		Pattern pattern = Pattern.compile("\\d*?");
			//		Matcher matcher = pattern.matcher(log.substring(start, end));
			//		String rtn = "";
			//
			//		if (matcher.matches()) {
			//			rtn = matcher.group();
			//		}
			rtn = Integer.parseInt(log.substring(start + 6, end));
		} catch (Exception e) {

		}
		return rtn;
	}

	private static int calcluateTotal(String str) {
		Pattern patttern = Pattern.compile("\\d+");
		Matcher matcher = patttern.matcher(str);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		} else {
			return 0;
		}
	}

	private static String getSytaticId(ArrayList<ArrayList<SemanticNode>> nodes) {
		StringBuffer sb = new StringBuffer();
		if (nodes != null && nodes.size() > 0) {
			for (SemanticNode node : nodes.get(0)) {
				if (node.type == NodeType.BOUNDARY) {
					BoundaryNode boundaryNode = (BoundaryNode) node;
					if (boundaryNode.isStart()) {
						sb.append(boundaryNode.syntacticPatternId + " ");

					}
				}
			}
		}
		return sb.toString();
	}
	
}
