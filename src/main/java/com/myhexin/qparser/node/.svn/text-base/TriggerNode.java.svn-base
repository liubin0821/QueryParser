package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.tech.TechMisc;

public class TriggerNode extends SemanticNode {

	private TriggerNode(){}
    public TriggerNode(String text) {
        super(text);
        type = NodeType.TRIGGER;
    }
    
    
    public void parseNode(HashMap<String,String> k2v, Query.Type qtype) throws BadDictException {
        if (k2v == null || !k2v.containsKey("prop")
                || !k2v.containsKey("direction")) {
            String msg = "Trigger 词典信息缺失";
            throw new BadDictException(msg, NodeType.TRIGGER, text);
        }
        String propLable = k2v.get("prop");
        String directionStr = k2v.get("direction");
        String stoplist = k2v.get("stoplist");
        String skiplist = k2v.get("skiplist");
        String isIndexStr = k2v.get("isindex");
        String ofWhatStr = k2v.get("ofwhat");
        String isOverlapStr = k2v.get("isOverlap") ;
        if(isIndexStr!=null&&isIndexStr.equals("true")){
            isIndex_=true;
        }
        if(isOverlapStr != null && isOverlapStr.equals("true")){
        	isOverlap_ = true ;
        }
        
        direction = directionStr.equals("left") ? Direction.LEFT : 
            directionStr.equals("right") ? Direction.RIGHT : Direction.BOTH;

        //add by wyh 2014.10.20
        subType = new LinkedHashSet<String>();
        if(propLable != null && propLable != "")
        	for (String sb : propLable.split("\\|")) 
        		subType.add(sb);				
			
        //MemOnto.getOnto(propLable, PropNodeFacade.class, qtype);
        /*Vector<PropNodeFacade> vector = ((Vector<PropNodeFacade>) OntoXmlReader.subTypeToProp.get(propLable));
        if(vector !=null) {
        	trigProp =  vector.get(0).clone();
        } else {
        	Collection collection = MemOnto.getOntoC(propLable, PropNodeFacade.class, qtype);
        	if (collection != null && collection.isEmpty() == false) {
	        	Iterator iterator = collection.iterator();
	        	while (iterator.hasNext()) {
	        		trigProp = (PropNodeFacade) iterator.next();
	        		break;
	        	}
        	}
        }*/
        
        ofWhat_ = new ArrayList<PropNodeFacade>();
        if(ofWhatStr!=null){
            String[] ofWhats = ofWhatStr.split("\\|");
            for (int i = 0; i < ofWhats.length; i++) {
                String ofWhatI = ofWhats[i];
                Collection collection = MemOnto.getOntoC(ofWhatI, PropNodeFacade.class, qtype);
                if (collection == null || collection.isEmpty())
                	continue;
                ofWhat_.addAll(collection);
            }
        }else{
            ofWhat_.add(trigProp);
        }
        
        stopList_ = addListInfo(stoplist,"\\|");
        skipList_ = addListInfo(skiplist,"\\|");
        
    }
    
    private ArrayList<String> addListInfo(String listStr, String splitStr) {
        if (listStr == null) {
            return null;
        }
        String[] strs = listStr.split("\\|");
        ArrayList<String> list = new ArrayList<String>();
        for (String str : strs) {
            if (list.contains(str)) {
                continue;
            }
            list.add(str);
        }
        return list;
    }
    
    public boolean isOverlap(){
    	return isOverlap_ ;
    }


    public PropNodeFacade trigProp = null;
    public ArrayList<String> skipList_=null;
    public ArrayList<String> stopList_=null;
    public ArrayList<PropNodeFacade> ofWhat_=null;
    public LinkedHashSet<String> subType = null;
    public boolean isIndex_ = false;
    public Direction direction = Direction.BOTH;
    /** 是否为overlap Trigger, 如: "水泥概念"当水泥的ofwhat和概念的ofwhat出现交叉(overlap)时,
     * 可用概念的ofwhat添加到水泥的ofwhat中
     * */
    private boolean isOverlap_ = false ;
	@Override
	protected SemanticNode copy() {
		TriggerNode rtn = new TriggerNode();
		rtn.direction=direction;
		rtn.isIndex_=isIndex_;
		rtn.isOverlap_=isOverlap_;
		if(ofWhat_!=null)
			rtn.ofWhat_=new ArrayList<PropNodeFacade>(ofWhat_);
		rtn.skipList_=skipList_;
		rtn.stopList_=stopList_;
		rtn.subType=subType;
		rtn.trigProp=trigProp;

		super.copy(rtn);
		return rtn;
	}
    
}
