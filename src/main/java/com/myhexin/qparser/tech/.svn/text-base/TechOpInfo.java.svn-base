package com.myhexin.qparser.tech;

import java.util.HashMap;

import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.TechOpNode;

public class TechOpInfo {
    
    /** 带参数的公式，默认每个公式只能对两条线做操作，#LEFT #RIGHT */
    public String formula;
    /** 默认时间 ,默认为天时间线 */
    public String dateTime = TechUtil.PERIOD_DEFAULT_ID;

    public TechOpInfo(String formulaWithParam, String defIndexes) throws UnexpectedException, NotSupportedException{
        formula = formulaWithParam;
    }
    
    

    /** 
     * 公式操作和多线指标的参数对应关系，用String表示
     * 例如："diff值&#LEFT&&dea值&#RIGHT"
     * 表示：diff值 对应 #LEFT，dea值对应 #
     */
    public HashMap<String,String> indexParamMap = new HashMap<String,String>();
    public HashMap<String,String> indexFormulaMap = new HashMap<String,String>();
    
    public void addIndexParam(String index,String paramMap){
        indexParamMap.put(index, paramMap);
    }
    
    public String getIndexParam(String index){
        return indexParamMap.get(index);
    }
    
    public void addIndexFormula(String index,String idxFormula){
        indexFormulaMap.put(index, idxFormula);
    }
    
    public String getIndexFormula(String index){
        return indexFormulaMap.get(index);
    }

    
    public static TechOpInfo changeTechFromOp(TechOpNode techOpNode) {
        String text = techOpNode.getText();
        OperatorNode op = techOpNode.innerOp;
        OperatorType type = op.operatorType;
        String sign = op.standard.getRangeType();
        if(type == OperatorType.SUBTRACT){
           if(">".equals(sign)){
               text = ">";
           }else if("<".equals(sign)){
               text = "<";
           }else {
               text = "=";
           }
        }        
        return TechUtil.TECH_OP_INFOS.get("公式"+text);
    }
}
