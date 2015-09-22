package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;

import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.util.NodeWeightLevel;

public class PhraseParserPluginSyntacticIdNodeWeight extends PhraseParserPluginAbstract{

  private static final String reqiredSyntacticDescpPrfix = "main_search";
  
  public PhraseParserPluginSyntacticIdNodeWeight() {
    super("SyntacticIdNodeWeight");
  }

  @Override
  public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
    ArrayList<SemanticNode> nodes = annotation.getNodes();
    return setWeightBySyntacticId(nodes);
  }

  /**
   * 根据句式匹配的syntacticid对相关node进行weight设置
   * @param nodes
   * @return
   */
  private ArrayList<ArrayList<SemanticNode>> setWeightBySyntacticId(ArrayList<SemanticNode> nodes) {
    if (nodes == null || nodes.size() == 0)
      return null;
    
    ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    boolean matchedSyntactic = false;
    int bnStart = -1;
    for (int i = 0; i < nodes.size(); i++) {
      SemanticNode node = nodes.get(i);
      if (node.isBoundaryNode()) {
        BoundaryNode bn = (BoundaryNode) node;
        if (bn.isStart()) {
          String syntacticId = bn.getSyntacticPatternId();
          SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticId);
          if (syntPtn != null) {
            String desc = syntPtn.getDescription();
            matchedSyntactic = desc != null ? desc.startsWith(reqiredSyntacticDescpPrfix) : false;
          }
          bnStart = i;
        }
      } else if (matchedSyntactic) {
        if (i == bnStart + 2) {
          node.setSyntacticWeight(NodeWeightLevel.KEY_LEVEL);
        } else {
          node.setSyntacticWeight(NodeWeightLevel.LOW_LEVEL);
        }
      }
    }
    rlist.add(nodes);
    return rlist;
  }
  
  

}
