package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_syntactic.xml
 * SyntacticPattern/SemanticBind/SemanticBindTo/SemanticArgument
 */
public class SemanticBindToArgument {
	public SemanticBindToArgument() {
		super();
	}

	public SemanticBindToArgument(int argumentId) {
		this.argumentId = argumentId;
	}

	private int argumentId = -1; // 语义配置中argument的id，即$1
	private SemanArgType type = SemanArgType.ANY; // 语义配置中argument的type，如index、indexlist、constant、any
	private Source source = Source.ELEMENT; // 来源，element表示从语法元素或语义中取值；fixed表示固定值
	private BindToType bindToType = BindToType.SYNTACTIC_ELEMENT; // source = Source.ELEMENT，表示从
	private int elementId = -1; // "1"表示从语法元素中取，@SyntacticElement@id=1；"#1"表示从语义中取，@SemanticBindTo@sequence=1
	private int from = -1; // 参数类型为indexlist时，用于list元素两两生成关系表达式
	private int to = -1; // 参数类型为indexlist时，用于list元素两两生成关系表达式
	private String index = null; // 指标值，if @source=fixed && (@type=index or @type=indexlist)
	private String value = null; // 固定值，if @source=fixed && @type=constant

	public int getArgumentId() {
		return argumentId;
	}

	public void setArgumentId(int argumentId) {
		this.argumentId = argumentId;
	}
	
	public void setArgumentId(String argumentId) {
		if (argumentId == null || argumentId.equals(""))
			return;
		this.argumentId = Util.parseInt(argumentId, -1);
	}
	
	public SemanArgType getType() {
		return type;
	}

	public void setType(SemanArgType type) {
		this.type = type;
	}
	
	public void setType(String type) {
        if(type == null)
            return;
        type = type.toLowerCase();
        if (type.equals("index")) {
        	this.type = SemanArgType.INDEX;
        }
        else if (type.equals("indexlist")) {
        	this.type = SemanArgType.INDEXLIST;
        }
        else if (type.equals("constant")) {
        	this.type = SemanArgType.CONSTANT;
        }
        else if (type.equals("constantlist")) {
        	this.type = SemanArgType.CONSTANTLIST;
        }
        else if (type.equals("any")) {
        	this.type = SemanArgType.ANY;
        }
    }

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	
	public void setSource(String source) {
        if(source == null)
            return;
        if (source.equals("element") || source.equals("argument")) {
        	this.source = Source.ELEMENT;
        }
        else if (source.equals("fixed")) {
        	this.source = Source.FIXED;
        }
    }

	public BindToType getBindToType() {
		return bindToType;
	}

	public void setBindToType(BindToType bindToType) {
		this.bindToType = bindToType;
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}
	
	public void setElementId(String bindToIdStr) {
		if (bindToIdStr == null || bindToIdStr.equals(""))
			return;
		if (bindToIdStr.contains("#")) {
			bindToType = BindToType.SEMANTIC;
			elementId = Util.parseInt(bindToIdStr.substring(1), -1);
		} else {
			elementId = Util.parseInt(bindToIdStr, -1);
		}
	}
	
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}
	
	public void setFrom(String from) {
		if(from == null || from.equals(""))
            return;
		this.from = Util.parseInt(from, -1);
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}
	
	public void setTo(String to) {
		if(to == null || to.equals(""))
            return;
		this.to = Util.parseInt(to, -1);
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static enum Source {
		ELEMENT, FIXED
	}
	
	public static enum BindToType {
		SEMANTIC, SYNTACTIC_ELEMENT
    }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("argumentId: ").append(argumentId).append(";");
		sb.append("type: ").append(type).append(";");
		sb.append("source: ").append(source).append(";");
		sb.append("bindToType: ").append(bindToType).append(";");
		sb.append("elementId: ").append(elementId).append(";");
		sb.append("from: ").append(from).append(";");
		sb.append("to: ").append(to).append(";");
		sb.append("index: ").append(index).append(";");
		sb.append("value: ").append(value);
		return sb.toString();
	}
}
