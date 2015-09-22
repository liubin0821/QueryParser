package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.WebAction;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;

public class IndexValueDefInfo {
	public static HashMap<String, String> indexDefaultVal = new HashMap<String, String>();

	// TODO
	public static void loadResource(Document doc) throws DataConfException {
		load(doc, Query.Type.STOCK);
	}

	public static DefValInfo getDefValInfoByIndexName(String indexName,
			Type type) {
		HashMap<String, DefValInfo> dataSrc;
		try {
			dataSrc = getData(type);
		} catch (UnexpectedException e) {
			return null;
		}
		return dataSrc.get(indexName);
	}

	private static void load(Document doc, Type type) {
		HashMap<String, DefValInfo> defInfoTmp = new HashMap<String, DefValInfo>();
		Element root = doc.getDocumentElement();
		NodeList infoNodes = root.getChildNodes();
		ArrayList<String> warningMsg = new ArrayList<String>();
		if (infoNodes.getLength() == 0) {
			// 现在没有信息也可以
			return;
		}
		for (int i = 0; i < infoNodes.getLength(); i++) {
			Node defValNodeI = infoNodes.item(i);
			if (defValNodeI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!defValNodeI.getNodeName().equals("def_value")) {
				warningMsg.add(String.format(
						"expect defvalue node while got %s",
						defValNodeI.getNodeName()));
			}

			NamedNodeMap namedNodeMap = defValNodeI.getAttributes();
			String label = namedNodeMap.getNamedItem("label").getNodeValue();
			if (defInfoTmp.containsKey(label)) {
				warningMsg.add(String.format("指标 “%s” 在配置文件中有重复定义", label));
			}
			try {
				DefValInfo defValInfoI = parseDefValInfo(label,
						defValNodeI.getChildNodes(), type);
				defInfoTmp.put(label, defValInfoI);
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

	private static DefValInfo parseDefValInfo(String label,
			NodeList childNodes, Type type) throws UnexpectedException,
			NotSupportedException {
		ClassNodeFacade indexNode = MemOnto.getOnto(label, ClassNodeFacade.class, type);
		DefValInfo defValInfo = new IndexValueDefInfo().new DefValInfo(label);
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node infoNodeI = childNodes.item(i);
			if (infoNodeI.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (infoNodeI.getNodeName().equals("value")) {
				parseValueInfo(defValInfo, indexNode, infoNodeI);
			} else if (infoNodeI.getNodeName().equals("web_style")) {
				parseWebActions(defValInfo, infoNodeI);
			} else if (infoNodeI.getNodeName().equals("movable_range")) {
				parseMovable(defValInfo, infoNodeI);
			} else {
				throw new UnexpectedException("Unexpected node name:%s",
						infoNodeI.getNodeName());
			}
		}
		return defValInfo;
	}

	private static void parseValueInfo(DefValInfo defValInfo,
			ClassNodeFacade indexNode, Node infoNodeI) throws UnexpectedException,
			NotSupportedException {

		NamedNodeMap valsNodes = infoNodeI.getAttributes();
		Node maxNode = valsNodes.getNamedItem("max");
		Node minNode = valsNodes.getNamedItem("min");
		Node defNode = valsNodes.getNamedItem("default");
		Node movableRangeNode = valsNodes.getNamedItem("movable_range");

		defValInfo.maxVal = getValsForIndex(indexNode, maxNode);
		defValInfo.minVal = getValsForIndex(indexNode, minNode);
		defValInfo.defVal = getValsForIndex(indexNode, defNode);

		parseMovable(defValInfo, movableRangeNode);

		checkIsEQ(defValInfo.maxVal);
		checkIsEQ(defValInfo.minVal);
		checkIsEQ(defValInfo.movableRange);
	}

	private static SemanticNode getValsForIndex(ClassNodeFacade indexNode,
			Node valNode) throws UnexpectedException, NotSupportedException {
		if (indexNode == null || valNode == null) {
			return null;
		}
		String valStr = valNode.getNodeValue();
		boolean isNum = indexNode.isNumIndex();
		boolean isDate = indexNode.isDateIndex();
		boolean isStr = indexNode.isStrIndex();
		if (isNum) {
			// 若为数字，按数字解析
			NumNode num = NumUtil.makeNumNodeFromStr(valStr);
			return num;
		} else if (isDate) {
			// 若为时间，按时间尝试解析，保证补充默认值时该节点可正确解析
			DateNode date = DateUtil.makeDateNodeFromStr(valStr,null);
			return date;
		} else if (isStr) {
			// 若都不是，就按字符串解析，ofWhat是指标的值属性
			return makeStrNodeFromStr(indexNode, valStr);
		} else {
			throw new UnexpectedException("Unexpected value type:“%s”",
					indexNode.type);
		}
	}

	private static SemanticNode makeStrNodeFromStr(ClassNodeFacade indexNode,
			String valStr) throws UnexpectedException {
		PropNodeFacade valProp = indexNode.getPropOfValue();
		if (!valProp.isStrProp()) {
			throw new UnexpectedException("非字符串型指标");
		}
		StrNode valNode = new StrNode(valStr);
		//valNode.ofWhat = new ArrayList<SemanticNode>();
		//valNode.ofWhat.add(valProp);
		return valNode;
	}

	private static void parseWebActions(DefValInfo defValInfo,
			Node webActionNode) throws UnexpectedException {
		NamedNodeMap valsNodes = webActionNode.getAttributes();
		Node webActionStyleNode = valsNodes.getNamedItem("style");
		if (webActionStyleNode == null) {
			return;
		}
		String[] webActionsStrs = webActionStyleNode.getNodeValue().split(";");
		if (webActionsStrs.length == 1 && webActionsStrs[0].trim().isEmpty()) {
			return;
		}
		for (int i = 0; i < webActionsStrs.length; i++) {
			WebAction addWebAction = getWebActionFromStr(webActionsStrs[i]);
			defValInfo.webActions.add(addWebAction);
		}
	}

	private static WebAction getWebActionFromStr(String webActionStr)
			throws UnexpectedException {
		// 添加web处理信号，暂时只有一种
		WebAction webAction = null;
		if (webActionStr.equals(MiscDef.WEBSTYLE_HIGH_LIGHT)) {
			webAction = WebAction.HIGH_LIGHT;
		} else {
			throw new UnexpectedException("Unexpected web action :%s",
					webActionStr);
		}
		return webAction;
	}

	private static void parseMovable(DefValInfo defValInfo, Node movableNode)
			throws NotSupportedException, UnexpectedException {
		if (movableNode == null || movableNode.getNodeValue() == null
				|| movableNode.getNodeValue().trim().length() == 0) {
			return;
		}
		String movableStr = movableNode.getNodeValue();
		if (movableStr.equals("0")) {
			// 当浮动范围设为0时，即为指标强制不浮动
			defValInfo.valIsMovable = false;
			return;
		}
		defValInfo.movableRange = NumUtil.makeNumNodeFromStr(movableStr);
	}

	private static void checkIsEQ(SemanticNode val) throws UnexpectedException {
		if (val == null || val.type != NodeType.NUM
				&& val.type != NodeType.DATE) {
			return;
		} else if (val.type == NodeType.NUM
				&& !((NumNode) val).getRangeType().equals(OperDef.QP_EQ)
				|| val.type == NodeType.DATE
				&& !((DateNode) val).getRangeType().equals(OperDef.QP_EQ)) {
			throw new UnexpectedException("Range is Not EQ");
		}
	}

	private static Map<Query.Type, HashMap<String, DefValInfo>> domainToIndexValueDefInfo = new HashMap<Query.Type, HashMap<String, DefValInfo>>();

	private static void setData(HashMap<String, DefValInfo> defInfoTmp,
			Type type) {
		domainToIndexValueDefInfo.put(type, defInfoTmp);
	}

	private static HashMap<String, DefValInfo> getData(Type type)
			throws UnexpectedException {
		if (type == null) {
			throw new UnexpectedException("unknown type", type);
		}
		return domainToIndexValueDefInfo.get(type);
	}

	public class DefValInfo {

		public DefValInfo(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public SemanticNode getDefVal(DateInfoNode curDate) {
			SemanticNode rtn = getValue(defVal, curDate);
			return rtn;
		}

		public SemanticNode getMaxVal(DateInfoNode curDate) {
			SemanticNode rtn = getValue(maxVal, curDate);
			return rtn;
		}

		public SemanticNode getMinVal(DateInfoNode curDate) {
			SemanticNode rtn = getValue(minVal, curDate);
			return rtn;
		}

		public NumNode getMovableRange(DateInfoNode curDate) {
			NumNode rtn = (NumNode) getValue(movableRange, curDate);
			return rtn;
		}

		public boolean isValIsMovable() {
			return valIsMovable;
		}

		public ArrayList<WebAction> getWebActions() {
			return webActions;
		}

		private String label = null;
		private SemanticNode defVal = null;
		private SemanticNode maxVal = null;
		private SemanticNode minVal = null;
		private NumNode movableRange = null;
		private boolean valIsMovable = true;
		private ArrayList<WebAction> webActions = new ArrayList<WebAction>();
	}

	public SemanticNode getValue(SemanticNode defVal, DateInfoNode curDate) {
		if (defVal == null) {
			return null;
		}
		try {
			SemanticNode rtn = defVal.type == NodeType.DATE ? DateUtil
					.getDateNodeFromStr(defVal.getText(),null) : NodeUtil.copyNode(defVal); //defVal.copy();
			return rtn;
		} catch (QPException e) {
			assert (false);
		}
		return null;
	}
}
