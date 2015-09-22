package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.ChangeDateType;
import com.myhexin.qparser.define.EnumDef.ChangeType;
import com.myhexin.qparser.define.EnumDef.GrowthType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.RangeabilityType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public final class ChangeNode extends SemanticNode {

	private ChangeNode(){}
    public ChangeNode(String text) {
        super(text);
        type = NodeType.CHANGE;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype)
            throws BadDictException {
        boolean hasInfo = k2v != null;
        boolean hasSign = k2v.containsKey("sign");
        boolean hasDefClass = k2v.containsKey("def_class");
        boolean hasDefVal = k2v.containsKey("def_val");
        boolean hasReplace = k2v.containsKey("replace");
        String msg = "";
        if (!hasInfo || !hasSign || !hasDefVal || !hasReplace) {
            msg = "Change 词典信息缺失";
            throw new BadDictException(msg, NodeType.CHANGE, text);
        }
        String signStr = k2v.get("sign");
        String def_valStr = k2v.get("def_val");
        String ofwhatStr = k2v.get("ofwhat");
        String replaceStr = k2v.get("replace");
        String isCreaseStr = k2v.get("iscrease");
        String growthTypeStr = k2v.get("growth");
        String dateTypeStr = k2v.get("date_type");
        try {
            parseDateTypes(dateTypeStr);
        } catch (UnexpectedException e) {
            msg = "Change 词典信息DateType格式或信息错误";
            System.out.println(e.getLogMsg());
            throw new BadDictException(e.getMessage(), NodeType.CHANGE, text);
        }
        setChangeType_(signStr.equals("+") ? ChangeType.INCREASE
                : ChangeType.DECREASE);
        setGrowthType(growthTypeStr);
        needBeReplace_ = replaceStr.equals("true");
        isCrease = isCreaseStr == null || isCreaseStr.equals("true");
        NumNode nn = null;
        NumRange nr = null;

        if (hasDefClass) {
            String[] def_classInfo = k2v.get("def_class").split(":");
            ClassNodeFacade cln = null;
            if (def_classInfo.length > 0) {
            	Collection collection = MemOnto.getOntoIndex(def_classInfo[0], ClassNodeFacade.class, qtype);
            	if (collection != null && collection.isEmpty() == false) {
	            	Iterator iterator = collection.iterator();
	            	while (iterator.hasNext()) {
	            		cln = (ClassNodeFacade) iterator.next();
	            		break;
	            	}
            	}
            }
            if (def_classInfo.length != 2) {
                msg = "Change 词典信息def_class格式或信息错误";
                throw new BadDictException(msg, NodeType.CHANGE, text);
            }

            nn = new NumNode(def_classInfo[1]);
            try {
                nr = NumParser.getNumRangeFromStr(def_classInfo[1]);
            } catch (NotSupportedException e) {
                msg = "Change 词典信息def_class格式或信息错误";
                throw new BadDictException(e.getMessage(), NodeType.CHANGE,
                        text);
            }
            nn.setNuminfo(nr);
            defClass_ = cln;
            defClassVal_ = nn;
        }
        try {
            nr = NumParser.getNumRangeFromStr(def_valStr);
        } catch (NotSupportedException e) {
            throw new BadDictException(e.getMessage(), NodeType.CHANGE, text);
        }
        nn = new NumNode(def_valStr);
        nn.setNuminfo(nr);
        defVal_ = nn;

        if (ofwhatStr != null) {
            String[] infos = ofwhatStr.split("\\|");
            for (String info : infos) {
                String[] line = info.split(":");
                boolean hasRightOfWhatInfo = line.length == 2;
                if (!hasRightOfWhatInfo) {
                    throw new BadDictException(msg, NodeType.CHANGE, text);
                }
                String label = line[0];
                Collection collection = MemOnto.getOntoIndex(label, ClassNodeFacade.class, qtype);
                if (collection == null || collection.isEmpty())
                	continue;
	            ofWhat_.addAll(collection);
            }
        }
    }

    private void parseDateTypes(String dateTypeStr) throws UnexpectedException {
        if(dateTypeStr==null||dateTypeStr.isEmpty()){
            return;
        }
        String[] dateTypeStrs = dateTypeStr.split("\\|");
        for(int i=0;i<dateTypeStrs.length;i++){
            String dateTypeI = dateTypeStrs[i];
            this.changeDateTypes.add(parseDateType(dateTypeI));
        }
    }

    private ChangeDateType parseDateType(String dateType) throws UnexpectedException {
       if(dateType.matches("预测|预告|预报")){
           return ChangeDateType.PREDICT;
       }else if(dateType.matches("累积|累计")){
           return ChangeDateType.ACCUMULATE;
       }else if(dateType.matches("复合")){
           return ChangeDateType.COMPOUND;
       }else {
           throw new UnexpectedException("Unexpected datetype of change:%s",dateType);
       }
    }

    private void setGrowthType(String growthTypeStr) throws BadDictException {
        if (growthTypeStr == null || growthTypeStr.isEmpty()) {
            return;
        } else if (!growthTypeStr.matches("同比|环比")) {
            throw new BadDictException("growthTpye信息错误", NodeType.CHANGE, text);
        }
        growthType = growthTypeStr.equals("同比") ? GrowthType.YEAR_ON_YEAR
                : GrowthType.CHAIN;
        growthTypeSettled = true;
    }

    public void setGrowthType(GrowthType growthType) {
        this.growthType = growthType;
    }

    public GrowthType getGrowthType() {
        return growthType;
    }

    public void setStandardSort(SortNode standardSort) {
        this.standardSort = standardSort;
    }

    public SortNode getStandardSort() {
        return standardSort;
    }

    public void setStandardNum(NumNode standardNum) {
        this.standardNum = standardNum;
    }

    public NumNode getStandardNum() {
        return standardNum;
    }

    public void setRangeabilityType(RangeabilityType rangeabilityType) {
        this.rangeabilityType = rangeabilityType;
    }

    public RangeabilityType getRangeabilityType() {
        return rangeabilityType;
    }
    
   public boolean hasStandard(){
       return standardSort!=null||standardNum!=null;
   }

    public void setCompareDate(DateNode compareDate) {
        this.compareDate = compareDate;
    }

    public DateNode getCompareDate() {
        return compareDate;
    }

    public void setChangeType_(ChangeType changeType_) {
        this.changeType_ = changeType_;
    }

    public String getPubText() {
        String dateTypeStr = null;
        String dateTypePredict = changeDateTypes
                .contains(ChangeDateType.HERALD) ? "预告" : changeDateTypes
                .contains(ChangeDateType.PREDICT) ? "预测" : "";
        String dateTypeAcc = changeDateTypes
                .contains(ChangeDateType.ACCUMULATE) ? "累计" : "";
        String dateTypeComp = changeDateTypes.contains(ChangeDateType.COMPOUND) ? "复合"
                : "";
        dateTypeStr = String.format("%s%s%s", dateTypePredict, dateTypeAcc,
                dateTypeComp);
        String rangeTypeStr = rangeabilityType == RangeabilityType.STRONGLY ? "大幅"
                : "";
        String standStr = standardNum == null ? "" : standardNum.oldStr_;
        standStr = standStr.isEmpty() ? standardSort == null ? standStr
                : standardSort.text : standardSort == null ? standStr
                : standStr + "并且" + standardSort.text;

        String pubText = String.format("%s%s%s%s", dateTypeStr, rangeTypeStr,
                text, standStr);
        return pubText;
    }
    
    public ChangeType getChangeType_() {
        return changeType_;
    }

    public void setCrease(boolean isCrease) {
        this.isCrease = isCrease;
    }

    public boolean isCrease() {
        return isCrease;
    }

    public void setGrowthTypeSettled(boolean growthTypeSettled) {
        this.growthTypeSettled = growthTypeSettled;
    }

    public boolean isGrowthTypeSettled() {
        return growthTypeSettled;
    }

    public void setChangeDateTypes(ArrayList<ChangeDateType> changeDateTypes) {
        this.changeDateTypes = changeDateTypes;
    }

    public ArrayList<ChangeDateType> getChangeDateTypes() {
        return changeDateTypes;
    }

    public boolean needBeReplace_ = true;
    private ChangeType changeType_ = ChangeType.INCREASE;
    public ClassNodeFacade defClass_ = null;
    public NumNode defClassVal_ = null;
    public NumNode defVal_ = null;
    public ArrayList<SemanticNode> ofWhat_ = new ArrayList<SemanticNode>();
    
    private ArrayList<ChangeDateType> changeDateTypes = new ArrayList<ChangeDateType>(); 
    private boolean isCrease = true;
    private DateNode compareDate = null;
    private GrowthType growthType = GrowthType.YEAR_ON_YEAR;
    private RangeabilityType rangeabilityType = RangeabilityType.GRADUAL;
    private SortNode standardSort = null;
    private NumNode standardNum = null;
    private boolean growthTypeSettled = false;
    
    @Override
    protected SemanticNode copy() {
		ChangeNode rtn = new ChangeNode();
		if(changeDateTypes!=null)
			rtn.changeDateTypes.addAll(changeDateTypes);
		
		rtn.changeType_=changeType_;
		if(compareDate!=null)
			rtn.compareDate=(DateNode)NodeUtil.copyNode(compareDate); //.copy();
		
		if(defClass_!=null)
			rtn.defClass_=(ClassNodeFacade)NodeUtil.copyNode(defClass_); //.copy();
		
		if(defClassVal_!=null)
			rtn.defClassVal_=(NumNode)NodeUtil.copyNode(defClassVal_); //.copy();
		if(defVal_!=null)
			rtn.defVal_= (NumNode)NodeUtil.copyNode(defVal_); //.copy();
		
		rtn.growthType=growthType;
		rtn.growthTypeSettled=growthTypeSettled;
		rtn.isCrease=isCrease;
		rtn.needBeReplace_=needBeReplace_;
		if(ofWhat_!=null) {
			rtn.ofWhat_.addAll(ofWhat_);
		}
		
		rtn.rangeabilityType=rangeabilityType;
		if(standardNum!=null)
			rtn.standardNum= (NumNode)NodeUtil.copyNode(standardNum); //.copy();
		
		if(standardSort!=null)
			rtn.standardSort=(SortNode) NodeUtil.copyNode(standardSort); //.copy();

		super.copy(rtn);
		return rtn;
	}
}
