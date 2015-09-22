package com.myhexin.qparser.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.SortValueType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public final class SortNode extends SemanticNode {
	@Override
	protected SortNode copy() {
		SortNode rtn = new SortNode(super.text);
		rtn.changedByChange=changedByChange;
		rtn.descending_=descending_;
		rtn.groupBy_=groupBy_;
		rtn.groupByInfo_=groupByInfo_;
		rtn.isTopK_=isTopK_;
		rtn.k_=k_;
		rtn.valueType_=valueType_;

		super.copy(rtn);
		return rtn;
	}
	
    public SortNode(String text) {
        super(text);
        type = NodeType.SORT;
    }

    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException{
        boolean hasAllNeed = k2v.size() == 4 && k2v.containsKey("valueType")
                && k2v.containsKey("descending") 
                && k2v.containsKey("isTopK")
                && k2v.containsKey("k");
        if(!hasAllNeed){
            throw new BadDictException("Sort词典信息或格式错误",
                    NodeType.SORT, text);
        }
        String valueType = k2v.get("valueType");
        String descending = k2v.get("descending");
        String isTopK = k2v.get("isTopK");
        String k = k2v.get("k");
        if (valueType.equals("date")) {
            valueType_ = SortValueType.DATE;
        }
        
        setDescending_(descending.equals("true"));
        isTopK_ = isTopK.equals("true");
        k_ = Double.valueOf(k);
        
        String gbinfoStr = k2v.get("groupbyinfo");
        if(gbinfoStr==null){
            return;
        }
        String[] gbinfoStrs = gbinfoStr.split("\\|");
        for(int i = 0;i<gbinfoStrs.length;i++){
            String[] gbiStrs = gbinfoStrs[i].split(",");
            if(gbiStrs.length!=2){
                String msg = "Sort 词典groupby信息格式错误";
                throw new BadDictException(msg, NodeType.SORT, text);
            }
            String keyStr = gbiStrs[0];
            String classStr=gbiStrs[1];
            Collection collection = MemOnto.getOntoIndex(classStr, ClassNodeFacade.class, qtype);
            if (collection == null || collection.isEmpty())
            	continue;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
	            ClassNodeFacade gbClass = (ClassNodeFacade) iterator.next();
	            if (groupByInfo_==null) {
	                groupByInfo_ = new HashMap<String,ClassNodeFacade>();
	            }
	            groupByInfo_.put(keyStr, gbClass);
	            break;
            }
        }
    }

    public void setChangedByChange(boolean changedByChange) {
        this.changedByChange = changedByChange;
    }

    public boolean isChangedByChange() {
        return changedByChange;
    }

    public void setDescending_(boolean descending_) {
        this.descending_ = descending_;
    }

    public boolean isDescending_() {
        return descending_;
    }

    public SortValueType valueType_ = SortValueType.NUM;
    private boolean descending_ = true; //默认降序排列
    public boolean isTopK_ = false;
    /** if k_ < 1, then k_ is a percentage. E.g. 0.1 means 10% */
    public double k_ = -1;
    public HashMap<String,ClassNodeFacade> groupByInfo_=null;
    public ClassNodeFacade groupBy_=null;
    /**标记是否被“下跌”之类的change所影响*/
    private boolean changedByChange = false;
}
