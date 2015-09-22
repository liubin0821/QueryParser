package conf.tool;

/***
 * 公用函数
 * @author lu
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Pair;

public class ToolUtil {

	private static Node curXMLNode = null;

	/***
	 * 获取问句的指标
	 * 
	 * @param query
	 * @param qp
	 * @return
	 */
	public static ArrayList<String> getQueryIndex(Query query, QueryParser qp) {
		ArrayList<String> rtn = new ArrayList<String>();
		qp.parse(query);
		ArrayList<SemanticNode> nodes = query.getNodes();
		for (SemanticNode sn : nodes) {
			if (sn.type == NodeType.CLASS)
				rtn.add(sn.text.trim());
		}
		return rtn;
	}

	/***
	 * 获取问句的指标
	 * 
	 * @param query
	 * @param qp
	 * @return
	 */
	public static ArrayList<String> getQueryIndex(String text, QueryParser qp) {
		Query query = new Query(text);
		return getQueryIndex(query, qp);
	}

	/***
	 * 获取一个问句中频率最大的指标。
	 * 
	 * @param text
	 * @param qp
	 * @return
	 */
	public static String getMaxFreIndexFromQuery(String text, QueryParser qp) {
		ArrayList<String> indexs = getQueryIndex(text, qp);
		if (indexs == null || indexs.isEmpty())
			return null;
		ArrayList<Pair<String, Integer>> counts = new ArrayList<Pair<String, Integer>>();
		for (String s : indexs) {
			boolean isExist = false;
			for (Pair<String, Integer> pair : counts) {
				if (pair.first.equals(s)) {
					++pair.second;
					isExist = true;
					continue;
				}
			}
			if (isExist == false)
				counts.add(new Pair<String, Integer>(s, 1));
		}
		Pair<String, Integer> tMaxFreIndex = counts.get(0);
		for (int i = 1; i < counts.size(); ++i) {
			if (counts.get(i).second > tMaxFreIndex.second)
				tMaxFreIndex = counts.get(i);
		}
		return tMaxFreIndex.first;
	}

	/***
	 * 获取dom树中node的所有祖先节点
	 * 
	 * @param node
	 * @return
	 */

	public static ArrayList<Node> getAncestor(Node node) {
		ArrayList<Node> list = new ArrayList<Node>();

		if (node.getParentNode() != null) {
			list.add(node.getParentNode());
			list.addAll(getAncestor(node.getParentNode()));
		}
		return list;
	}

	/***
	 * 获取dom树中 nodes的属性值
	 * 
	 * @param nodes
	 * @param attName
	 * @return
	 */
	public static ArrayList<String> getNodesAttrib(ArrayList<Node> nodes,
			String attName) {
		if (nodes == null || nodes.isEmpty())
			return null;
		ArrayList<String> attNames = new ArrayList<String>();
		for (Node node : nodes) {
			if (node.hasAttributes()) {
				attNames.add(((Element) node).getAttribute(attName));
			}
		}
		return attNames;
	}

	/***
	 * 查找dom树，查找title = name 的Node
	 * 
	 * @param root
	 * @param name
	 * @param hasFind
	 */
	private static void getXMLNode(Node root, String name, boolean hasFind) {
		Element e = (Element) root;
		if (hasFind == true)
			return;
		if (e.getAttribute("title").equalsIgnoreCase(name)) {
			curXMLNode = root;
			hasFind = true;
		} else {
			for (int index = 0; index < root.getChildNodes().getLength(); ++index) {
				getXMLNode(root.getChildNodes().item(index), name, hasFind);
			}
		}
	}

	/**
	 * 查找dom树，查找title = name 的Node
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	public static Node getXMLNode(Node root, String name) {
		getXMLNode(root, name, false);
		Node rtn = curXMLNode;
		curXMLNode = null;
		return rtn;
	}

	/***
	 * 写入文件
	 * 
	 * @param list
	 * @param fileName
	 * @return
	 */
	public static boolean write2Txt(List<String> list, String fileName) {
		return write2Txt(list, new File(fileName));
	}

	public static boolean write2Txt(List<String> list, File file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String s : list) {
				bw.write(s + "\n");
			}
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
