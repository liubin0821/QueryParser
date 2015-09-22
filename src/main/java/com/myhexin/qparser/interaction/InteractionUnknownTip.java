package com.myhexin.qparser.interaction;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.UnexpectedException;

public class InteractionUnknownTip {

    public static void logTip(Query query) throws UnexpectedException {
    	throw new UnexpectedException("This method was not supported any more.");
        /*ArrayList<SemanticNode> snKnown = new ArrayList<SemanticNode>(query.getNodes().size());
        for (SemanticNode sn : query.getNodes()) {
            if (sn.type == NodeType.UNKNOWN && ((UnknownNode)sn).isRealUnknown()) {
                //这里skip还是有必要记录一下的，因为此处的unknown确实是跳过了
                //unknown 不用在此重复记录
                query.getLog().logUnknownWord(sn.getPubText());
            } else {
                snKnown.add(sn);
            }
        }
        

        String parseText = query.getLog().getMsg(ParseLog.TIP_PARSE_TEXT);
        if(parseText.length() > 0) {
            query.getLog().clearMsg(ParseLog.TIP_PARSE_TEXT);
            parseText += "，";
        }
        
        if(snKnown.size() == 0) {
            parseText += query.parseText;
        } else {
            for(SemanticNode sn : snKnown) {
                parseText += sn.getPubText();
            }
        }
        query.getLog().logMsg(ParseLog.TIP_PARSE_TEXT, parseText);*/
    }
}

//StringBuilder sbTip = new StringBuilder();
//ArrayList<String> unknowns = query.getLog().getUnknownWord();
//if(unknowns != null && unknowns.size() > 0) {
//  sbTip.append("未能识别 “<em>");
//  for(int i = 0; i < unknowns.size(); i++) {
//      if(i > 0) sbTip.append("、");
//      sbTip.append(Util.escapeHtml(unknowns.get(i)));
//  }
//  sbTip.append("</em>”，");
//}
//if(query.getLog().getTransWords() != null &&
//      query.getLog().getTransWords().size() > 0) {
//  sbTip.append("部分词有转译，");
//}

//若已经有tip了，则本次可能是追加条件的问句，需要将上次的
//用于解析的句子拿出来，然后将本次的追加在后面