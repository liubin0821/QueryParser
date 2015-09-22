package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.IndexNodeValueType.PropNodeType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.xmlreader.XmlReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PhraseXmlReaderSemanticArgumentType {
	public static Map<String, IndexNodeValueType> indexNodeValueTypeMap = new HashMap<String, IndexNodeValueType>();
	public static void loadSemanticArgumentTypeMap(Document doc) {
		// System.out.println("loadSemanticArgumentTypeMap");
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "IndexNode");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "ValueType");
			if (id == null)
				continue;
			ValueType valueType = IndexNodeValueType.getValueTypeFromStr(id);
			if (valueType == null)
				continue;
			IndexNodeValueType indexNodeValueType = new IndexNodeValueType(valueType);
			loadIndexNodeValueType(node, indexNodeValueType);
			indexNodeValueTypeMap.put(id, indexNodeValueType);
		}
		buildMap();
	}
	
	private static void loadIndexNodeValueType(Node node, IndexNodeValueType indexNodeValueType) {
		String desc = XmlReader.getChildElementValueByTagName(node, "desc");
		List<ValueType> subs = loadSubclass(node);
		List<PropNodeType> propNodeTypes = loadPropNodeTypes(node);
		indexNodeValueType.setDesc(desc);
		indexNodeValueType.setSubs(subs);
		addPropNodeTypes(indexNodeValueType, propNodeTypes);
	}

	private static List<ValueType> loadSubclass(Node node) {
		List<ValueType> subs = new ArrayList<ValueType>();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "subclass");
		for (int i = 0; i < nodes.size(); i++) {
			ValueType valueType = IndexNodeValueType.getValueTypeFromStr(XmlReader.getElementValue(nodes.get(i)));
			if (valueType == null)
				continue;
			subs.add(valueType);
		}
		return subs;
	}
	
	private static List<PropNodeType> loadPropNodeTypes(Node node) {
		List<PropNodeType> propNodeTypes = new ArrayList<PropNodeType>();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(node, "PropNode");
		for (int i = 0; i < nodes.size(); i++) {
			Node child = nodes.get(i);
			String typeStr = XmlReader.getAttributeByName(child, "type");
			String unitStr = XmlReader.getAttributeByName(child, "unit");
			PropType propType = IndexNodeValueType.getPropTypeFromStr(typeStr);
			Unit unit = EnumConvert.getUnitFromStr(unitStr);
			PropNodeType propNodeType = new PropNodeType(propType, unit);
			propNodeTypes.add(propNodeType);
		}
		return propNodeTypes;
	}
	
	public static void buildMap() {
		Iterator iter = indexNodeValueTypeMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			IndexNodeValueType indexNodeValueType = (IndexNodeValueType) val;
			indexNodeValueType.setPropTypes(getPropTypes(indexNodeValueType));
		}
	}
	
	public static List<PropNodeType> getPropTypes(IndexNodeValueType indexNodeValueType) {
		List<PropNodeType> propNodeTypes = new ArrayList<PropNodeType>();
		for (PropNodeType prop : indexNodeValueType.getPropTypes())
			propNodeTypes.add(prop);
		for (ValueType valType : indexNodeValueType.getSubs()) {
			System.out.println("***" + valType.toString() + "***");
			propNodeTypes.addAll(getPropTypes(indexNodeValueTypeMap.get(valType.toString())));
		}
		System.out.println(indexNodeValueType.toString());
		System.out.println(propNodeTypes);
		return propNodeTypes;
	}
	
	public static void addPropNodeType(IndexNodeValueType indexNodeValueType, PropNodeType propNodeType) {
		boolean isExist = false;
		for (PropNodeType prop : indexNodeValueType.getPropTypes()) {
			if (prop.equals(propNodeType)) {
				isExist = true;
				break;
			}
		}
		if (!isExist)
			indexNodeValueType.getPropTypes().add(propNodeType);
	}
	
	public static void addPropNodeTypes(IndexNodeValueType indexNodeValueType, List<PropNodeType> propNodeTypes) {
		for (PropNodeType prop : propNodeTypes) {
			addPropNodeType(indexNodeValueType, prop);
		}
	}
	
	public static void main(String[] args) throws DataConfException {
		loadSemanticArgumentTypeMap(Util.readXMLFile("./data/stock/stock_phrase_semantic_argument_type.xml", true));
		System.out.println("*******************");
		Iterator iter = indexNodeValueTypeMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			System.out.println((String)key);
			IndexNodeValueType indexNodeValueType = (IndexNodeValueType) val;
			for (PropNodeType prop : indexNodeValueType.getPropTypes()) {
				System.out.println(prop.valueType + ":" + prop.unit);
			}
		}
		System.out.println("*******************");
	}
}

class IndexNodeValueType {
	private ValueType valueType = ValueType.UNDEFINED;
	private String desc = "";
	private List<ValueType> subs = new ArrayList<ValueType>();
	private List<PropNodeType> propTypes = new ArrayList<PropNodeType>();
	
	public IndexNodeValueType() {
		
	}
	
	public IndexNodeValueType(ValueType valueType) {
		this.valueType = valueType;
	}
	
	public IndexNodeValueType(ValueType valueType, String desc,
			List<ValueType> subs, List<PropNodeType> propTypes) {
		this.valueType = valueType;
		this.desc = desc;
		this.subs = subs;
		this.propTypes = propTypes;
	}
	
	public String toString() {
		return valueType.toString();
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<ValueType> getSubs() {
		return subs;
	}

	public void setSubs(List<ValueType> subs) {
		this.subs = subs;
	}

	public List<PropNodeType> getPropTypes() {
		return propTypes;
	}

	public void setPropTypes(List<PropNodeType> propTypes) {
		this.propTypes = propTypes;
	}
	
	public static class PropNodeType {
		public PropType valueType = PropType.UNKNOWN;
		public Unit unit = Unit.UNKNOWN;
		public PropNodeType() {
			
		}
		public PropNodeType(PropType valueType, Unit unit) {
			this.valueType = valueType;
			this.unit = unit;
		}
		public PropNodeType(String valueType, String unit) {
			
		}
		public String toString() {
			return valueType + "-" + unit;
		}
		public boolean equals(Object object) {
			if (this == object)
				return true;
			if (object == null)
				return false;
			if (!(object instanceof PropNodeType))
				return false;
			PropNodeType other = (PropNodeType)object;
			if (this.valueType == other.valueType && this.unit == other.unit)
				return true;
			else
				return false;
		}
	}
	
	public static ValueType getValueTypeFromStr(String str) {
		try {
			ValueType val = ValueType.valueOf(str);
			return val;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static PropType getPropTypeFromStr(String str) {
		try {
			PropType val = PropType.valueOf(str);
			return val;
		} catch (java.lang.IllegalArgumentException e) {
			return null;
		}
	}
	
	public static boolean isIndexNodeValueType(ValueType valueType, PropNodeType propNodeType) {
		IndexNodeValueType indexNodeValueType = PhraseXmlReaderSemanticArgumentType.indexNodeValueTypeMap.get(valueType.toString());
		for (PropNodeType prop : indexNodeValueType.getPropTypes()) {
			if (prop.equals(propNodeType))
				return true;
		}
		return false;
	}
	
	public static boolean isIndexNodeValueType(ValueType valueType, PropType propType, Unit unit) {
		PropNodeType propNodeType = new PropNodeType(propType, unit);
		return isIndexNodeValueType(valueType, propNodeType);
	}
}
