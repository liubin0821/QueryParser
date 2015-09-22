package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import com.myhexin.qparser.onto.ClassNodeFacade;

/**
 * stock_phrase_semantic.xml
 * SemanticPattern
 */
public class SemanticPattern {

	public SemanticPattern() {
		super();
	}

    public SemanticPattern(String id) {
        this.id = id;
    }
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isKeyValue() {
		return isKeyValue;
	}

	public void setKeyValue(boolean isKeyValue) {
		this.isKeyValue = isKeyValue;
	}
	
	public void setKeyValue(String isKeyValue) {
		if (isKeyValue == null || isKeyValue.equals(""))
			return ;
		isKeyValue = isKeyValue.toLowerCase();
		if (isKeyValue.equals("false"))
			this.isKeyValue = false;
		else if (isKeyValue.equals("true"))
			this.isKeyValue = true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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
		this.chineseRepresentation = chineseRepresentation;
	}

	public SemanticArguments getSemanticArguments() {
		return semanticArguments;
	}

	public void setSemanticArguments(SemanticArguments semanticArguments) {
		this.semanticArguments = semanticArguments;
	}

	public SemanticArgumentDependency getSemanticArgumentDependency() {
		return semanticArgumentDependency;
	}

	public void setSemanticArgumentDependency(
			SemanticArgumentDependency semanticArgumentDependency) {
		this.semanticArgumentDependency = semanticArgumentDependency;
	}

	public SemanticArgument getSemanticArgument(String id, boolean create)
    {
        return semanticArguments.getSemanticArgument(id, create);
    }

    public SemanticArgument getSemanticArgument(int id, boolean create)
    {
        return semanticArguments.getSemanticArgument(id, create);
    }

    public int getSemanticArgumentCount()
    {
	    return semanticArguments.getSemanticArgumentsCount();
    }

    private String id = null; // 语义编号
    private boolean isKeyValue = true;
    private String description = null; // 描述，注释，任意字符串
    private String uiRepresentation = ""; // 在维护工具中显示给用户的表示方式
    private String chineseRepresentation = ""; // 中文自然语言表述方式，用于生成候选问句给用户
    private SemanticArguments semanticArguments = new SemanticArguments(); // 
    private SemanticArgumentDependency semanticArgumentDependency = new SemanticArgumentDependency(); // 
    
    private SemanticArgument result = new SemanticArgument();
    
    public SemanticArgument getResult() {
		return result;
	}

	public void setResult(SemanticArgument result) {
		this.result = result;
	}

	// 用于存储语义属性
    private ClassNodeFacade propsClassNode = new ClassNodeFacade();

	public ClassNodeFacade getPropsClassNode() {
		return propsClassNode;
	}

	public void setPropsClassNode(ClassNodeFacade propsClassNode) {
		this.propsClassNode = propsClassNode;
	}
}
