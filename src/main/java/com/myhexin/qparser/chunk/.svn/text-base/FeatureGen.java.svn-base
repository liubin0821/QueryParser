package com.myhexin.qparser.chunk;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.EnumDef.*;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.*;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

public class FeatureGen {
    public static final String NODE_DELI = "<->";
    private static final String IND_NUM = "IndNum";
    private static String delStr = "个股|推荐|与|。|1.|3.|股中|股份|、|股|上市|股票|的|公司|有|哪|几|只|且|而且|与|或|而|然而|及|并且|g|,|票|及其|哪些|？|，|上市公司|的股票|的股|或者|或|+| |是谁|有多少|情况|是多少|的公司|的上市公司|有哪些|中";
    //建立一个简单的前序后续遍历列表
    private static String[] delStrArray = null;
    static{
        delStrArray = delStr.split("\\|");
        for(int i = 0; i < delStrArray.length; i++ ){
            delStrArray[i] = delStrArray[i] + NODE_DELI;
        }
    }

    public static String genFeature(Query query) {
        StringBuilder sbRtn = new StringBuilder();
        StringBuilder tempSB = new StringBuilder();
        String tempStr = null;
        for(int j = 0; j < query.getNodes().size(); j++){
            SemanticNode nodeJ = query.getNodes().get(j);
            getNodeFeat(tempSB, nodeJ);
            tempStr = tempSB.toString();
            sbRtn.append(tempSB);
            tempSB = new StringBuilder();
        }
        
        return sbRtn.toString();
    }
    
    public static void getNodeFeat( StringBuilder sbRtn, SemanticNode nodeJ ){
        switch(nodeJ.getType()){
        case CLASS:
            genIndexFeat(sbRtn, (ClassNodeFacade)nodeJ);
            break;
        case PROP:
            genPropFeat(sbRtn, (PropNodeFacade)nodeJ);
            break;
        case NUM:
            genNumFeat(sbRtn, (NumNode)nodeJ);
            break;
        case DATE:
            genDateFeat(sbRtn, (DateNode)nodeJ);
            break;
        case SORT:
            genSortFeat(sbRtn, (SortNode)nodeJ);
            break;
        case LOGIC:
            genLogicFeat(sbRtn, (LogicNode)nodeJ);
            break;
        case OPERATOR:
            genOperFeat(sbRtn, (OperatorNode)nodeJ);
            break;
        case QWORD:
            genQwordFeat(sbRtn, (QuestNode)nodeJ);
            break;
        case AVG:
            genAvgFeat(sbRtn, (AvgNode)nodeJ);
            break;
        case TECHOPERATOR:
            genTechOperFeat(sbRtn, (TechOpNode)nodeJ);
            break;
        case STR_VAL:
            genStrFeat(sbRtn, (StrNode)nodeJ);
            break;
        case TECH_PERIOD :
            genPeriodFeat(sbRtn, (TechPeriodNode)nodeJ);
            break ;
        default:
            sbRtn.append("Unkn").append(nodeJ.getText());
            sbRtn.append(NODE_DELI);
            break;
        }
    }

    private static void genPeriodFeat(StringBuilder sbRtn,
            TechPeriodNode techperiodNode) {
        sbRtn.append("TechPeri");
        
//        String period = "None";
//        switch(techperiodNode.getPeriodType()){
//        case DAY: period = "D"; break;
//        case WEEK: period = "W"; break;
//        case MONTH: period = "M"; break;
//        case YEAR: period = "Y"; break;
//        case MIN_1: period = "m1"; break;
//        case MIN_5: period = "m5"; break;
//        case MIN_15: period = "m15"; break;
//        case MIN_30: period = "m30"; break;
//        case MIN_60: period = "m60"; break;
//        }
//        sbRtn.append(period);
        
        sbRtn.append(NODE_DELI);
    }

    private static void genStrFeat(StringBuilder sbRtn, StrNode strNode) {
        sbRtn.append("Str");
        sbRtn.append(NODE_DELI);
    }

    private static void genTechOperFeat(StringBuilder sbRtn,
            TechOpNode techopNode) {
        int nOperand = techopNode.maxBindNum;
        sbRtn.append("TechOper").append(nOperand == 1 ? "Uni" : nOperand == 2 ? "Bi" : "Mul");
        sbRtn.append(NODE_DELI);
    }

    private static void genAvgFeat(StringBuilder sbRtn, AvgNode nodeJ) {
        sbRtn.append("Avg");
        sbRtn.append(NODE_DELI);
    }

    private static void genQwordFeat(StringBuilder sbRtn, QuestNode nodeJ) {
        sbRtn.append("Qword");
        sbRtn.append(NODE_DELI);
    }

    private static void genOperFeat(StringBuilder sbRtn, OperatorNode operNode) {
        sbRtn.append("Oper");

        String operType = "None";
        switch(operNode.operatorType){
        case ADD: operType = "加"; break;
        case SUBTRACT: operType =  "减"; break;
        case MULTIPLY: operType =  "乘"; break;
        case DIVIDE: operType = "除"; break;
        case RATE: operType = "比"; break;
        }
        sbRtn.append(operType);
        
        sbRtn.append(NODE_DELI);
    }

    private static void genLogicFeat(StringBuilder sbRtn, LogicNode nodeJ) {
        sbRtn.append("Logic");
        sbRtn.append(NODE_DELI);
    }

    private static void genSortFeat(StringBuilder sbRtn, SortNode nodeJ) {
        sbRtn.append("Sort");
        sbRtn.append(NODE_DELI);
    }

    private static void genDateFeat(StringBuilder sbRtn, DateNode dateNode) {
        sbRtn.append("Date");
        int rangeLen = 1;
        try {
            rangeLen = dateNode.getRangeLen(dateNode.getUnitOfDate());
        } catch (UnexpectedException e) {
            ; //ignored
        }
        sbRtn.append(rangeLen == 1 ? "Point" : "Range");
        sbRtn.append(NODE_DELI);
    }

    private static void genNumFeat(StringBuilder sbRtn, NumNode numNode) {
        Unit numUnit = numNode.getUnit();
        sbRtn.append("Num").append(mapUnitToStr(numUnit));
        sbRtn.append(NODE_DELI);
    }

    private static void genPropFeat(StringBuilder sbRtn, PropNodeFacade PropNodeFacade) {
        if(PropNodeFacade.isNumProp()){
            sbRtn.append("PropNum");
        }else if(PropNodeFacade.isDateProp()){
            sbRtn.append("PropDate");
        }else if(PropNodeFacade.isStrProp()){
            sbRtn.append("PropStr");
        }else if(PropNodeFacade.isBoolProp()){
            sbRtn.append("PropBool");
        }else{
            sbRtn.append("PropNone");
        }
        
        sbRtn.append(NODE_DELI);
    }

    private static void genIndexFeat(StringBuilder sbRtn, ClassNodeFacade ClassNodeFacade) {
        if(ClassNodeFacade.getIndexType() == IndexType.TECH_A_LINE) {
            sbRtn.append("TechLine");
        } else if(ClassNodeFacade.getIndexType() == IndexType.TECH_LINES) {
            sbRtn.append("TechInd");
        } else if(ClassNodeFacade.getIndexType() == IndexType.TECH_SPECIAL) {
            sbRtn.append("TechSpec");
        } else if(ClassNodeFacade.isDateIndex()){
            sbRtn.append("IndDate");
        } else if(ClassNodeFacade.isStrIndex()){
            sbRtn.append("IndStr");
        } else if(ClassNodeFacade.isBoolIndex()){
            sbRtn.append("IndBool");
        } else if(ClassNodeFacade.isNumIndex()){
            if(MiscDef.CLOSING.equals(ClassNodeFacade.getText())) {
                sbRtn.append("Ind股价");
            } else if(MiscDef.CHENG_JIAO_LIANG.equals(ClassNodeFacade.getText())) {
                sbRtn.append("IndVol");
            } else {
                sbRtn.append(IND_NUM).append(mapUnitToStr(ClassNodeFacade.getValueUnit()));
            }
        } else {
            sbRtn.append("IndNone");
        }
        
        sbRtn.append(NODE_DELI);
    }
    
    private static String mapUnitToStr(Unit unit) {
        switch(unit){
        case YUAN: return "元";
        case BEI:
        case PERCENT: return "%";
        case SHOU:
        case GU: return "股";
        default:
            return "无";
        }
    }
}

