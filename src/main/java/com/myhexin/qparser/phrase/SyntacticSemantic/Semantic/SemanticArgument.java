package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_semantic.xml SemanticPattern/Argument
 */
public class SemanticArgument {
	private static Logger _logger = LoggerFactory.getLogger(SemanticArgument.class);

	public SemanticArgument() {
		this.acceptValueTypes.add(ValueType.UNDEFINED);
	}

	public SemanticArgument(int id) {
		this.id = id;
		this.acceptValueTypes.add(ValueType.UNDEFINED);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SemanArgType getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null || type.equals(""))
			return;
		this.type = getTypeFromStr(type);
	}

	public void setType(SemanArgType type) {
		this.type = type;
	}

	public List<ValueType> getAllAcceptValueTypes() {
		return acceptValueTypes;
	}

	public void setValueType(String valueType) {
		if (valueType == null || valueType.equals("")) {
			return;
		}
		this.acceptValueTypes = getValueTypeFromStr(valueType);
	}

	public void setValueType(List<ValueType> valueType) {
		this.acceptValueTypes = valueType;
	}

	public String getSpecificIndex() {
		return specificIndex;
	}

	public void setSpecificIndex(String specificIndex) {
		if (specificIndex == null || specificIndex.equals(""))
			return;
		this.specificIndex = specificIndex;
	}

	public String getSpecificIndexGroup() {
		return specificIndexGroup;
	}

	public void setSpecificIndexGroup(String specificIndexGroup) {
		if (specificIndexGroup == null || specificIndexGroup.equals(""))
			return;
		this.specificIndexGroup = specificIndexGroup;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		if (superClass == null || superClass.equals(""))
			return;
		this.superClass = superClass;
	}

	public int getListElementMinCount() {
		return listElementMinCount;
	}

	public void setListElementMinCount(String listElementMinCount) {
		if (listElementMinCount == null || listElementMinCount.equals(""))
			return;
		setListElementMinCount(Util.parseInt(listElementMinCount, 2));
	}

	public void setListElementMinCount(int listElementMinCount) {
		if (listElementMinCount < 2)
			listElementMinCount = 2;
		this.listElementMinCount = listElementMinCount;
	}

	public String getDefaultIndex() {
		return defaultIndex;
	}

	public void setDefaultIndex(String defaultIndex) {
		if (defaultIndex == null || defaultIndex.equals(""))
			return;
		this.defaultIndex = defaultIndex;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		if (defaultValue == null || defaultValue.equals(""))
			return;
		this.defaultValue = defaultValue;
	}

	public static enum SemanArgType {
		INDEX, INDEXLIST, CONSTANT, CONSTANTLIST, ANY
	};

	public static enum ValueType {
		/*
		 * STRING, NUMBER, PERCENTAGE, UNDEFINED, DATE
		 */
		UNDEFINED, STRING, DATE, FIGURE, NUMBER, LONG_NUM, DOUBLE_NUM, RATIO, PERCENTAGE, TIMES
	};

	private int id = -1;
	private SemanArgType type = null;
	// 从ValueType转成List<ValueType>, 保存参数在该语义里所有匹配的值类型, 这样可以增强匹配的灵活性
	//例子:
	//<Argument id="2" type="constant" ValueType="percentage|number|date" DefaultValue="50"/>
	private List<ValueType> acceptValueTypes = new ArrayList<ValueType>();
	private String specificIndex = null;
	private String specificIndexGroup = null;
	private String superClass = null; // unused
	private int listElementMinCount = 1;
	private String defaultIndex = null;
	private String defaultValue = null;

	public static SemanArgType getTypeFromStr(String typeStr) {
		if (typeStr == null)
			return null;
		SemanArgType type = null;
		typeStr = typeStr.toLowerCase();
		if (typeStr.equals("index"))
			type = SemanArgType.INDEX;
		else if (typeStr.equals("indexlist"))
			type = SemanArgType.INDEXLIST;
		else if (typeStr.equals("constant"))
			type = SemanArgType.CONSTANT;
		else if (typeStr.equals("constantlist"))
			type = SemanArgType.CONSTANTLIST;
		else if (typeStr.equals("any"))
			type = SemanArgType.ANY;
		return type;
	}

	public static List<ValueType> getValueTypeFromStr(String valueTypeStr) {
		List<ValueType> rtnValueTypes = new ArrayList<ValueType>();

		String[] valueTypeStrList = valueTypeStr.toLowerCase().split("\\|");
		for (String str : valueTypeStrList) {
			switch (str) {
			case "string":
				rtnValueTypes.add(ValueType.STRING);
				break;
			case "number":
				rtnValueTypes.add(ValueType.NUMBER);
				break;
			case "percentage":
				rtnValueTypes.add(ValueType.PERCENTAGE);
				break;
			case "date":
				rtnValueTypes.add(ValueType.DATE);
				break;
			default:
				break;
			}
		}
		return rtnValueTypes;
	}

	/*
	 * 传入值类型集合, 判断是否和SemanticArgument里的acceptValueTypes完全一样
	 * 
	 */
	public boolean isSameValueType(List<ValueType> valuTypeCheckList) {
		//集合长度不匹配,肯定不相同
		if (valuTypeCheckList.size() != acceptValueTypes.size()) {
			return false;
		}
		boolean flag = false;
		for (ValueType acceptValueType : acceptValueTypes) {
			flag = false;
			for (ValueType valueTypeToCheck : valuTypeCheckList) {
				if (acceptValueType == valueTypeToCheck) {
					flag = true;
				}
			}
			//当前type没有找到,直接返回false
			if (!flag) {
				return false;
			}
		}
		//全部都匹配上,返回true
		return true;
	}

	/*
	 * 传入值类型集合, 当前参数里有任意一个满足传入类型就返回true
	 * 
	 */
	public boolean isContainsValueType(ValueType... valuTypeCheckList) {
		for (ValueType acceptValueType : acceptValueTypes) {
			for (ValueType valueTypeToCheck : valuTypeCheckList) {
				if (acceptValueType == valueTypeToCheck) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 取固定值类型都是为了创建默认节点,这种情况下值类型不应该有多个
	 */
	public ValueType getFixedValueType() {
		if (acceptValueTypes.size() > 0) {
			if (acceptValueTypes.size() > 1) {
				_logger.warn("句式中fixed或default的语义的值类型不应该有多个:" + acceptValueTypes.toString());
			}
			return acceptValueTypes.get(0);
		} else {
			return ValueType.UNDEFINED;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: ").append(id).append(";");
		sb.append("type: ").append(type).append(";");
		sb.append("valueType: ").append(acceptValueTypes).append(";");
		sb.append("specificIndex: ").append(specificIndex).append(";");
		sb.append("specificIndexGroup: ").append(specificIndexGroup).append(";");
		sb.append("superClass: ").append(superClass).append(";");
		sb.append("listElementMinCount: ").append(listElementMinCount).append(";");
		sb.append("defaultIndex: ").append(defaultIndex).append(";");
		sb.append("defaultValue: ").append(defaultValue);
		return sb.toString();
	}
}
