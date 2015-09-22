package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tech.TechMisc;
import com.myhexin.qparser.tech.TechUtil;

public class PhraseParserPluginTechperiodDetermine extends PhraseParserPluginAbstract{

	@Autowired(required=true)
	private TechPeriod techPeriod;
	
	
	
    public void setTechPeriod(TechPeriod techPeriod) {
    	this.techPeriod = techPeriod;
    }

	public PhraseParserPluginTechperiodDetermine() {
        super("Tech_Period_Determine");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return techperiodDetermine(nodes);
    }

    /**
     * 用于识别句中的周期节点
     * 
     * @param qlist
     * @throws NotSupportedException
     */
    private ArrayList<ArrayList<SemanticNode>> techperiodDetermine(ArrayList<SemanticNode> nodes)
    {
        ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
        qlist.add(nodes);
        try{
        	techPeriod.parseTechperiod(qlist);
        }catch(UnexpectedException e)
        {
        }catch(NotSupportedException e)
        {
        }
        return qlist;
    }
}

@Component
class TechPeriod {


    public void parseTechperiod(ArrayList<ArrayList<SemanticNode>> qlist) throws UnexpectedException, NotSupportedException {
        for (ArrayList<SemanticNode> nodes : qlist) {
            parse(nodes);
        }
    }

    /**
     * 解决分析周期识别问题，例句如下：
     * a.周macd金叉。【done】
     * b.macd月线金叉。【done】
     * c.5周线金叉10周线。【doing】
     * d.60分钟macd金叉。【doing】
     * 
     * @author zd<zhangdong@myhexin.com>
     * @param nodes
     * @throws UnexpectedException
     * @throws NotSupportedException 
     */
    private void parse(ArrayList<SemanticNode> nodes) throws UnexpectedException, NotSupportedException {
        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode sn = nodes.get(i);
            if (sn.type == NodeType.UNKNOWN) {
                if (sn.getText().matches(TechMisc.TECHPERIOD_REGEX)) {
                    SemanticNode oldNode = null;
                    if (i + 1 < nodes.size() && TechUtil.isKLineWord(nodes.get(i + 1).getText())) {
                        oldNode = nodes.get(i + 1);
                        nodes.remove(i + 1);
                    }
                    TechPeriodNode techPeriodNode = new TechPeriodNode(String.format("%s", sn.getText()));
                    techPeriodNode.ofwhat.addAll(MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class,
                            Query.Type.ALL));
                    nodes.set(i, techPeriodNode);
                    techPeriodNode.oldNodes.add(oldNode);
                }
            } else if (sn.type == NodeType.DATE) {
                if (DatePatterns.MUNITE.matcher(sn.getText()).matches()) {
                    TechPeriodNode techPeriodNode = new TechPeriodNode(String.format("%s%s", sn.getText(),
                            TechMisc.LINE_CHINESE));
                    techPeriodNode.ofwhat.addAll(MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class,
                            Query.Type.ALL));
                    nodes.set(i, techPeriodNode);
                    techPeriodNode.oldNodes.add(sn);
                } else if (DatePatterns.REGEX_WEEK_MONTH.matcher(sn.getText()).matches()) {
                    for (int p = i + 1; p < nodes.size(); p++) {
                        SemanticNode rightNode = nodes.get(p);
                        if (rightNode.type == NodeType.UNKNOWN || rightNode.type == NodeType.BOUNDARY) {
                            continue;
                        } else if (rightNode.type == NodeType.FOCUS) {
                            FocusNode fn = (FocusNode) rightNode;
                            ClassNodeFacade cn = fn.getIndex();
                            if (cn != null && cn.getText().matches(TechMisc.MA_TECH_INST_NAME)) {
                                Matcher matcher = DatePatterns.REGEX_WEEK_MONTH.matcher(sn.getText());
                                matcher.matches();
                                TechPeriodNode techperiodNode = new TechPeriodNode(String.format("%s%s",
                                        matcher.group(2), TechMisc.LINE_CHINESE));
                                DateNode dn = DateUtil.getDateNodeFromStr(String.format("%s日", matcher.group(1)), null);
                                nodes.set(i, dn);
                                nodes.add(i, techperiodNode);
                                i++;
                                p++;
                                break;
                            }
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                continue; // 只有unknownnode和datenode才有可能成为技术周期
            }
        }
    }
}
