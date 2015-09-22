package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

public final class FocusNode extends SemanticNode {
    
    private ClassNodeFacade index = null;
    
    public FocusNode() {}
    
    public FocusNode(String text) throws UnexpectedException {
        super(text);
        type = NodeType.FOCUS;
        //FocusNode不再只针对指标或指标别名节点
        //index = MemOnto.getOnto(text, ClassNodeFacade.class, Query.Type.STOCK);
    }
    
    /**
     * get the index
     * 
     * @author zd<zhangdong@myhexin.com>
     * @return
     */
    public ClassNodeFacade getIndex() {
        return this.index;
    }
    
    public void setIndex(ClassNodeFacade cn) {
    	this.index = cn;
    	/*if (cn == null)
    		this.index = null;
    	else
    		this.index = (ClassNodeFacade)NodeUtil.copyNode(cn); //.copy();
    	*/ 
   }
    
    public void setIndex(ClassNodeFacade cn, boolean propsCopy) {
    	if (propsCopy == true) {
    		//ClassNodeFacade from = this.index; //(ClassNodeFacade)NodeUtil.copyNode(this.index); //.copy();
    		//ClassNodeFacade to = (ClassNodeFacade)NodeUtil.copyNode(cn); //.copy();
    		copyProps(this.index, cn);
    		this.index = cn;
    	} else {
    		setIndex(cn);
    	}
    }
    
    public void copyProps(ClassNodeFacade from, ClassNodeFacade to) {
    	if (from == null || to == null)
            return;
        List<PropNodeFacade> fromPropList = from.getAllProps();
        List<PropNodeFacade> toPropList = to.getAllProps();
        if (fromPropList == null || toPropList == null)
            return;
        for (PropNodeFacade fromPn : fromPropList) {
            if (fromPn.getValue() != null) {
            	boolean isDiffProp = true;
                for (PropNodeFacade toPn : toPropList) {
                	if (fromPn.getText().equals(toPn.getText())) {
                		toPn.setValue(fromPn.getValue());
                		isDiffProp = false;
                	}
                }
                if (isDiffProp) {
                	for (PropNodeFacade toPn : toPropList) {
                    	if (fromPn.valueTypeIsMatchOf(toPn)) {
                    		toPn.setValue(fromPn.getValue());
    	                    fromPn.getValue().setIsBoundToIndex(true);
    	                    fromPn.getValue().setBoundToIndexProp(to,toPn);
                    	}
                    }
                }
            }
        }
    }
    
    public StrNode getString() {
    	for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == Type.STRING) {
                return item.str;
            }
        }
    	return null;
    }
    
    public LogicNode getLogic() {
    	for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == Type.LOGIC) {
                return item.logic;
            }
        }
    	return null;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
    }

    /*@Override
    public FocusNode clone() {
        FocusNode rtn = null;
        rtn = (FocusNode) super.clone();
        rtn.focusList = new ArrayList<FocusItem>();
        for (FocusItem item : focusList) {
            rtn.focusList.add(item.clone());
        }
        if (index != null) {
            rtn.index = index.clone();
        }
        return rtn;
    }*/
    
    protected FocusNode copy() {
    	//focusNode
    	FocusNode rtn = new FocusNode();
    	if(focusList!=null) {
    		for(FocusItem  item : focusList) {
    			rtn.addFocusItem(item.copy());
    		}
    	}
    	if(index!=null)
    	{	
    		rtn.index = (ClassNodeFacade) NodeUtil.copyNode(index); //.copy(); //ClassNodeFacade
    	}
    	rtn.indexCount = indexCount; // int
    	rtn.keywordCount = keywordCount; // int
    	rtn.logicCount = logicCount; // int
    	rtn.stringCount = stringCount; // int
    	
    	//semanticNode
    	super.copy(rtn);
    	return rtn;
    }

	public FocusItem addFocusItem(FocusNode.Type type, String content) {
        // 如果该指标已放入focusList_
    	for (FocusItem item : focusList) {
        	if (type == Type.INDEX && item.content.equals(content))
        		return null;
        }
    	FocusItem item = new FocusItem(type, content);
        focusList.add(item);
        if (type == Type.INDEX) {
            indexCount++;
        } else if (type == Type.KEYWORD) {
            keywordCount++;
        } else if (type == Type.STRING) {
        	stringCount++;
        } else if (type == Type.LOGIC) {
        	logicCount++;
        }
        return item;
    }
    
    
    public FocusItem addFocusItem(FocusItem newItem) {
        // 如果该指标已放入focusList_
    	for (FocusItem item : focusList) {
        	if (item.type == Type.INDEX && item.index!=null && item.index.equals(newItem.index))
        		return null;
        	else if (item.type == Type.STRING && item.str!=null && item.str.equals(newItem.index))
        		return null;
        	else if (item.type == Type.LOGIC && item.logic!=null && item.logic.equals(newItem.index))
        		return null;
        }
        focusList.add(newItem);
        if (newItem.type == Type.INDEX) {
            indexCount++;
        } else if (newItem.type == Type.KEYWORD) {
            keywordCount++;
        } else if (newItem.type == Type.STRING) {
        	stringCount++;
        }
        return newItem;
    }
    
    public FocusItem addFocusItem(FocusNode.Type type, String content, SemanticNode sn) throws UnexpectedException {
        // 如果该指标已放入focusList_
    	for (FocusItem item : focusList) {
        	if (type == Type.INDEX && item.index.equals(sn))
        		return null;
        	else if (type == Type.STRING && item.str.equals(sn))
        		return null;
        	else if (type == Type.LOGIC && item.logic.equals(sn))
        		return null;
        }
    	FocusItem item = new FocusItem(type, content, sn);
        focusList.add(item);
        if (type == Type.INDEX) {
            indexCount++;
        } else if (type == Type.KEYWORD) {
            keywordCount++;
        } else if (type == Type.STRING) {
        	stringCount++;
        }
        return item;
    }

    /**
     * 真删除FocusItem
     * @param type
     * @param content
     * @return
     * @deprecated
     */
    public boolean removeFocusItem(FocusNode.Type type, String content) {
        for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == type && item.content.equals(content)) {
                if (type == Type.INDEX)
                    indexCount--;
                else if (type == Type.KEYWORD)
                    keywordCount--;
                else if (type == Type.STRING)
                	stringCount--;
                focusList.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 真删除FocusItem
     * @param type
     * @param content
     * @param fi
     * @return
     */
    public boolean removeFocusItem(FocusNode.Type type, String content, FocusItem fi) {
        for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == type) {
                if (type == Type.INDEX && item.index.equals(fi.index)) {
                    indexCount--;
                    focusList.remove(i);
                    return true;
                } else if (type == Type.STRING && item.str.equals(fi.str)) {
                	stringCount--;
                	focusList.remove(i);
                	return true;
                } else if (type == Type.LOGIC && item.logic.equals(fi.str)) {
                	logicCount--;
                	focusList.remove(i);
                	return true;
                } else if (type == Type.KEYWORD && item.content.equals(content)) {
                	keywordCount--;
                	focusList.remove(i);
                	return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 将FocusItem标记为删除
     * @param type
     * @param content
     * @return
     * @deprecated
     */
    public boolean markFocusItemCanDelete(FocusNode.Type type, String content) {
        for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == type && item.content.equals(content)) {
            	/*
                if (type == Type.INDEX)
                    indexCount--;
                else if (type == Type.KEYWORD)
                    keywordCount--;
                */
                focusList.get(i).canDelete = true;
                return true;
            }
        }
        return false;
    }
    
    /**
     * 将FocusItem标记为删除
     * @param type
     * @param content
     * @return
     */
    public boolean markFocusItemCanDelete(FocusNode.Type type, String content, FocusItem fi) {
        for (int i = 0; i < focusList.size(); i++) {
            FocusItem item = focusList.get(i);
            if (item.type == type) {
                if (type == Type.INDEX && item.index.equals(fi.index)) {
                	focusList.get(i).canDelete = true;
                    return true;
                } else if (type == Type.STRING && item.str.equals(fi.str)) {
                	focusList.get(i).canDelete = true;
                	return true;
                } else if (type == Type.LOGIC && item.logic.equals(fi.str)) {
                	focusList.get(i).canDelete = true;
                	return true;
                } else if (type == Type.KEYWORD && item.content.equals(content)) {
                	focusList.get(i).canDelete = true;
                	return true;
                }
            }
        }
        return false;
    }

    public ArrayList<FocusItem> getFocusItemList() {
        return focusList;
    }

    public void reset() {
        focusList = new ArrayList<FocusItem>();
        indexCount = 0;
        keywordCount = 0;
        stringCount = 0;
        logicCount = 0;
    }

    public boolean hasIndex() {
        return indexCount > 0;
    }

    public boolean hasKeyword() {
        return keywordCount > 0;
    }
    
    public boolean hasString() {
        return stringCount > 0;
    }
    
    public boolean hasLogic() {
    	return logicCount > 0;
    }

    public ArrayList<FocusItem> focusList = new ArrayList<FocusItem>();
    public int indexCount = 0;
    public int keywordCount = 0;
    public int stringCount = 0;
    public int logicCount = 0;

	public static enum Type {
        INDEX, KEYWORD, STRING, LOGIC
    }

    public String toString() {
        //String str = super.toString();
        StringBuilder str = new StringBuilder();
        String str1 = super.toString();
        str.append(str1);
        if(focusList!=null) {
        	int kc = 0;
        	for(FocusItem item : focusList) {
        		if(item.type == Type.KEYWORD) {
        			kc ++;
        			if(kc==6) {
        				str.append("...").append(focusList.size()-5).append("...");
        				//str += "..." + (focusList.size()-5) + "...";
        				continue;
        			}else if(kc>7) {
        				continue;
        			}
        			str.append(",").append(item.toString());
        			//str += "," + item.toString();
        			
        		}else{
        			str.append(",").append(item.toString());
        			//str += "," + item.toString();
        		}
        		
        	}
        }
        return str.toString();
    }
    
    /*
     * 比较各属性值是否相等
     * text			内容
     * index		指标
     * focusList	元素
     * 
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FocusNode))
			return false;
		final FocusNode fn = (FocusNode) obj;
		
		if (!this.text.equals(fn.text))
			return false;
		if ((this.index == null && fn.index != null)
				|| (this.index != null && !this.index.equals(fn.index)))
			return false;
		if (this.focusList.size() != fn.focusList.size())
			return false;
		for (FocusItem fi : this.focusList)
			if (!fn.focusList.contains(fi))
				return false;
		
		return true;
    }
    
    public class FocusItem {
    	
    	private FocusItem() {}
    	
		public FocusItem(Type t, String s) {
            type = t;
            content = s;
        }
        
		public FocusItem(Type t, String s, SemanticNode sn) {
            type = t;
            content = s;
            if (t == Type.INDEX) {
            	index = (ClassNodeFacade) sn; 
            } else if (t == Type.STRING) {
            	str = (StrNode) sn;
            } else if (t == Type.LOGIC) {
            	logic = (LogicNode) sn;
            }
        }

        public FocusItem copy() {
            FocusItem newItem = new FocusItem();
            newItem.canDelete=canDelete;
            newItem.content=content;
            newItem.score=score;
            if(signals!=null) {
            	 newItem.signals=new HashMap<String, Double>();
            	 newItem.signals.putAll(signals);
            }
           
            newItem.signalsScore=signalsScore;
            newItem.type=type;

            
            if(this.index != null)
            	newItem.index = (ClassNodeFacade) NodeUtil.copyNode(this.index); //.copy();
            if(this.str != null)
            	newItem.str = (StrNode) NodeUtil.copyNode(this.str); //.copy();
            if(this.logic != null)
            	newItem.logic = (LogicNode)NodeUtil.copyNode(this.logic);//.copy();
            return newItem;
        }

        private Type type;
        private String content;
        private ClassNodeFacade index = null;
        private StrNode str = null;
        private LogicNode logic = null;
        private double score = 0;
        private HashMap<String, Double> signals = null;
        private double signalsScore = 0;

        private boolean canDelete = false; // 在歧义识别中，标记是否可被删除

        public void setScore(double score) {
        	this.score = score;
        }
        public void setLogic(LogicNode node ) {
        	this.logic = node;
        }
        
        public void setStr(StrNode node) {
        	this.str = node;
        }
        
        public String toString() {
            String strtype = "";
            String strdomain = "";
            if(type == Type.KEYWORD) {
                strtype = "KEY";
            } else if(type == Type.INDEX) {
                strtype = "IDX";
                strdomain = ":"+index.getDomains();
            } else if(type == Type.STRING) {
                strtype = "STR";
            } else if(type == Type.LOGIC) {
            	strtype = "LOGIC";
            }
            return String.format("%s:%s%s", strtype, content, strdomain);
        }
        
        /*
         * 比较各属性值是否相等
         * type			内容
         * isCombined	是否被合并
         * isBound		是否被绑定
         * props		属性是否相同
         */
        public boolean equals(Object obj) {
        	if (this == obj)
    			return true;
    		if (obj == null)
    			return false;
    		if (!(obj instanceof FocusItem))
    			return false;
    		final FocusItem fi = (FocusItem) obj;
    		
    		if (this.type != fi.type)
    			return false;
    		if ((this.content == null && fi.content != null)
    				||  (this.content != null && !this.content.equals(fi.content)))
    			return false;
    		
    		return true;
        }

		public Type getType() {
			return type;
		}

		public String getContent() {
			return content;
		}

		public ClassNodeFacade getIndex() {
			return index;
		}

		public StrNode getStr() {
			return str;
		}

		public LogicNode getLogic() {
			return logic;
		}

		public double getScore() {
			return score;
		}

		public HashMap<String, Double> getSignals() {
			return signals;
		}

		public double getSignalsScore() {
			return signalsScore;
		}

		public boolean isCanDelete() {
			return canDelete;
		}
        
        
    }

	public Unit getPropUnit() {
		Unit unit = Unit.UNKNOWN;
		ClassNodeFacade classNode = this.getIndex();
		if (classNode != null) {
			PropNodeFacade propNodeFacade = classNode.getPropOfValue();
			if (propNodeFacade != null) {
				unit = propNodeFacade.getUnitByOfWhat(classNode);
			}
		}
		return unit;
	}
}
