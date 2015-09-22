package com.myhexin.qparser.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public final class AvgNode extends SemanticNode {

    public static final String GT = ">";
    public static final String LT = "<";
    public static final String GE = ">=";
    public static final String LE = "<=";
    public static final String EQ = "=";
    
    private AvgNode(){}
    public AvgNode(String text) {
        super(text);
        type = NodeType.AVG;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws QPException {
        if(k2v.get("groupbyinfo")==null){
            String msg = "Avg 词典groupby信息缺失";
            throw new BadDictException(msg, NodeType.AVG, text);
        }
        String gbinfoStr = k2v.get("groupbyinfo");
        String[] gbinfoStrs = gbinfoStr.split("\\|");
        for(int i = 0;i<gbinfoStrs.length;i++){
            String[] gbiStrs = gbinfoStrs[i].split(",");
            if(gbiStrs.length!=2){
                String msg = "Avg 词典groupby信息格式错误";
                throw new BadDictException(msg, NodeType.AVG, text);
            }
            String keyStr = gbiStrs[0];
            String classStr=gbiStrs[1];
            Collection collection = MemOnto.getOntoIndex(classStr, ClassNodeFacade.class, qtype);
            if (collection == null || collection.isEmpty())
            	continue;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
            	ClassNodeFacade gbClass = (ClassNodeFacade)iterator.next();
	            if (gbClass == null){
	                String msg = String.format("Avg 词典groupby信息错误,本体词典中找不到指标“%s”",classStr);
	                throw new BadDictException(msg, NodeType.AVG, text);
	            }
	            if (groupByInfo_ == null) {
	                groupByInfo_ = new HashMap<String,ClassNodeFacade>();
	            }
	            groupByInfo_.put(keyStr, gbClass);
            }
        }
    }
    
    public void setCompare(String str){
        if(str.equals(GT)){
            compare_=GT;
        }else if(str.equals(LT)){
            compare_=LT;
        }else if(str.equals(GE)){
            compare_=GE;
        }else if(str.equals(LE)){
            compare_=LE;
        }else if(str.equals(EQ)){
            compare_=EQ;
        }else {
            
        }
    }
    
    public String toString(){
    	String str = super.toString();
    	str += "  GroupBy:["+groupBy_+"]";
    	return str;
    }
    
    public HashMap<String,ClassNodeFacade> groupByInfo_=null;
    public ClassNodeFacade groupBy_=null;
    public String compare_ = null;

	@Override
	protected SemanticNode copy() {
		AvgNode rtn = new AvgNode();
		rtn.compare_=compare_;
		if(groupBy_!=null) rtn.groupBy_= (ClassNodeFacade)NodeUtil.copyNode(groupBy_); //groupBy_.copy();
		
		if(groupByInfo_!=null) {
			rtn.groupByInfo_ = new HashMap<String,ClassNodeFacade>(groupByInfo_);
			/*Iterator<String> it = groupByInfo_.keySet().iterator();
			while(it.hasNext()) {
				String k = it.next();
				ClassNodeFacade v = groupByInfo_.get(k);
				rtn.groupByInfo_.put(k, (ClassNodeFacade)NodeUtil.copyNode(groupBy_));
			}*/
		}

		super.copy(rtn);
		return rtn;
	}

}
