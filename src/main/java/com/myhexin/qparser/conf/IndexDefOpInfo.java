package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public class IndexDefOpInfo {

	public static DefOpInfo getDefOpInfoByIndexName(String indexName,
			Type type) {
		HashMap<String, DefOpInfo> dataSrc = getData(type);
		
		if (dataSrc == null) {
			return null;
		}
		return dataSrc.get(indexName);
	}

	public static void load(Document doc, Type type) {
		HashMap<String, DefOpInfo> defInfoTmp = new HashMap<String, DefOpInfo>();
		Element root = doc.getDocumentElement();
		NodeList infoNodes = root.getChildNodes();
		ArrayList<String> warningMsg = new ArrayList<String>();
		if (infoNodes.getLength() == 0) {
			// 现在没有信息也可以
			return;
		}
		for (int i = 0; i < infoNodes.getLength(); i++) {
			Node defOpNodeI = infoNodes.item(i);
			if (defOpNodeI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!defOpNodeI.getNodeName().equals("def_op")) {
				warningMsg.add(String.format(
						"expect defop node while got %s",
						defOpNodeI.getNodeName()));
			}

			NamedNodeMap namedNodeMap = defOpNodeI.getAttributes();
			String label = namedNodeMap.getNamedItem("label").getNodeValue();
			if (defInfoTmp.containsKey(label)) {
				warningMsg.add(String.format("指标 “%s” 在配置文件中有重复定义", label));
			}
			try {
				DefOpInfo defOpInfoI = parseDefOpInfo(label, defOpNodeI.getChildNodes(), type);
				if (defOpInfoI != null)
					defInfoTmp.put(label, defOpInfoI);
			} catch (QPException e) {
				warningMsg.add(String.format("指标 “%s” 的默认值信息读取出错，错误信息：%s",
						label, e.getMessage()));
			}
		}
		setData(defInfoTmp, type);
		if (warningMsg.size() > 0) {
			;
		}
	}

	private static DefOpInfo parseDefOpInfo(String label,
			NodeList childNodes, Type type) throws UnexpectedException,
			NotSupportedException {
		DefOpInfo defOpInfo = new IndexDefOpInfo().new DefOpInfo(label);
		Collection collection = MemOnto.getOntoIndex(label, ClassNodeFacade.class, type);
		if (collection == null || collection.isEmpty())
			return null;
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			ClassNodeFacade indexNode = (ClassNodeFacade) iterator.next();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node infoNodeI = childNodes.item(i);
				if (infoNodeI.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				} else if (infoNodeI.getNodeName().equals("op")) {
					parseOpInfo(defOpInfo, indexNode, infoNodeI);
				} else {
					throw new UnexpectedException("Unexpected node name:%s",
							infoNodeI.getNodeName());
				}
			}
		}
		return defOpInfo;
	}

	private static void parseOpInfo(DefOpInfo defOpInfo,
			ClassNodeFacade indexNode, Node infoNodeI) throws UnexpectedException,
			NotSupportedException {

		NamedNodeMap valsNodes = infoNodeI.getAttributes();
		Node defNode = valsNodes.getNamedItem("default");
		defOpInfo.defOp = getOpForIndex(defNode);
	}

	private static SemanticNode getOpForIndex(Node opNode) throws UnexpectedException, NotSupportedException {
		if (opNode == null) {
			return null;
		}
		String opStr = opNode.getNodeValue();
		FocusNode node = new FocusNode(opStr);
		return node;
	}

	private static Map<Query.Type, HashMap<String, DefOpInfo>> domainToIndexDefOpInfo = new HashMap<Query.Type, HashMap<String, DefOpInfo>>();

	private static void setData(HashMap<String, DefOpInfo> defInfoTmp,
			Type type) {
		domainToIndexDefOpInfo.put(type, defInfoTmp);
	}

	private static HashMap<String, DefOpInfo> getData(Type type) {
		if (type == null) {
			return null;
		}
		return domainToIndexDefOpInfo.get(type);
	}

	public class DefOpInfo {

		public DefOpInfo(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public SemanticNode getDefOp(DateInfoNode curDate) {
			SemanticNode rtn = getValue(defOp, curDate);
			return rtn;
		}

		private String label = null;
		private SemanticNode defOp = null;
	}

	public SemanticNode getValue(SemanticNode defOp, DateInfoNode curDate) {
		if (defOp == null) {
			return null;
		}
		SemanticNode rtn = NodeUtil.copyNode(defOp); //.copy();
		return rtn;
	}
}
