package com.myhexin.qparser.tech;

import java.util.ArrayList;

import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.util.Util;

public class TechParser {

    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(TechParser.class.getName());

    private Query query_;

    public TechParser(Query query) {
        query_ = query;
    }

    public void parser() {

        try {
            parseTechPeriodNode();
        } catch (NotSupportedException e) {
            logger_.debug(e.getLogMsg());
            query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
        } catch (UnexpectedException e) {
            logger_.debug(e.getLogMsg());
            query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
        }

    }

    /**
     * 找出节点中，技术指标周期相关词汇
     * 
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    private void parseTechPeriodNode() throws NotSupportedException, UnexpectedException {
        // TODO 目前只检测分钟线相关数据
        int i = 0;
        ArrayList<SemanticNode> nodes = query_.getNodes();
        while (i < nodes.size()) {
            SemanticNode node = nodes.get(i);
            if (node.type == NodeType.UNKNOWN) {
                NumNode numNode = NumUtil.makeNumNodeFromStr("1");
                if (TechUtil.isMinuteWord(node.getText())) {
                    if ((i - 1) >= 0 && nodes.get(i - 1).type == NodeType.NUM) {
                        numNode = (NumNode) nodes.get(i - 1);
                        nodes.remove(i - 1);
                        i--;
                    }
                    checkRelatedWords(nodes, i);
                    nodes.set(i, TechUtil.getTechPeriodNodeByNodes(numNode, node.getText()));
                } else if (TechUtil.isHourWord(node.getText())) {
                    if ((i - 1) >= 0 && nodes.get(i - 1).type == NodeType.NUM) {
                        numNode = (NumNode) nodes.get(i - 1);
                        nodes.remove(i - 1);
                        i--;
                    }
                    checkRelatedWords(nodes, i);
                    TechPeriodNode period = TechUtil.getTechPeriodNodeByNodes(
                            NumUtil.makeNumNodeFromStr(MiscDef.NUM_60), MiscDef.MINUTE_WORD);
                    period.setInference(true);
                    nodes.set(i, period);
                    if (numNode.getFrom() > 1) {
                        DateNode date = DateUtil.makeDateNodeFromStr(String.format("%s日",
                                Util.formatDouble(numNode.getFrom())), null);
                        ClassNodeFacade cn = MemOnto.getOnto(MiscDef.MA_TECH_INST_NAME, ClassNodeFacade.class, query_.getType());
                        nodes.add(++i, date);
                        nodes.add(++i, cn);
                    }
                }
            } else if (node.type == NodeType.NUM) {
                String numStr = TechUtil.getSuitableStringForTechPeriod(node.getText());
                if (numStr != null) {
                    nodes.set(i,
                            TechUtil.getTechPeriodNodeByNodes(NumUtil.makeNumNodeFromStr(numStr), MiscDef.MINUTE_WORD));
                }
            }
            i++;
        }
    }


    private void checkRelatedWords(ArrayList<SemanticNode> nodes, int i) {
        if ((i + 1) < nodes.size()) {
            SemanticNode mid = nodes.get(i + 1);
            if (TechUtil.isMinMidWord(mid.getText())) {
                nodes.remove(i + 1);
            }
        }
        if ((i + 1) < nodes.size()) {
            SemanticNode kLineWord = nodes.get(i + 1);
            if (TechUtil.isKLineWord(kLineWord.getText())) {
                nodes.remove(i + 1);
            }
        }
    }

}
