package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import java.util.ArrayList;

import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_syntactic.xml
 * SyntacticPattern/SemanticBind
 */
public class SemanticBind {

	public SemanticBind() {
		
	}

	public void setId(String id) {
		if (id == null || !id.matches("[0-9]+"))
			return ;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Deprecated
	public SemanticArguments getArguments() {
		return semanticBindArguments;
	}

	@Deprecated
	public SemanticArgument getArgument(int id, boolean create) {
		return semanticBindArguments.getSemanticArgument(id, create);
	}

	@Deprecated
	public SemanticArgumentDependency getArgumentDependency() {
		return semanticArgumentDependency;
	}

	private String id = null; // 新配置不需要此字段
	@Deprecated
	private SemanticArguments semanticBindArguments = new SemanticArguments(); // 新配置不需要此字段
	@Deprecated
	private SemanticArgumentDependency semanticArgumentDependency = new SemanticArgumentDependency(); // 新配置不需要此字段

	// 新配置相关字段及getter、setter方法
	private String uiRepresentation = ""; // 此字段暂时不用
	private String chineseRepresentation = ""; // 用于表示语义入口及多个语义相互关系的描述
	private String description = null; // 描述，注释，任意字符串
	private String semanticBindToIds = null; // 绑定的语义
	private ArrayList<SemanticBindTo> semanticBindTos = new ArrayList<SemanticBindTo>(); // 多语义绑定语义元素与语法元素的映射

	public String getUiRepresentation() {
		return uiRepresentation;
	}

	public void setUiRepresentation(String uiRepresentation) {
		this.uiRepresentation = uiRepresentation;
	}

	public String getChineseRepresentation() {
		return chineseRepresentation;
	}

	public void setChineseRepresentation(String chineseRepresentation) {
		chineseRepresentation = chineseRepresentation.replace("且", "&").replace("或", "|").replace("非", "!");
		chineseRepresentation = chineseRepresentation.replace(" ", "");
		this.chineseRepresentation = chineseRepresentation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSemanticBindToIds() {
		return semanticBindToIds;
	}

	public void setSemanticBindToIds(String semanticBindRoot) {
		semanticBindRoot = semanticBindRoot.replace("且", "&").replace("或", "|").replace("非", "!");
		this.semanticBindToIds = semanticBindRoot;
	}

	public ArrayList<SemanticBindTo> getSemanticBindTos() {
		return semanticBindTos;
	}

	public void setSemanticBindTos(ArrayList<SemanticBindTo> semanticBindTos) {
		this.semanticBindTos = semanticBindTos;
	}

	public SemanticBindTo getSemanticBindTo(int sequence) {
		int size = semanticBindTos.size();
		if (sequence < 1)
			return null;
		// sequence 从1开始
		int sequenceidx = sequence - 1;

		if (sequenceidx < size) {
			return semanticBindTos.get(sequenceidx);
		} else if (sequenceidx == size) {
			SemanticBindTo semanticBindTo = new SemanticBindTo(sequence);
			semanticBindTos.add(semanticBindTo);
			return semanticBindTo;
		} else {
			//start
			for (int i=size; i < sequenceidx; i++) {
				SemanticBindTo semanticBindTo = new SemanticBindTo(i);
				semanticBindTos.add(semanticBindTo);
			}
			SemanticBindTo semanticBindTo = new SemanticBindTo(sequenceidx);
			semanticBindTos.add(semanticBindTo);
			return semanticBindTo;
			//end
			//return null; // 必须顺序加入,是否抛出异常
		}
	}

	public SemanticBindTo getSemanticBindTo(String sequence) {
		if (sequence == null || sequence.equals(""))
			return null;
		int sequenceid = Util.parseInt(sequence, -1);
		return getSemanticBindTo(sequenceid);
	}
}
