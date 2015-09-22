package com.myhexin.qparser.node;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public final class BoundaryNode extends SemanticNode {

    public BoundaryNode() {
        super("");
        type = NodeType.BOUNDARY;
    }

    public String toString() {
    	String str = "NodeType:"+type+"{";
        StringBuffer buffer = new StringBuffer(str);
        if (isStart()) {
        	buffer.append("start");
        } else if (isEnd()) {
        	buffer.append("end");
        } else {
        	buffer.append("nsne");
        }
        buffer.append(" syntpatternid:"+syntacticPatternId);
        if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
		    SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
		    if(syntPtn!=null && syntPtn.getSemanticBind()!=null)
		    {
		    	String semanticPatternId = syntPtn.getSemanticBind().getId();
		    	String semanticBindRoot = syntPtn.getSemanticBind().getSemanticBindToIds();
			    String temp = semanticPatternId!=null ? semanticPatternId : semanticBindRoot;
			    buffer.append(" semanticpatternid:"+temp);
		    }else{
		    	buffer.append(" semanticpatternid:null");
		    }
		    
        }
        SyntacticPatternExtParseInfo extInfo = getSyntacticPatternExtParseInfo(false);
	    if (extInfo != null) {
	    	buffer.append("["+extInfo.toString()+"]");
	    }
	    buffer.append("}");
        return buffer.toString();
    }
    
    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
    }


    public boolean isStart() {
        return (boundaryType & START) > 0;
    }

    public boolean isEnd() {
        return (boundaryType & END) > 0;
    }

    public void setType(int type, String patternId) {
        type = type & MASK;
        boundaryType |= type;
        syntacticPatternId = patternId;
        if ((boundaryType & END) != 0) {
            this.text = "END";
        } else if ((boundaryType & START) != 0) {
            this.text = "START";
        }
    }

    public void unsetType(int type) {
        type = type & MASK;
        boundaryType &= ~type;
    }

    public String getSyntacticPatternId() {
        return syntacticPatternId;
    }

    public void dump() {
        if (isStart()) {
            System.out.print("START:");
        } else if (isEnd()) {
            System.out.print("END:");
        }
        System.out.println(syntacticPatternId);
        SyntacticPatternExtParseInfo extInfo = getSyntacticPatternExtParseInfo(false);
        if (extInfo != null) {
            extInfo.dump();
        }
    }

    public SyntacticPatternExtParseInfo getSyntacticPatternExtParseInfo(boolean create) {
        if (extInfo == null && create) {
            extInfo = new SyntacticPatternExtParseInfo();
        }
        return extInfo;
    }
    
	public void setSyntacticPatternExtParseInfo(SyntacticPatternExtParseInfo extInfo) {
		this.extInfo = extInfo;
	}

    public int boundaryType = 0;
    public static int START = 0x01;
    public static int END = 0x02;
    public static int MASK = 0x03;
    
    public enum IMPLICIT_PATTERN {
    	KEY_VALUE, 
    	STR_INSTANCE,
    	FREE_VAR
    }
    
    public static IMPLICIT_PATTERN getImplicitPattern(String str)
	{
    	IMPLICIT_PATTERN word = null;
		try {
			word = Enum.valueOf(IMPLICIT_PATTERN.class, str);
			//word = IMPLICIT_PATTERN.valueOf(str);
		} catch (Exception e) {
			word = null;
		}
		return word;
	} 

    public String syntacticPatternId = null;
    public SyntacticPatternExtParseInfo extInfo = null;
    public LogicType contextLogicType = LogicType.AND;
    
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BoundaryNode))
			return false;
		final BoundaryNode bn = (BoundaryNode) obj;
		
		if (this.boundaryType != bn.boundaryType)
			return false;
		if ((this.syntacticPatternId == null && bn.syntacticPatternId != null)
				|| (this.syntacticPatternId != null && !this.syntacticPatternId.equals(bn.syntacticPatternId)))
			return false;
		if ((this.extInfo == null && bn.extInfo != null)
				|| (this.extInfo != null && !this.extInfo.equals(bn.extInfo)))
			return false;
		return true;
    }

    public class SyntacticPatternExtParseInfo { // implements Cloneable

    	
    	/*
    	 * public SyntacticPatternExtParseInfo clone() {
            SyntacticPatternExtParseInfo rtn = null;
            try {
                rtn = (SyntacticPatternExtParseInfo) super.clone();
                rtn.elementNodePostList = new ArrayList<ArrayList<Integer>>();
                for (ArrayList<Integer> sublist : elementNodePostList) {
                    ArrayList<Integer> sublist2 = new ArrayList<Integer>();
                    for (Integer n : sublist) {
                        Integer n2 = new Integer(n.intValue());
                        sublist2.add(n2);
                    }
                    rtn.elementNodePostList.add(sublist2);
                }
                rtn.absentDefalutIndexMap = new HashMap<Integer, SemanticNode>();
                rtn.absentDefalutIndexMap.putAll(this.absentDefalutIndexMap);
                rtn.defaultIndexOfStrNode=(ArrayList<SemanticNode>) this.defaultIndexOfStrNode.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
            return rtn;
        }
    	 */
    	
        public SyntacticPatternExtParseInfo copy() {
            SyntacticPatternExtParseInfo rtn = new SyntacticPatternExtParseInfo();
            
            rtn.absentArgumentCount=absentArgumentCount;
            rtn.absentKeywordCount=absentKeywordCount;
            
            if(elementNodePostList!=null)
            {
            	for (ArrayList<Integer> sublist : elementNodePostList) {
                    ArrayList<Integer> sublist2 = new ArrayList<Integer>();
                    for (Integer n : sublist) {
                        Integer n2 = new Integer(n.intValue());
                        sublist2.add(n2);
                    }
                    rtn.elementNodePostList.add(sublist2);
                }
            }
            
            if(existedIndexNode!=null)
            	rtn.existedIndexNode=NodeUtil.copyNode(existedIndexNode); //.copy();
            
            rtn.existedIndexNodeElementPos=existedIndexNodeElementPos;
            
            rtn.existedIndexNodePos=existedIndexNodePos;
            if(fixedArgumentMap!=null) {
            	Iterator<Integer> it = fixedArgumentMap.keySet().iterator();
            	while(it.hasNext()) {
            		Integer k = it.next();
            		SemanticNode v = fixedArgumentMap.get(k);
            		rtn.fixedArgumentMap.put(new Integer(k), NodeUtil.copyNode(v));
            	}
            }
          
            rtn.implicitArgumentCount=implicitArgumentCount;
            rtn.presentArgumentCount=presentArgumentCount;
            rtn.presentKeywordCount=presentKeywordCount;
            
            if(referToIndexNodeMap!=null){
            	Iterator<Integer> it = referToIndexNodeMap.keySet().iterator();
            	while(it.hasNext()) {
            		Integer k = it.next();
            		SemanticNode v = referToIndexNodeMap.get(k);
            		rtn.referToIndexNodeMap.put(new Integer(k), NodeUtil.copyNode(v));
            	}
            }
            
            if(referToIndexNodePosMap!=null)
            {
            	Iterator<Integer> it = referToIndexNodePosMap.keySet().iterator();
            	while(it.hasNext()) {
            		Integer k = it.next();
            		Integer v = referToIndexNodePosMap.get(k);
            		rtn.referToIndexNodePosMap.put(new Integer(k), new Integer(v));
            	}
            }
            
            if(semanticPropsMap!=null) {
            	Iterator<Integer> it = semanticPropsMap.keySet().iterator();
            	while(it.hasNext()) {
            		Integer k = it.next();
            		SemanticNode v = semanticPropsMap.get(k);
            		rtn.semanticPropsMap.put(new Integer(k), NodeUtil.copyNode(v));
            	}
            }
            

            rtn.absentDefalutIndexMap = new HashMap<Integer, SemanticNode>();
            if(this.absentDefalutIndexMap!=null){
            	Iterator<Integer> it = absentDefalutIndexMap.keySet().iterator();
            	while(it.hasNext()) {
            		Integer k = it.next();
            		SemanticNode v = absentDefalutIndexMap.get(k);
            		rtn.absentDefalutIndexMap.put(new Integer(k), NodeUtil.copyNode(v));
            	}
            }
            
            if(this.defaultIndexOfStrNode!=null) {
            	 rtn.defaultIndexOfStrNode = new ArrayList<SemanticNode>(this.defaultIndexOfStrNode.size());
                 for(SemanticNode node : this.defaultIndexOfStrNode) {
                	 rtn.defaultIndexOfStrNode.add(NodeUtil.copyNode(node));
                 }
            }
           
            
            return rtn;
        }

        private void growElementNodePosListSize(int size) {
            for (int i = elementNodePostList.size(); i < size; i++) {
                Integer n = new Integer(-1);
                ArrayList<Integer> l = new ArrayList<Integer>();
                l.add(n);
                elementNodePostList.add(i, l);
            }
        }

        public void addElementNodePos(int ele, int pos) {
            if (ele <= 0)
                return;
            if (ele > elementNodePostList.size()) {
                growElementNodePosListSize(ele);
            }
            if (pos < 0) {
                Integer n = new Integer(-1);
                ArrayList<Integer> l = new ArrayList<Integer>();
                l.add(n);
                elementNodePostList.set(ele - 1, l);
                return;
            }
            ArrayList<Integer> l = elementNodePostList.get(ele - 1);
            if (l.size() == 1) {
                Integer n = l.get(0);
                if (n == -1) {
                    l.remove(0);
                }
            }
            Integer n = new Integer(pos);
            l.add(n);
        }

        public int getElementNodePos(int ele) {
            if (ele <= 0)
                return -1;
            if (ele > elementNodePostList.size()) {
                return -1;
            }
            ArrayList<Integer> l = elementNodePostList.get(ele - 1);
            if (l.size() == 0)
                return -1;
            Integer n = l.get(0);
            return n.intValue();
        }

        public ArrayList<Integer> getElementNodePosList(int ele) {
            if (ele <= 0)
                return null;
            if (ele > elementNodePostList.size()) {
                return null;
            }
            ArrayList<Integer> l = elementNodePostList.get(ele - 1);
            return l;
        }

        public int getElementNodePosMax() {
            int max = -1;
            ArrayList<Integer> list;
            for (int i = 1; (list = getElementNodePosList(i)) != null; i++) {
                for (Integer n : list) {
                    int t = n.intValue();
                    if (t > max) {
                        max = t;
                    }
                }
            }
            return max;
        }

        public int getElementNodePosMin() {
            int min = -1;
            ArrayList<Integer> list;
            for (int i = 1; (list = getElementNodePosList(i)) != null; i++) {
                for (Integer n : list) {
                    int t = n.intValue();
                    if (t >= 0 && (t < min || min < 0)) {
                        min = t;
                    }
                }
            }
            return min;
        }

        public void offsetElementNodePos(int by) {
            ArrayList<Integer> list;
            for (int i = 1; (list = getElementNodePosList(i)) != null; i++) {
                for (int k = 0; k < list.size(); k++) {
                    Integer n = list.get(k);
                    int t = n.intValue();
                    if (t >= 0) {
                        Integer nn = new Integer(t - by);
                        list.set(k, nn);
                    }
                }
            }
        }
        
        public String toString() {
	    	StringBuffer buffer = new StringBuffer();
	    	ArrayList<Integer> list;
	    	for (int i = 1; (list = getElementNodePosList(i)) != null; i++)
		    {
			    buffer.append("Elem" + i + ":");
			    for (int j = 0; j < list.size(); j ++)
			    {
			    	Integer n = list.get(j);
			    	if (j > 0)
			    	{
			    		buffer.append("、");
			    	}
			    	buffer.append(n.intValue());
			    	if (n.intValue() == -1 && absentDefalutIndexMap.get(i) != null) {
			    		buffer.append(": " + ((FocusNode)absentDefalutIndexMap.get(i)).getIndex());
			    	}
			    }
			    buffer.append(",");
		    }
	    	buffer.append("argCount:" + presentArgumentCount 
	    			+ ",absentArgCount:" + absentArgumentCount 
				    + ",keyCount:" + presentKeywordCount 
				    + ",absentKeyCount:" + absentKeywordCount);
		    return buffer.toString();
	    }

        public void dump() {
            ArrayList<Integer> list;
            for (int i = 1; (list = getElementNodePosList(i)) != null; i++) {
                System.out.print("Element " + i + ":");
                for (Integer n : list) {
                    System.out.print(" " + n.intValue());
                }
                System.out.println("");
            }
            System.out.println("presentArgumentCount:" + presentArgumentCount + ",absentArgumentCount:"
                    + absentArgumentCount + ",implicitArgumentCount:" + implicitArgumentCount + ",presentKeywordCount:"
                    + presentKeywordCount + ",absentKeywordCount:" + absentKeywordCount);
        }

        public int presentArgumentCount = 0;
        public int absentArgumentCount = 0;
        public int implicitArgumentCount = 0;
        public int presentKeywordCount = 0;
        public int absentKeywordCount = 0;
        public ArrayList<ArrayList<Integer>> elementNodePostList = new ArrayList<ArrayList<Integer>>();
        public Map<Integer, SemanticNode> absentDefalutIndexMap = new HashMap<Integer, SemanticNode>();
        public Map<Integer, SemanticNode> fixedArgumentMap = new HashMap<Integer, SemanticNode>();
        public Map<Integer, SemanticNode> semanticPropsMap = new HashMap<Integer, SemanticNode>();
        
        //add by wyh 2015.01.05
        public ArrayList<SemanticNode> defaultIndexOfStrNode = new ArrayList<SemanticNode>();
        
        public SemanticNode existedIndexNode = null;
        public int existedIndexNodeElementPos = -1;
        public int existedIndexNodePos = -1;
        public Map<Integer, SemanticNode> referToIndexNodeMap = new HashMap<Integer, SemanticNode>();
        public Map<Integer, Integer> referToIndexNodePosMap = new HashMap<Integer, Integer>();
        
        /*
         * 比较各属性值是否相等
         * presentArgumentCount 	显式参数个数
         * absentArgumentCount		缺省参数个数
         * implicitArgumentCount	KEY_VALUE、FREE_VAR、STR_INSTANCE参数个数
         * presentKeywordCount		显式关键字个数
         * absentKeywordCount		缺省关键字个数
         * elementNodePostList		句式元素映射列表
         */
        public boolean equals(Object obj) {
        	if (this == obj)
    			return true;
    		if (obj == null)
    			return false;
    		if (!(obj instanceof SyntacticPatternExtParseInfo))
    			return false;
    		final SyntacticPatternExtParseInfo extInfo = (SyntacticPatternExtParseInfo) obj;
    		
    		if (this.presentArgumentCount != extInfo.presentArgumentCount || this.absentArgumentCount != extInfo.absentArgumentCount
    				|| this.implicitArgumentCount != extInfo.implicitArgumentCount
    				|| this.presentKeywordCount != extInfo.presentKeywordCount || this.absentKeywordCount != extInfo.absentKeywordCount)
    			return false;
    		if (this.elementNodePostList.size() != extInfo.elementNodePostList.size())
    			return false;
    		for (int i =1; i < this.elementNodePostList.size(); i++) {
    			ArrayList<Integer> list1 = this.elementNodePostList.get(i);
    			ArrayList<Integer> list2 = extInfo.elementNodePostList.get(i);
    			if (!list1.equals(list2))
    				return false;
    		}
    		return true;
        }
    }
    

    /*public BoundaryNode clone() {
        BoundaryNode rtn = null;
        rtn = (BoundaryNode) super.clone();
        if (extInfo == null)
            rtn.extInfo = null;
        else
            rtn.extInfo = (SyntacticPatternExtParseInfo) extInfo.clone();
        return rtn;
    }*/

	@Override
	protected BoundaryNode copy() {
		BoundaryNode rtn = new BoundaryNode();
		//参考clone,其余不做处理
		rtn.boundaryType=boundaryType;
		rtn.contextLogicType=contextLogicType;
		if (extInfo == null)
            rtn.extInfo = null;
        else
            rtn.extInfo = (SyntacticPatternExtParseInfo) extInfo.copy();
		rtn.bindBoundaryInfos = bindBoundaryInfos;
		rtn.isBindtoSyntactic = isBindtoSyntactic;
		rtn.syntacticPatternId=syntacticPatternId;
		super.copy(rtn);
		return rtn;
	}
	
	//liuxiaofeng 2015/6/24
	//句式之间绑定信息
	private BoundaryInfos bindBoundaryInfos; //绑定到此句式的BoundaryInfos
	private boolean isBindtoSyntactic = false;

	public BoundaryInfos getBindBoundaryInfos() {
		return bindBoundaryInfos;
	}

	public void setBindBoundaryInfos(BoundaryInfos bindBoundaryInfos) {
		this.bindBoundaryInfos = bindBoundaryInfos;
	}

	public boolean isBindtoSyntactic() {
		return isBindtoSyntactic;
	}

	public void setBindtoSyntactic(boolean isBindtoSyntactic) {
		this.isBindtoSyntactic = isBindtoSyntactic;
	}
	
	
	
}
