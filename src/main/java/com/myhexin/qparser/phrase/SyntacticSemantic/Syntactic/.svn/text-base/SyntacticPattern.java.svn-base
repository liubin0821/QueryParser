package com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic;

import java.util.ArrayList;

import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoFacadeUtil;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_syntactic.xml
 * SyntacticPattern
 */
public class SyntacticPattern {

    public SyntacticPattern(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public SyntacticElement getSyntacticElement(String sequence) {
        if(sequence == null || sequence.equals(""))
            return null;
        int sequenceid = Util.parseInt(sequence, -1);
        return getSyntacticElement(sequenceid);
    }

    public SyntacticElement getSyntacticElement(int sequence) {
        int size = syntacticElements.size();
        if(sequence < 1)
            return null;
        // sequence 从1开始
        int sequenceidx = sequence-1;

        if(sequenceidx < size) {
            return syntacticElements.get(sequenceidx);
        } else if(sequenceidx == size) {
            SyntacticElement syntacticElement = new SyntacticElement(sequence);
            syntacticElements.add(syntacticElement);
            return syntacticElement;
        } else {
            // TODO 必须顺序加入,是否抛出异常
            return null;
        }
    }
    
    public int getSyntacticElementMax() {
        return syntacticElements.size()+1;
    }

    public SemanticBind getSemanticBind() {
        return semanticBind;
    }

    public void addModifier(String modifier) {
        // TODO skip duplicate modifier
        this.modifier.add(modifier);
    }

    public ArrayList<String> getModifier() {
        return modifier;
    }

    private String id = null; // 句式id
    private String description = null; // 句式描述
    private ArrayList<SyntacticElement> syntacticElements = new ArrayList<SyntacticElement>(); // 句式元素
    private SemanticBind semanticBind = new SemanticBind(); // 语义绑定
    private ArrayList<String> modifier = new ArrayList<String>(); // ?
    
    private ArrayList<SyntacticElement> fixedArguments = new ArrayList<SyntacticElement>(); // 固定值参数
    
    public SyntacticElement getFixedArgument() {
        int sequenceid = fixedArguments.size()+1;
        return getFixedArgument(sequenceid);
    }

    public SyntacticElement getFixedArgument(int sequence) {
        int size = fixedArguments.size();
        if(sequence < 1)
            return null;
        // sequence 从1开始
        int sequenceidx = sequence-1;

        if(sequenceidx < size) {
            return fixedArguments.get(sequenceidx);
        } else if(sequenceidx == size) {
            SyntacticElement syntacticElement = new SyntacticElement(sequence);
            fixedArguments.add(syntacticElement);
            return syntacticElement;
        } else { 
        	// 必须顺序加入
            return null;
        }
    }
    
    public int getFixedArgumentMax() {
        return fixedArguments.size()+1;
    }
    
    private ArrayList<ClassNodeFacade> semanticPropsClassNodes = new ArrayList<ClassNodeFacade>(); // 各语义属性
	private String syntacticType;
    
    public ClassNodeFacade getSemanticPropsClassNode() {
        int sequenceid = semanticPropsClassNodes.size()+1;
        return getSemanticPropsClassNode(sequenceid);
    }

    public ClassNodeFacade getSemanticPropsClassNode(int sequence) {
        int size = semanticPropsClassNodes.size();
        if(sequence < 1)
            return null;
        // sequence 从1开始
        int sequenceidx = sequence-1;

        if(sequenceidx < size) {
            return semanticPropsClassNodes.get(sequenceidx);
        } else if(sequenceidx == size) {
        	ClassNodeFacade ClassNodeFacade = new ClassNodeFacade();
            semanticPropsClassNodes.add(ClassNodeFacade);
            return ClassNodeFacade;
        } else { 
        	// 必须顺序加入
            return null;
        }
    }
    
    public void addSemanticPropsClassNode(ClassNodeFacade ClassNodeFacade) {
    	semanticPropsClassNodes.add( OntoFacadeUtil.copy(ClassNodeFacade)); //.copy());
    }
    
    public int getSemanticPropsClassNodeMax() {
        return semanticPropsClassNodes.size()+1;
    }

	public void setSyntacticType(String syntacticType) {
		this.syntacticType = syntacticType;
	}
    
	public String getSyntacticType() {
		return syntacticType;
	}
}
