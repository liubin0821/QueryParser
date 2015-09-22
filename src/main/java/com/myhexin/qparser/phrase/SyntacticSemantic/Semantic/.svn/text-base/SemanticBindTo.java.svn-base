package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import java.util.ArrayList;

import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_syntactic.xml
 * SyntacticPattern/SemanticBind/SemanticBindTo
 */
public class SemanticBindTo {
	public SemanticBindTo() {
		super();
	}

	public SemanticBindTo(int sequence) {
		this.sequence = sequence;
	}

	public SemanticBindTo(int sequence, int bindToId) {
		this.sequence = sequence;
		this.bindToId = bindToId;
	}

	private int sequence = -1; // 此语义在句式多个语义中的序号，如：1、2、3、4、5
	private int bindToId = -1; // 此语义在语义配置中的编号，如“index>num”的语义编号为9
	// 语义中各元素与句式中各元素的映射关系
	private ArrayList<SemanticBindToArgument> semanticBindToArguments = new ArrayList<SemanticBindToArgument>();

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int id) {
		this.sequence = id;
	}

	public int getBindToId() {
		return bindToId;
	}

	public void setBindToId(int bindToId) {
		this.bindToId = bindToId;
	}
	
	public void setBindToId(String bindToId) {
		if (bindToId == null || bindToId.equals(""))
			return;
		this.bindToId = Util.parseInt(bindToId, -1);
	}

	public ArrayList<SemanticBindToArgument> getSemanticBindToArguments() {
		return semanticBindToArguments;
	}

	public void setSemanticBindToArguments(
			ArrayList<SemanticBindToArgument> semanticArgument) {
		this.semanticBindToArguments = semanticArgument;
	}

	public SemanticBindToArgument getSemanticBindToArgument(
			String sequence) {
		if (sequence == null || sequence.equals(""))
			return null;
		int sequenceid = Util.parseInt(sequence, -1);
		return getSemanticBindToArgument(sequenceid);
	}

	public SemanticBindToArgument getSemanticBindToArgument(
			int sequence) {
		int size = semanticBindToArguments.size();
		if (sequence < 1)
			return null;
		// sequence 从1开始
		int sequenceidx = sequence - 1;

		if (sequenceidx < size) {
			return semanticBindToArguments.get(sequenceidx);
		} else if (sequenceidx == size) {
			SemanticBindToArgument semanticArgument = new SemanticBindToArgument(sequence);
			semanticBindToArguments.add(semanticArgument);
			return semanticArgument;
		} else {
			for (int i = size; i < sequenceidx; i++) {
				SemanticBindToArgument semanticArgument = new SemanticBindToArgument(i+1);
				semanticBindToArguments.add(semanticArgument);
			}
			SemanticBindToArgument semanticArgument = new SemanticBindToArgument(sequence);
			semanticBindToArguments.add(semanticArgument);
			return semanticArgument;
		}
	}
	
	private int semanticPropsClassNodeId = -1; // 此语义对应的语义属性在semanticPropsMap中的id

	public int getSemanticPropsClassNodeId() {
		return semanticPropsClassNodeId;
	}

	public void setSemanticPropsClassNodeId(int semanticPropsClassNodeId) {
		this.semanticPropsClassNodeId = semanticPropsClassNodeId;
	}
}
