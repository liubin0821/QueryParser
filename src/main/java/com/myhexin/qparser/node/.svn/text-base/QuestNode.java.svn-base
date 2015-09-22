package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.QuestType;
import com.myhexin.qparser.except.BadDictException;

public final class QuestNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		QuestNode rtn = new QuestNode(super.text);
		rtn.questType=questType;
		rtn.questUnit=questUnit;


		super.copy(rtn);
		return rtn;
	}
	
	private QuestNode(){}
    public QuestNode(String text){
        super(text);
        type = NodeType.QWORD;
    }

    private void setQwordTpye(String qtype) {
        if (qtype.equals("how_much")) {
            questType = QuestType.HOW_MUCH;
        } else if (qtype.equals("when")) {
            questType = QuestType.WHEN;
        } else if (qtype.equals("who")) {
            questType = QuestType.WHO;
        } else if (qtype.equals("where")) {
            questType = QuestType.WHERE;
        } else if (qtype.equals("which")) {
            questType = QuestType.WHICH;
        } else if (qtype.equals("what")) {
            questType = QuestType.WHAT;
        } else {
            ;// No Op
        }
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
        String qType= k2v.get("type");
        setQwordTpye(qType);
        String msg = "Qword 词典信息错误";
        if(questType==QuestType.UNKNOWN){
            throw new BadDictException(msg, NodeType.QWORD, text);
        }
        if (k2v.get("type").equals("how_much") || k2v.get("unit") != null) {
            String unitStr = k2v.get("unit");
            questUnit = unitStr;
        }
    } 
    
    public QuestType questType = QuestType.UNKNOWN; 
    public String questUnit = null;
   
}
