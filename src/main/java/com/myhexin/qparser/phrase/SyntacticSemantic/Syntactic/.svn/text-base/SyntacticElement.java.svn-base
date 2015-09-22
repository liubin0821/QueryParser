package com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic;

import java.util.List;

import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.IndexGroupMap;
import com.myhexin.qparser.util.Util;

/**
 * stock_phrase_syntactic.xml
 * SyntacticPattern/SyntacticElement
 */
public class SyntacticElement {

    public SyntacticElement(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public void setType(String type)
    {
        if(type == null || type.equals(""))
            return;
        type = type.toLowerCase();
        if(type.equals("argument")) {
            this.type = SyntElemType.ARGUMENT;
        } else if(type.equals("index") || type.equals("indexlist") || type.equals("constant") || type.equals("constantlist")) {
        	this.type = SyntElemType.ARGUMENT;
        } else if(type.equals("keyword")) {
            this.type = SyntElemType.KEYWORD;
        }
    }

    public void setType(SyntElemType type) {
        this.type = type;
    }

    public SyntElemType getType() {
        return type;
    }

    public void setSemanticBind(String semanticBind) {
        if(semanticBind == null || semanticBind.equals(""))
            return;
        int semanticBindId = Util.parseInt(semanticBind, -1);
        setSemanticBind(semanticBindId);
    }

    public void setSemanticBind(int semanticBind) {
        if(semanticBind <=0 )
            semanticBind = -1;
        else
            this.semanticBind = semanticBind;
    }

    public int getSemanticBind() {
        return semanticBind;
    }

    public void setKeyword(String keyword) {
    	if (keyword == null || keyword.equals(""))
    		return;
    	this.keyword = keyword;
    }
    
    public String getKeyword() {
        return keyword;
    }

    public void setKeywordGroup(String keywordGroup) {
    	if (keywordGroup == null || keywordGroup.equals(""))
    		return;
    	this.keywordGroup = keywordGroup;
    }

    public String getKeywordGroup() {
        return keywordGroup;
    }

    public void setCanAbsent(String canAbsent) {
        boolean bCanAbsent = false;
        if(canAbsent != null && canAbsent.equals("true")) {
            bCanAbsent = true;
        }
        setCanAbsent(bCanAbsent);
    }
    
    public void setCanAbsent(boolean canAbsent) {
        this.canAbsent = canAbsent;
    }
    
    public boolean getCanAbsent() {
        return canAbsent;
    }

    public static enum SyntElemType {
        ARGUMENT,
        KEYWORD
    }

    private int sequence = -1;
    private SyntElemType type = null;
    private int semanticBind = -1; // 新配置不需要此字段
    private String keyword = null;
    private String keywordGroup = null;
    private boolean canAbsent = false;
    private boolean shouldIgnore= false;//是否可忽略，如“2012年大股东增持”的“大股东”
    
    private SemanticArgument argument = new SemanticArgument(); // 新配置新增字段
    private boolean isDefaultIndexSet = false;
    private boolean isDefaultValueSet = false;
    
	private ValueType syntElemValueType= null;
	private String syntElemSubType= null;

	
	public ValueType getSyntElemValueType() {
    	return syntElemValueType;
    }

	public void setSyntElemValueType(String syntElemValueType) {
		if(syntElemValueType==null)
			return ;
		try{
			ValueType temp=ValueType.valueOf(syntElemValueType.toUpperCase());
	    	this.syntElemValueType = temp;
		}catch (Exception e) {}
    }

	public String getSyntElemSubType() {
    	return syntElemSubType;
    }

	public void setSyntElemSubType(String syntElemSubType) {
		this.syntElemSubType = syntElemSubType;
    } 
 
    
	public SemanticArgument getArgument() {
		return argument;
	}

	public void setArgument(SemanticArgument argument) {
		this.argument = argument;
	}

	public SemanticArgument.SemanArgType getArgumentType() {
		return argument.getType();
	}

	public void setArgumentType(SemanticArgument.SemanArgType argumentType) {
		argument.setType(argumentType);
	}
	
	// merge操作
	// 默认：Argument.Type.INDEX?
	// INDEX, INDEXLIST, CONSTANT, ANY
	public boolean mergeArgumentType(SemanticArgument.SemanArgType argumentType) {
		if (argument.getType() == null && argumentType != null) {
			argument.setType(argumentType);
			return true;
		} else if (argument.getType() != argumentType) {
			return false;
		}
		return true;
	}
	
	public void setArgumentType(String argumentType) {
		if (argumentType == null || argumentType.equals("") || argumentType.equals("keyword"))
			return;
		argument.setType(SemanticArgument.getTypeFromStr(argumentType));
	}

	public String getSpecificIndex() {
		return argument.getSpecificIndex();
	}

	public void setSpecificIndex(String specificIndex) {
		if (specificIndex == null || specificIndex.equals(""))
			return;
		argument.setSpecificIndex(specificIndex);
	}
	
	// merge操作
	// 默认：null
	public boolean mergeSpecificIndex(String specificIndex) {
		if (argument.getSpecificIndex() == null) {
			argument.setSpecificIndex(specificIndex);
			return true;
		} else if (specificIndex == null) {
			return true;
		} else if (argument.getSpecificIndex().equals(specificIndex)) {
			return true;
		}
		return false;
	}

	public String getSpecificIndexGroup() {
		return argument.getSpecificIndexGroup();
	}
	
	// merge操作
	// 默认：null
	public boolean mergeSpecificIndexGroup(String specificIndexGroup, IndexGroupMap indexGroupMap) {
		if (argument.getSpecificIndexGroup() == null) {
			argument.setSpecificIndexGroup(specificIndexGroup);
			return true;
		} else if (specificIndexGroup == null) {
			return true;
		} else if (argument.getSpecificIndexGroup().equals(specificIndexGroup)) {
			return true;
		}
		String specificIndexGroupId = indexGroupMap.intersectionOfIndexGroupAB(argument.getSpecificIndexGroup(), specificIndexGroup);
		argument.setSpecificIndexGroup(specificIndexGroupId);
		if (specificIndexGroupId!=null)
			return true;
		return false;
	}
	
	public void setSpecificIndexGroup(String specificIndexGroup) {
		if (specificIndexGroup == null || specificIndexGroup.equals(""))
			return;
		argument.setSpecificIndexGroup(specificIndexGroup);
	}

	public List<ValueType> getValueType() {
		return argument.getAllAcceptValueTypes();
	}

	public void setValueType(List<ValueType> valueTypes) {
		argument.setValueType(valueTypes);
	}
	
	// merge操作
	// 默认：ValueType.UNDEFINED
	// UNDEFINED, STRING, DATE, FIGURE, NUMBER, LONG_NUM, DOUBLE_NUM, RATIO, PERCENTAGE, TIMES
	// 注意父子关系的判断
	public boolean mergeValueType(List<ValueType> valueTypes) {
		if (argument.isContainsValueType(ValueType.UNDEFINED)) {
			argument.setValueType(valueTypes);
			return true;
		} else if (valueTypes != null && valueTypes.size() > 0 && valueTypes.get(0) == ValueType.UNDEFINED) {
			return true;
		} else if (argument.isSameValueType(valueTypes)) {
			return true;
		}
		return false;
	}
	
	public void setValueType(String valueType) {
		if (valueType == null || valueType.equals(""))
			return;
		argument.setValueType(SemanticArgument.getValueTypeFromStr(valueType));
	}
	
	public int getListElementMinCount() {
		return argument.getListElementMinCount();
	}
	
	public void setListElementMinCount(String listElementMinCount) {
		if (listElementMinCount == null || listElementMinCount.equals(""))
			return;
		setListElementMinCount(Util.parseInt(listElementMinCount, 2));
	}

	public void setListElementMinCount(int listElementMinCount) {
		if (listElementMinCount < 2)
			listElementMinCount = 2;
		argument.setListElementMinCount(listElementMinCount);
	}
	
	public boolean mergeListElementMinCount(int listElementMinCount) {
		if (argument.getListElementMinCount() < listElementMinCount) {
			argument.setListElementMinCount(listElementMinCount);
		}
		return true;
	}
	
	public String getDefaultIndex() {
		return argument.getDefaultIndex();
	}

	public void setDefaultIndex(String defaultIndex) {
		if (defaultIndex == null || defaultIndex.equals(""))
			return;
		argument.setDefaultIndex(defaultIndex);
		isDefaultIndexSet = true;
	}
	
	// merge操作
	// 默认：null
	public boolean mergeDefaultIndex(String defaultIndex) {
		if (argument.getDefaultIndex() == null) {
			argument.setDefaultIndex(defaultIndex);
			return true;
		} else if (defaultIndex == null) {
			return true;
		} else if (argument.getDefaultIndex().equals(defaultIndex)) {
			return true;
		} else if (isDefaultIndexSet) {
			return true;
		}
		return false;
	}

	public String getDefaultValue() {
		return argument.getDefaultValue();
	}

	public void setDefaultValue(String defaultValue) {
		if (defaultValue == null || defaultValue.equals(""))
			return;
		argument.setDefaultValue(defaultValue);
		isDefaultValueSet = true;
	}
	
	// merge操作
	// 默认：null
	public boolean mergeDefaultValue(String defaultValue) {
		if (argument.getDefaultValue() == null) {
			argument.setDefaultValue(defaultValue);
			return true;
		} else if (defaultValue == null) {
			return true;
		} else if (argument.getDefaultValue().equals(defaultValue)) {
			return true;
		} else if (isDefaultValueSet) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("sequence: ").append(sequence).append(";");
		sb.append("type: ").append(type).append(";");
		sb.append("keyword: ").append(keyword).append(";");
		sb.append("keywordGroup: ").append(keywordGroup).append(";");
		sb.append("canAbsent: ").append(canAbsent).append(";");
		sb.append("argument: [").append(argument).append("];");
		return sb.toString();
	}

	public boolean isShouldIgnore() {
		return shouldIgnore;
	}

	public void setShouldIgnore(boolean shouldIgnore) {
		this.shouldIgnore = shouldIgnore;
	}

	public void setShouldIgnore(String shouldIgnore) {
		boolean bShouldIgnore = false;
		if (shouldIgnore != null && shouldIgnore.equals("true")) {
			bShouldIgnore = true;
		}
		setShouldIgnore(bShouldIgnore);
	}
}
