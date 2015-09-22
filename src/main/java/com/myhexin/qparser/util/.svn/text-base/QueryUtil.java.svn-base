package com.myhexin.qparser.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.ChangeType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TriggerNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

public class QueryUtil {
    public static String formatQueryAsTpl(Query query) {
        StringBuilder sb = new StringBuilder();
        for(SemanticNode node : query.getNodes()) {
            if(node.type == NodeType.DATE) {
                sb.append("DATE").append(' ');
            } else if(node.type == NodeType.NUM) {
                sb.append("NUM").append(' ');
            } else if(node.type == NodeType.OPERATOR) {
                sb.append("OP").append(' ');
            } else if(node.type == NodeType.LOGIC) {
                sb.append("LOGIC").append(' ');
            } else {
                sb.append(node.getText()).append(' ');
            }
        }
        return sb.toString().trim();
    }
    
    public static void printNodes(Query query) {
        ArrayList<SemanticNode> nodes = query.getNodes();
        System.out.println(semanticNodesToStr(nodes));
        
    }
    
    public static String getQueryText(Query query) {
    	ArrayList<SemanticNode> nodes = query.getNodes();
        StringBuilder sb = new StringBuilder();
        for (SemanticNode sn : nodes) {
        	sb.append(sn.getText());
        	sb.append("  ");
        }
        return sb.toString().trim();        
    }
    
    
    public static String semanticNodesToStr(ArrayList<SemanticNode> nodes) {
        String names = "";
        String types = "";
        for (int i = 0; i < nodes.size(); i++) {
            
            if (nodes.get(i).type == NodeType.DATE
                    && ((DateNode) nodes.get(i)).getDateinfo() != null) {
                DateRange dr = ((DateNode) nodes.get(i)).getDateinfo();
                types = String
                        .format("%s(%s)[%s]", nodes.get(i).type,
                                dr.toDateString(),
                                ((DateNode) nodes.get(i)).isSequence);
                types = String.format("%s[%s]", types,((DateNode) nodes.get(i)).getFrequencyInfo());
                
            } else if (nodes.get(i).type == NodeType.NUM
                    && ((NumNode) nodes.get(i)).getNuminfo() != null) {
                NumRange nr = ((NumNode) nodes.get(i)).getNuminfo();
                types = nodes.get(i).type
                        + String.format("(%s)", nr.toNumString());
            } else if (nodes.get(i).type == NodeType.FOCUS) {
                types = String.format("FOCUS[");
                FocusNode node = (FocusNode)nodes.get(i);
                for(int j=0; j<node.focusList.size(); j++)
                {
                    types += "|" + node.focusList.get(j).getType() + ":" + node.focusList.get(j).getContent();
                }
                types +="]";
	    } else if (nodes.get(i).type == NodeType.BOUNDARY) {
		    types = String.format("BOUNDARY[");
		    BoundaryNode boundary = (BoundaryNode)nodes.get(i);
		    if (boundary.isStart())
		    {
			    types += "START:";
		    }
		    else if (boundary.isEnd())
		    {
			    types += "END:";
		    }
		    types += boundary.getSyntacticPatternId();
		    types += "]";
            } else if (nodes.get(i).type == NodeType.STR_VAL
                    && ((StrNode) nodes.get(i)).subType!= null) {
                /*ArrayList<SemanticNode> ofwhat = ((StrNode) nodes.get(i)).ofWhat;
                String ofwhatStr  = "";
                for(SemanticNode node:ofwhat){
                    ofwhatStr+=node.text+" ";
                }*/
            	LinkedHashSet<String> sts = ((StrNode) nodes.get(i)).subType;
                types = nodes.get(i).type
                + String.format("(Ofwhat:%s)", sts.toString());
            }else if (nodes.get(i).type == NodeType.CHANGE) {
                ChangeNode cn = (ChangeNode)nodes.get(i);
                 boolean needBeReplace_ = cn.needBeReplace_;
                 String defClass=null;
                 ChangeType type_ = cn.getChangeType_();
                 ClassNodeFacade defClass_ = cn.defClass_;
                 if(defClass_!=null){
                     defClass=defClass_.getText();
                 }
                 NumNode defClassVal_ = cn.defClassVal_;
                 String defClassVal=null;
                 if(defClassVal_!=null){
                     defClassVal=defClassVal_.getNuminfo().toNumString();
                 }
                 NumNode defVal_ = cn.defVal_;
                 String defVal=null;
                 if(defVal_!=null){
                     defVal=defVal_.getNuminfo().toNumString();
                 }
                // HashMap<SemanticNode,NumNode> ofWhat_ = cn.ofWhat_;
                 types=nodes.get(i).type+String.format("[needBeReplace_:%s][type_:%s][defClass_:%s][defClassVal_:%s][defVal_:%s]", needBeReplace_,type_,defClass,defClassVal,defVal);
            } else if (nodes.get(i).type == NodeType.TRIGGER) {
                TriggerNode curNode = (TriggerNode) nodes.get(i);
                ArrayList<PropNodeFacade> ofwhat = curNode.ofWhat_;
                String ofwhatStr = "";
                for (int k = 0 ;k<ofwhat.size();k++) {
                    PropNodeFacade node = ofwhat.get(k);
                    ofwhatStr += node.getText() + "|";
                }

                types = nodes.get(i).type
                        + String.format(
                                "[TriggerProp:%s][Dirction:%s][OfWhat:%s]",
                                curNode.trigProp.getText(), curNode.direction,
                                ofwhatStr);
            } else if (nodes.get(i).type == NodeType.CLASS) {
                types = ((ClassNodeFacade) nodes.get(i)).isFake() ? "FAKECLASS"
                        : nodes.get(i).type.toString();
            }
            else {
                types = nodes.get(i).type.toString();
            }
            names += nodes.get(i).getText() + "{"+types+"}"+" ";
        }
        return "[Nodes]="+names;
    }

    
    

    public static void splitTest(Query query) {
        ArrayList<SemanticNode> nodes = query.getNodes();
        for (SemanticNode node : nodes) {
            if (node.type != NodeType.DATE) {
                continue;
            }
            
            ArrayList<String[]> dates;
            try {
                dates = ((DateNode) node).splitRangeToListOfString();
                if (dates == null) {
                    System.out.println(node.getText());
                    continue;
                }
                for (String[] date : dates) {
                    System.out.println(date[0] + "----" + date[1]);
                }
            } catch (UnexpectedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
