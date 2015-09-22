package conf.stock.queryclassify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bench.ParserAgent;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.Util;

import conf.stock.StockIndex;
import conf.stock.queryclassify.QueryClass.ClassLevel;
import conf.tool.ToolUtil;

public class StockQueryClassifierByIndex {

	/**
	 * @param args
	 * @throws DataConfException
	 */

	/***
	 * 按照产品的需求初始化
	 * @param conf
	 * @throws DataConfException
	 */
	public static QueryClass init() {
		String str = "主力追踪选股/1 资金流向/2 资金流向指标/3 机构追踪/2 股东指标/3 机构持股指标/4 "
				+ "价值投资选股/1 财务分析/2 财务分析/3 财务报表/3 盈利预测/3 上市公司业绩快报/4 报表附注/3 机构评级/预测/2 盈利预测/3 盈利预测综合值/4 投资评级综合值/4 预测与评级明细值/4 "
				+ "技术分析选股/1 技术形态/3 技术指标/3 行情指标/3 "
				+ "其它指标选股/1 估值指标/3 风险分析/3 分红指标/3 配股指标/3 增发指标/3 盈利预测/3 上市公司业绩预告/4 首发（IPO）指标/3 其他/3";
		ArrayList<QueryClass> list = new ArrayList<QueryClass>();
		QueryClass root = new QueryClass.Builder("root", 0).build();
		list.add(root);
		for (String s : str.split(" ")) {
			String name = s.substring(0, s.lastIndexOf("/"));
			int level = Integer.parseInt(s.substring(s.lastIndexOf("/") + 1));
			QueryClass qc = new QueryClass.Builder(name, level).build();
			if (list != null && !list.isEmpty()) {
				for (int idx = list.size() - 1; idx >= 0; --idx) {
					if (level >= list.get(idx).level + 1) {
						list.get(idx).children.add(qc);
						qc.father = list.get(idx);
						break;
					}
				}
			}
			list.add(qc);
		}
		return root;
	}

	public static void print(QueryClass qcls) {
		System.out.println(qcls.name);
		if (qcls.children != null && !qcls.children.isEmpty()) {
			for (QueryClass qc : qcls.children) {
				print(qc);
			}
		} else
			return;
	}

	/***
	 * 获取所有的QueryClass
	 * @param qc
	 */
	public static void getQueryClsSet(QueryClass qc) {
		if (qc.children.isEmpty()) {
			QUERY_CLASS_SET.add(qc);
			return;
		} else {
			for (QueryClass tmpqc : qc.children)
				getQueryClsSet(tmpqc);
		}
	}

	/****
	 * 获取text中每一指标对应的QueryClass，即每一个index对应的选股类型
	 * 
	 * @param text
	 * @param qp
	 * @param root
	 * @return
	 */
	public static List<QueryClass> getClassifyPath(String text, QueryParser qp,
			Node root) {
		LinkedList<QueryClass> rtn = new LinkedList<QueryClass>();
		ArrayList<String> indexs = ToolUtil.getQueryIndex(text, qp);
		for (String index : indexs) {
			// System.out.println(index);
			Node node = ToolUtil.getXMLNode(root, index);
			if (node == null) {
				System.err.println("text:" + text + " ===node is null");
				return null;
			}
			// System.out.println("node tile: "
			// + ((Element) node).getAttribute("title"));
			ArrayList<String> fatherList = ToolUtil.getNodesAttrib(
					ToolUtil.getAncestor(node), "title");
			if (fatherList == null || fatherList.isEmpty()) {
				System.err.println("fatherList is null");
				return null;
			}
			boolean isMatched = false;

			for (String father : fatherList) {
				// System.out.println("father: " + father);
				for (QueryClass tmpQC : QUERY_CLASS_SET) {
					if (tmpQC.name.equals(father)) {
						QueryClass tmpFather = tmpQC.getFirstAncestor(tmpQC);
						// System.out.println("tmpFather: " + tmpFather.name);
						tmpFather.directSon = tmpQC;
						rtn.add(tmpFather);
						isMatched = true;
						break;
					}
				}
				if (isMatched == true)
					break;
			}
			// 查找不到则为其他选股
			if (isMatched == false) {
				QueryClass tmpQC = getOtherQueryClass();
				// System.out.println(tmpQC.name);
				QueryClass tmpFather = tmpQC.getFirstAncestor(tmpQC);
				tmpFather.directSon = tmpQC;
				// System.out.println(tmpFather.name);
				rtn.add(tmpFather);
			}
		}

		return rtn;
	}

	/****
	 * 经过两轮排序选择，得到问句所属的QueryClass
	 * 
	 * @param qcList
	 * @return
	 */
	public static QueryClass parseClassifier(List<QueryClass> qcList) {
		if (qcList == null || qcList.isEmpty()) {
			System.err.println("qcList is null ");
			return null;
		}

		// 第一轮排序，选择问句所属的大类
		Pair<String, Integer> maxOfFirst = null;
		ArrayList<Pair<String, Integer>> firstSortList = new ArrayList<Pair<String, Integer>>();
		for (QueryClass qc : qcList) {
			if (firstSortList.isEmpty())
				firstSortList.add(new Pair<String, Integer>(qc.name, 1));
			else {
				boolean isAdd = false;
				for (Pair<String, Integer> pair : firstSortList) {
					if (pair.second.equals(qc.name)) {
						pair.second++;
						isAdd = true;
						break;
					}
				}
				if (isAdd == false)
					firstSortList.add(new Pair<String, Integer>(qc.name, 1));
			}
		}

		for (int i = 0; i < firstSortList.size(); ++i) {
			if (maxOfFirst == null
					|| maxOfFirst.second < firstSortList.get(i).second)
				maxOfFirst = firstSortList.get(i);
		}

		if (maxOfFirst == null) {
			System.err.println("maxOfFirst is null");
			return null;
		}
		// System.out.println("maxOfFirst:" + maxOfFirst.first + " " +
		// maxOfFirst.second);
		// 第二轮排序
		for (int i = 0; i < qcList.size();) {
			if (qcList.get(i).name.equals(maxOfFirst.first))
				++i;
			else
				qcList.remove(i);
		}
		Pair<String, Integer> maxOfSecond = null;
		ArrayList<Pair<String, Integer>> secondSortList = new ArrayList<Pair<String, Integer>>();
		for (QueryClass qc : qcList) {
			if (secondSortList.isEmpty())
				secondSortList.add(new Pair<String, Integer>(qc.name, 1));
			else {
				boolean isAdd = false;
				for (Pair<String, Integer> pair : secondSortList) {
					if (pair.second.equals(qc.name)) {
						pair.second++;
						isAdd = true;
						break;
					}
				}
				if (isAdd == false)
					secondSortList.add(new Pair<String, Integer>(qc.name, 1));
			}
		}

		for (int i = 0; i < secondSortList.size(); ++i) {
			if (maxOfSecond == null
					|| maxOfSecond.second < secondSortList.get(i).second)
				maxOfSecond = secondSortList.get(i);
		}
		if (maxOfSecond == null)
			return null;
		else {
			for (QueryClass qc : qcList) {
				if (qc.name.equals(maxOfSecond.first))
					return qc;
			}
			return null;
		}
	}

	/***
	 * 查找“其他”
	 * 
	 * @return
	 */
	public static QueryClass getOtherQueryClass() {
		if (QUERY_CLASS_SET == null || QUERY_CLASS_SET.isEmpty())
			return null;
		else {
			for (QueryClass qc : QUERY_CLASS_SET) {
				if (qc.name.equals("其他"))
					return qc;
			}
			return null;
		}
	}

	public static String parse(String text, QueryParser qp, Node root) {
		List<QueryClass> clsPath = getClassifyPath(text, qp, root);

		// for(QueryClass qc : clsPath){
		// System.out.println("qc name :" + qc.name);
		// System.out.println("qc son :" + qc.directSon.name);
		// }
		if (clsPath != null && !clsPath.isEmpty()) {
			QueryClass result = parseClassifier(clsPath);
			if (result != null) {
				return result.name + "(" + result.directSon.name + ") ";
			}
		}
		return null;
	}

	public static void main(String[] args) throws DataConfException {
		// TODO Auto-generated method stub

		// initialize
		Document doc = Util.readXMLFile("stock.xml");
		Element root = doc.getDocumentElement();
		Node rootNode = (Node) root;

		qp = QueryParser.getParser("./conf/qparser.conf");
		QueryClass queryCls = init();
		getQueryClsSet(queryCls);

		ArrayList<String> queries = Util.readTxtFile("test-query.txt");
		ArrayList<String> tout = new ArrayList<String>(queries.size()+1) ;
//		ArrayList<String> classifyTestOut = new ArrayList<String>();
		int count = 0 ;
		for (String text : queries) {
			// for (String s : ToolUtil.getQueryIndex(text, qp)) {
			// System.out.print(s + " ");
			// }
			// System.out.println();
			System.out.println("正在处理第" + count++ + "条。。。");
			String result = parse(text, qp, rootNode);

//			System.out.println(text + " ==> " + result);
			tout.add(text + " ==> " + result) ;
			// System.out.println();
			// List<QueryClass> clsPath = getClassifyPath(text, qp, rootNode) ;
			// if (clsPath != null && !clsPath.isEmpty()){
			// for (QueryClass qc : clsPath) {
			// // if (qc != null)
			// // System.out.print(qc.hashCode() + qc.name + "("
			// // + qc.directSon.name + ") ");
			// }
			// QueryClass result = parseClassifier(clsPath);
			// if (result != null) {
			// System.out.println(result.name + "(" + result.directSon.name +
			// ") ");
			// }
			// else System.out.println("result is null");
			// }
			// System.out.println();
		}
		ToolUtil.write2Txt(tout, new File("问句分类结果.txt")) ;

		System.exit(0);
	}

//	public static HashMap<String, String> CLASSIFY_MAP_ = new HashMap<String, String>();
	public static HashSet<QueryClass> QUERY_CLASS_SET = new HashSet<QueryClass>();
	public static ArrayList<Node> xmlNodes = new ArrayList<Node>();
	public static QueryParser qp;

}
