package com.myhexin.qparser.smart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;

/**
 * 这是一个貌似聪明的Operator（可理解为“接线员”或{@link com.myhexin.operate.Operator}的 子类
 * ），它匹配到一个系统解决不了的问题时，尝试给一个较为聪明的回答。
 */
public class SmartOperator {
    public SmartOperator(Query query) {
        this.query_ = query;
    }

    /**
     * 通过正则匹配的方式给出答案
     * @param query
     * @return
     */
    private String rgxBranch(String matchText) {
        String rlt = null;
        OUTTER: for(SmartAnswerEntry answer : SmartAnswer.answerList) {
            for (Pattern ptn : answer.rgxPtnArray_) {
                Matcher matcher = ptn.matcher(matchText);
                if (matcher.find()) {
                    rlt = answer.answer(matcher);
                    break OUTTER;
                }
            }
        }
        if(rlt != null && rlt.startsWith("SEE_DOCTOR_")) {
            String stkName = rlt.substring("SEE_DOCTOR_".length());
            rlt = MsgDef.SEE_DOCTOR_STR;
        }
        
        return rlt;
    }

    /**
     * 根据本身内部的query成员的内容，来分析是否是在本系统可以回答的范围之内
     * 如果在回答范围之内，返回一个根据用户输入动态生成的句子
     * 如果不在回答范围之内，返回null
     * @return
     */
    public String doOp() {
        String rtn = rgxBranch(getTextForRegex());
        if(rtn == null) {
            answerConcept();
        }
        return rtn;
    }
    
    private void answerConcept() {
        for(SemanticNode sn : query_.getNodes()) {
            if(NodeUtil.isConceptNode(sn)) {
                String conceptId = ((StrNode)sn).info;
                if(conceptId == null) {
                    logger_.debug("concept [{}] has no id", sn.getText());
                }
                break;
            }
        }
    }

    /**
     * 加载智能回答问句文件
     * @param doc
     * @throws DataConfException
     */
    public static void loadSmartAnswer(Document doc) throws DataConfException {
        SmartAnswer.loadSmartAnswer(doc);
    }
    
    /**
     * 从问句文本生成用于正则匹配的文本。目前是将其中的股票名称或代码替换成
     * 特殊模板。
     * @return
     */
    private String getTextForRegex() {
        String rtn = query_.text;
        for(SemanticNode sn : query_.getNodes()) {
            if(NodeUtil.isStockNode(sn)) {
                rtn = rtn.replaceAll(sn.getText().replace("*", "\\*"),
                        String.format(MiscDef.STK_MARK_FOR_SMART_OPER, sn.getText()));
            }
        }
        return rtn;
    }
    
    private Query query_;
    
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SmartOperator.class.getName());
}
