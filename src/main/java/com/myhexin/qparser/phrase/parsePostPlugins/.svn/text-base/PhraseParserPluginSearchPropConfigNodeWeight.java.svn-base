package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.Map;

import com.myhexin.DB.mybatis.mode.SearchPropConfig;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.resource.ResourceInst;

public class PhraseParserPluginSearchPropConfigNodeWeight extends PhraseParserPluginAbstract{

  public PhraseParserPluginSearchPropConfigNodeWeight() {
    super("SyntacticIdNodeWeight");
  }

  @Override
  public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
    ArrayList<SemanticNode> nodes = annotation.getNodes();
    return setWeightBySearchPropConfig(nodes);
  }

  private ArrayList<ArrayList<SemanticNode>> setWeightBySearchPropConfig(ArrayList<SemanticNode> nodes) {
    if (nodes == null || nodes.size() == 0)
      return null;
    
    Map<String, SearchPropConfig> searchPropConfigMap = ResourceInst.getInstance().getPropWeightMap();
    ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    for (int i = 0; i < nodes.size(); i++) {
      SemanticNode node = nodes.get(i);
      
      if (node.isStrNode()) {
        setStrNodeWeight(searchPropConfigMap, (StrNode) node);
      } else if (node.isFocusNode()) {
        StrNode strNode = ((FocusNode) node).getString();
        setStrNodeWeight(searchPropConfigMap, strNode);
      }
    }
    rlist.add(nodes);
    return rlist;
  }
  
  private void setStrNodeWeight(Map<String, SearchPropConfig> searchPropConfigMap, StrNode strNode) {
    if (strNode != null) {
      String[] props = strNode.props.split("\\|");
      for (String prop : props) {
        if (searchPropConfigMap.containsKey(prop)) {
          Integer weight = searchPropConfigMap.get(prop).getWeight();
          strNode.setSearchPropWeight(weight);
        }
      }
    }
  }
  

}
