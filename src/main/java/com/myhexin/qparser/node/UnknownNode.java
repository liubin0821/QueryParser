package com.myhexin.qparser.node;

import java.util.EnumSet;
import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;

/**
 * 本语义节点代表所有语义角色未知的词。这些词中，有些是系统认识的，大部分是不认识的。
 * 所有系统认识的词在{@link SpecialWords}中定义。未定义的，则是真正的未识别词。
 * 注意：此类词允许有多重类型。
 */
public final class UnknownNode extends SemanticNode {
 
    public UnknownNode(String text) {
        super(text);
        type = NodeType.UNKNOWN;
        for(SpecialWordType type : SpecialWords.getTypesOf(text)) {
            addSpecialType(type);
        }
    }
    
    private UnknownNode() {}
    
    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) 
        throws BadDictException{
    }
    
    /**
     * 在系统的某些阶段，此类词可能被识别了，可调用此方法设置其类型。
     * @param type 要添加的类型
     */
    public void addSpecialType(SpecialWordType type){
    	if(type == null){
    		return ;
    	}
    	specialTypes.remove(SpecialWordType.UNKNOWN);
    	specialTypes.add(type);
    }
    
    public boolean isTypeOf(SpecialWordType type) {
        return specialTypes.contains(type);
    }
    
    /**
     * 是否真的完全不认识
     */
    public boolean isRealUnknown(){
    	return isTypeOf(SpecialWordType.UNKNOWN);
    }

    public boolean isTag = false;
    public boolean isFromChange = false;
    private EnumSet<SpecialWordType> specialTypes = EnumSet.of(SpecialWordType.UNKNOWN);
    
    /*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnknownNode))
			return false;
		final UnknownNode un = (UnknownNode) obj;
		
		if (!this.text.equals(un.text))
			return false;
		if (this.isCombined != un.isCombined)
			return false;
		if (this.isBoundToIndex() != un.isBoundToIndex())
			return false;
		return true;
    }

	@Override
	protected SemanticNode copy() {
		UnknownNode rtn = new UnknownNode();
		rtn.isFromChange=isFromChange;
		rtn.isTag=isTag;
		rtn.specialTypes=specialTypes;
		super.copy(rtn);
		return rtn;
	}
}
