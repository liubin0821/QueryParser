package com.myhexin.qparser.tokenize;

import java.util.ArrayList;

import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.ChangeDateType;
import com.myhexin.qparser.define.EnumDef.ChangeType;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.GrowthType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.RangeabilityType;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.FakeNumNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.number.NumUtil;

public class ChangeParser {
    private static String rangeabilityRegex = "大幅|剧烈|急剧|高";
    private static String growthYearOnYearRegex = "逐年|同比|年";
    private static String growthChainRegex = "环比|逐天|逐月|季";
    
    private static String predictRegex = "预测";
    private static String heraldRegex="预告|预报";
    private static String accumulateRegex = "累积|累计";
    private static String compoundRegex = "复合";
    
    private static String needChangeToChange = "同比|环比";

    public ChangeParser(Query query) {
        this.query_ = query;
    }

    public void parse() {
        try {
            parseChange();
            changeUnknowToChange();
        } catch (UnexpectedException e) {
            logger_.debug(e.getLogMsg());
            query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
        }
    }

    private void changeUnknowToChange() {
    //TODO:将没用到的同比、环比转为change
        ArrayList<SemanticNode> nodes = query_.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).type != NodeType.UNKNOWN||!nodes.get(i).getText().matches(needChangeToChange)) {
                continue;
            }
        }
    }

    private void parseChange() throws UnexpectedException {
        ArrayList<SemanticNode> nodes = query_.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).type != NodeType.CHANGE) {
                continue;
            }
            ChangeNode curChange = (ChangeNode) nodes.get(i);
            parseChangeDateType(nodes, curChange);
            parseRangeability(nodes, curChange);
            parseGrowthType(nodes, curChange);
            parseStandard(nodes, curChange);
            i = nodes.indexOf(curChange);
        }
    }

    /**
     * 添加累积、预测等信息
     * @param nodes
     * @param curChange
     * @throws UnexpectedException 
     */
    private void parseChangeDateType(ArrayList<SemanticNode> nodes,
            ChangeNode curChange) throws UnexpectedException {
        parseChangeDateType(nodes, curChange, Direction.BOTH);
    }

    private void parseChangeDateType(ArrayList<SemanticNode> nodes,
            ChangeNode curChange, Direction direction)
            throws UnexpectedException {
        if (direction == Direction.BOTH) {
            parseChangeDateType(nodes, curChange, Direction.LEFT);
            parseChangeDateType(nodes, curChange, Direction.RIGHT);
            return;
        }
        int i = nodes.indexOf(curChange);
        int indexCount = 0;
        while (direction == Direction.LEFT ? --i > -1 : ++i < nodes.size()) {
            SemanticNode curNode = nodes.get(i);
            boolean needBreak = curNode.type == NodeType.LOGIC;
            needBreak |= curNode.type == NodeType.OPERATOR;
            needBreak |= curNode.type == NodeType.NUM ;//|| TreeBuilderUtil.isSpValueLikeNum(curNode);
            needBreak |= direction == Direction.LEFT ? indexCount == 2: indexCount == 1;
            if (needBreak) {
                break;
            } else if (curNode.type == NodeType.CLASS) {
                indexCount++;
            } else if (isChangeDateTypeKeyWord(curNode.getText())) {
                ChangeDateType changeDateType = getChangeDateTypeByStr(curNode.getText());
                if (changeDateType == null) {
                    throw new UnexpectedException("changeDateType 未识别：%s",
                            curNode.getText());
                } else if (curChange.getChangeDateTypes().contains(
                        changeDateType)) {
                    break;
                }
                curChange.getChangeDateTypes().add(changeDateType);
            } else {
                ;// No Op
            }
        }
    }

    private ChangeDateType getChangeDateTypeByStr(String text) {
        if (text == null || text.isEmpty() || !isChangeDateTypeKeyWord(text)) {
            return null;
        }
        return text.matches(accumulateRegex) ? ChangeDateType.ACCUMULATE : text
                .matches(compoundRegex) ? ChangeDateType.COMPOUND : text
                .matches(predictRegex) ? ChangeDateType.PREDICT : text
                .matches(heraldRegex) ? ChangeDateType.HERALD : null;
    }

    private boolean isChangeDateTypeKeyWord(String text) {
        return text != null && text.matches(accumulateRegex)
                || text.matches(compoundRegex) || text.matches(predictRegex)
                || text.matches(heraldRegex);
    }

    private void parseGrowthType(ArrayList<SemanticNode> nodes,
            ChangeNode curChange) throws UnexpectedException {
        if (curChange.isGrowthTypeSettled()) {
            return;
        }

        int step = 0;
        int i = nodes.indexOf(curChange);
        boolean isSetByDate = false;
        while (--i > -1) {
            SemanticNode curNode = nodes.get(i);
            boolean needContinue = curNode.type == NodeType.CLASS;
            boolean needBreak = curNode.type != NodeType.UNKNOWN
                    && curNode.type != NodeType.DATE;
            needBreak |= step > 3;
            if (needContinue) {
                step++;
                continue;
            } else if (needBreak) {
                break;
            }
            if (curNode.getText().matches(growthYearOnYearRegex)) {
                curChange.setGrowthType(GrowthType.YEAR_ON_YEAR);
                curChange.setText(curNode.getText() + curChange.getText());
                curChange.setGrowthTypeSettled(true);
                nodes.remove(curNode);
                break;
            } else if (curNode.getText().matches(growthChainRegex)) {
                curChange.setGrowthType(GrowthType.CHAIN);
                curChange.setText( curNode.getText() + curChange.getText() );
                curChange.setGrowthTypeSettled(true);
                nodes.remove(curNode);
                break;
            } else if (curNode.type == NodeType.DATE) {
                if (isSetByDate) {
                    break;
                }
                isSetByDate = true;
            } 
        }
    }

    private void parseRangeability(ArrayList<SemanticNode> nodes,
            ChangeNode curChange) {
        int step = 0;
        int i = nodes.indexOf(curChange);
        while (--i > -1) {
            SemanticNode curNode = nodes.get(i);
            boolean needBreak = curNode.type != NodeType.UNKNOWN;
            needBreak |= curChange.getRangeabilityType() != RangeabilityType.GRADUAL;
            needBreak |= step > 3;
            if (needBreak) {
                break;
            }
            if (curNode.getText().matches(rangeabilityRegex)) {
                curChange.setRangeabilityType(RangeabilityType.STRONGLY);
                nodes.remove(curNode);
                break;
            }
        }
    }

    private void parseStandard(ArrayList<SemanticNode> nodes,
            ChangeNode curChange) throws UnexpectedException {
        int i = nodes.indexOf(curChange);
        while (++i < nodes.size()) {
            SemanticNode curNode = nodes.get(i);
            NodeType type = curNode.type;
            if (type == NodeType.UNKNOWN ) {
                if (curChange.hasStandard()) {
                    break;
                }
                continue;
            } else if(type == NodeType.DATE){
                SemanticNode nextNode = i<nodes.size()-1?nodes.get(i+1):null;
                if (nextNode != null && nextNode.type != NodeType.NUM
                        && nextNode.type != NodeType.SORT){
                    break;
                }
                continue;
            }else if (type != NodeType.NUM && type != NodeType.SORT) {
                break;
            } else if (type == NodeType.NUM
                    && curChange.getStandardNum() != null) {
                // 如“增长30%，20%”
                break;
            } else if (type == NodeType.SORT
                    && curChange.getStandardSort() != null) {
                // 如“增长最大，前20的”
                break;
            }

            if (type == NodeType.SORT) {
                // 对ChangeNode后的SortNode进行处理，如“减少最大”
                SortNode sortNode = (SortNode) curNode;
                changeSortForChange(sortNode, curChange.getChangeType_());
                curChange.setStandardSort(sortNode);
            } else if (type == NodeType.NUM) {
                NumNode numNode = (NumNode) curNode;
                changeNumForChange(numNode, curChange.getChangeType_());
                curChange.setStandardNum(numNode);
            }
            nodes.remove(curNode);
            i--;
        }
    }

    private void changeSortForChange(SortNode sn, ChangeType changeType) {
        if (changeType == ChangeType.DECREASE) {
            sn.setDescending_(sn.isDescending_() ? false : true);
            sn.setChangedByChange(true);
        }
    }

    private void changeNumForChange(NumNode numNode, ChangeType changeType) {
        if (numNode.isFake()) {
            if (changeType == ChangeType.DECREASE) {
                FakeNumNode fakeNumNode = (FakeNumNode) numNode;
                fakeNumNode.exchangeFakeNumType();
            }
            return;
        }
        if (changeType == ChangeType.INCREASE
                && numNode.getRangeType().equals(OperDef.QP_EQ)) {
            // 凡是有增长趋势的，都将其下数值转为大于或大于等于
            numNode.getNuminfo().setNumRange(
                    numNode.getNuminfo().getDoubleFrom(), NumRange.MAX_);
        } else if (changeType == ChangeType.DECREASE) {
            // 凡是有增长趋势的，都将其下数值转为大于或大于等于
            NumUtil.changeNumForDecrease(numNode);
            if (numNode.getRangeType().equals(OperDef.QP_EQ)) {
                numNode.getNuminfo().setNumRange(NumRange.MIN_,
                        numNode.getNuminfo().getDoubleFrom());
            }
        }
    }

    private Query query_;
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(ChangeParser.class.getName());
}

