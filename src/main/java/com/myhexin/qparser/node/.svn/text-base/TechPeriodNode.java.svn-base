package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.TechPeriodType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.tech.TechMisc;
import com.myhexin.qparser.tech.TechUtil;
import com.myhexin.qparser.util.Util;

public class TechPeriodNode extends SemanticNode{
    
    public ArrayList<SemanticNode> ofwhat = new ArrayList<SemanticNode>();

    private TechPeriodNode(){}
    
    public TechPeriodNode(String text) throws UnexpectedException {
        super(text);
        type = NodeType.TECH_PERIOD;
        periodType = getPeriod();
        double value = getNumValue();
        if(value>0) {
        	numNode = new NumNode(String.valueOf(value), value, value);
        }
        ofwhat.addAll(MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, Query.Type.STOCK));
    }

    public TechPeriodNode(String text, TechPeriodType periodType) throws UnexpectedException{
        this(text);
        this.periodType = periodType;
    }
    
    private final static Pattern Pattern_PERIOD_MIN = Pattern.compile("(分钟)");
    private final static Pattern Pattern_PERIOD_HOUR = Pattern.compile("(小时)");
	private final static Pattern Pattern_PERIOD_DAY = Pattern.compile("(日|天)" + TechMisc.LINE_CHINESE_REGEX);
	private final static Pattern Pattern_PERIOD_WEEK = Pattern.compile("(周)" + TechMisc.LINE_CHINESE_REGEX);
	private final static Pattern Pattern_PERIOD_MONTH = Pattern.compile("(月)" + TechMisc.LINE_CHINESE_REGEX);
	private final static Pattern Pattern_PERIOD_QUARTER = Pattern.compile("(季度|季)" + TechMisc.LINE_CHINESE_REGEX);
	private final static Pattern Pattern_PERIOD_YEAR = Pattern.compile("(年)" + TechMisc.LINE_CHINESE_REGEX);
    
    
    private TechPeriodType getPeriod() {
    	if(text!=null) {
    		if(Pattern_PERIOD_MIN.matcher(text).find()) {
    			return TechPeriodType.MIN;
    		}else if(Pattern_PERIOD_HOUR.matcher(text).find()) {
    			return TechPeriodType.HOUR;
    		}else if(Pattern_PERIOD_DAY.matcher(text).find()) {
    			return TechPeriodType.DAY;
    		}else if(Pattern_PERIOD_WEEK.matcher(text).find()) {
    			return TechPeriodType.WEEK;
    		}else if(Pattern_PERIOD_MONTH.matcher(text).find()) {
    			return TechPeriodType.MONTH;
    		}else if(Pattern_PERIOD_QUARTER.matcher(text).find()) {
    			return TechPeriodType.QUARTER;
    		}else if(Pattern_PERIOD_YEAR.matcher(text).find()) {
    			return TechPeriodType.YEAR;
    		}
    	}
    	return TechPeriodType.UNKNOWN;
    }
    
    private double getNumValue() {
    	StringBuilder buf = new StringBuilder();
    	for(int i=0;i<text.length();i++) {
    		char c = text.charAt(i);
    		if(c>='0' && c<='9') {
    			buf.append(c);
    		}
    	}
    	try{
    		return Double.parseDouble(buf.toString());
    	}catch(Exception e) {
    		
    	}
    	return -1.0;
    }
    
    
    @Override
    public void parseNode(HashMap<String, String> k2v, Type qtype) throws QPException {
    	//periodType = getPeriod();
    	periodType = TechUtil.getPeriodTypeByUserSay(text);
    }
    
    public String getPeriodId() throws UnexpectedException{
        return TechUtil.getPeriodIdByType(periodType);
    }    
    
    public String getPeriodUserSay() throws UnexpectedException{
        return TechUtil.getPeriodUserSayByType(periodType);
    }
    
    public TechPeriodType getPeriodType(){
        return periodType;
    }
    
    public String getNumNodeStr(){
    	if(numNode!=null) {
    		return Util.formatDouble(numNode.getFrom());
    	}else{
			return "0";
    	}
    }
    
    public void setNumNode(NumNode numNode) {
        this.numNode = numNode;
    }
    
    public NumNode getNumNode() {
        return numNode;
    }

    public boolean checkPeriod(){
        if(!TechUtil.isMinPeriod(periodType)) return true;
        return TechUtil.checkPeriod(this);
    }
    

    public void setInference(boolean isInference) {
        this.isInference = isInference;
    }

    public boolean isInference() {
        return isInference;
    }

    private NumNode numNode = null;
    private TechPeriodType periodType;
    private boolean isInference = false;
    
    
    
    public String toString(){
        return String.format("TECH_PERIOD:%s,Period:%s,NumNode:%s", text, periodType,
                numNode != null ? numNode.text : "");
    }
    
    public ArrayList<SemanticNode> oldNodes = new ArrayList<SemanticNode>();

	@Override
	protected SemanticNode copy() {
		TechPeriodNode rtn = new TechPeriodNode();
		rtn.isInference=isInference;
		rtn.numNode=numNode;
		rtn.ofwhat=ofwhat;
		rtn.oldNodes=oldNodes;
		rtn.periodType=periodType;



		super.copy(rtn);
		return rtn;
	}
}
