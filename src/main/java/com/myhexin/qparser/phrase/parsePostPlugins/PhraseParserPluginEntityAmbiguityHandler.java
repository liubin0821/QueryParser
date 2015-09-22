package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.util.Consts;

public class PhraseParserPluginEntityAmbiguityHandler extends PhraseParserPluginAbstract {

  private static Map<String, String[]> ambSubTypesMap = new HashMap<String, String[]>();
  private static Map<String, String[]> ambTypesMap = new HashMap<String, String[]>();
  private static List<String> codeFormatSyb = Arrays.asList(new String[]{".", " "});
  private static String[] stockTypes = new String[]{"_股票代码", "_a股", "_股票简称"};
  private static String[] fundTypes = new String[]{"_全部基金代码", "_基金代码", "_全部基金(含未成立、已到期)代码", "_全部基金(含未成立、已到期)简称"};
  private static String[] hkTypes = new String[]{"_港股代码", "_港股", "_港股简称", "_港股全称"};
  static {
    ambSubTypesMap.put("sh", stockTypes);
    ambSubTypesMap.put("sz", stockTypes);
    ambSubTypesMap.put("股票", stockTypes);
    ambSubTypesMap.put("a股", stockTypes);
    ambSubTypesMap.put("股份", stockTypes);
    ambSubTypesMap.put("of", fundTypes);
    ambSubTypesMap.put("基金", fundTypes);
    ambSubTypesMap.put("hk", hkTypes);
    ambSubTypesMap.put("港股", hkTypes);
    
    ambTypesMap.put("ASTOCK", stockTypes);
    ambTypesMap.put("HKSTOCK", hkTypes);
    ambTypesMap.put("FUND", fundTypes);
  }
  
  public PhraseParserPluginEntityAmbiguityHandler() {
    super("EntityAmbiguityHandler");
  }

  @Override
  public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
    ArrayList<SemanticNode> nodes = annotation.getNodes();
    try{
      	return processEntityAmbiguity(nodes);
    } catch (Exception e) {

        e.printStackTrace();
      ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
      rlist.add(nodes);
      return rlist;
    }
    
  }
  
  /**
   * 对基金代码，港股代码，股票代码等进行type的修正，对歧义无用节点进行删除
   * @param nodes
   * @return
   */
  private ArrayList<ArrayList<SemanticNode>> processEntityAmbiguity(ArrayList<SemanticNode> nodes) {
    if (nodes == null || nodes.size() == 0)
      return null;
    
    ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    List<SemanticNode> removableNodes = new ArrayList<SemanticNode>();
    List<Integer> entityIndice = findEntityIndice(nodes);
    if (entityIndice.size() > 0) {
      boolean findTarget = false;
      for (int i = 0; i < nodes.size(); i++) {
        SemanticNode node = nodes.get(i);
        String text = node.getText();
        String[] ambTypes = ambSubTypesMap.get(text);
        if (ambTypes != null) { 

          removableNodes.add(node);
          
          // left direction search
          int targetEntityIndex = i - 1;
          int distance = 1;
          while (distance <= 2) {
            if (targetEntityIndex >= 0) {
              
              SemanticNode targetNode = nodes.get(targetEntityIndex);
              
              if (targetNode.isBoundaryNode()) {
                targetEntityIndex--;
                continue;
              } else if (targetNode.isFocusNode()) {
                targetNode = ((FocusNode) targetNode).getString();
              }
              
              distance++;

              
              if (codeFormatSyb.contains(targetNode.getText()) || Arrays.asList(ambTypes).contains(targetNode.getText())) {
                removableNodes.add(targetNode);
              } else if (entityIndice.contains(targetEntityIndex)) {
                for (String ambType : ambTypes) {
                  StrNode strTargetNdoe = (StrNode) targetNode;
                  String id = strTargetNdoe.getId(ambType);
                  if (!Consts.STR_BLANK.equals(id)) {
                    strTargetNdoe.addNoAmbiguitySubType(ambType);
                    strTargetNdoe.setDefaultIndexSubtype(ambType);
                    
                    String bigType = getType(ambType);
                    strTargetNdoe.setNoAmbiguityType(bigType);
                    modifyIdByType(bigType, ambType, strTargetNdoe.getText(), strTargetNdoe);
                    findTarget = true;
                  }
                }
                break;
              }
              
              targetEntityIndex--;
            } else {
              break;
            }
          }

          //right direction search 1 slot
          if (!findTarget) {
            removableNodes.add(node);
            targetEntityIndex = i + 1;
            distance = 1;
            while (distance <= 1) {
              if (targetEntityIndex < nodes.size()) {
                SemanticNode targetNode = nodes.get(targetEntityIndex);
                
                if (targetNode.isBoundaryNode()) {
                  targetEntityIndex++;
                  continue;
                }
                
                if (" ".equals(targetNode.getText())) {
                  targetEntityIndex++;
                  removableNodes.add(targetNode);
                  continue;
                }
                
                distance++;
                
                if (entityIndice.contains(targetEntityIndex)) {
                  for (String ambType : ambTypes) {
                    StrNode strTargetNdoe = (StrNode) targetNode;
                    String id = strTargetNdoe.getId(ambType);
                    if (!Consts.STR_BLANK.equals(id)) {
                      strTargetNdoe.addNoAmbiguitySubType(ambType);
                      strTargetNdoe.setDefaultIndexSubtype(ambType);
                      
                      String bigType = getType(ambType);
                      strTargetNdoe.setNoAmbiguityType(bigType);
                      modifyIdByType(bigType, ambType, strTargetNdoe.getText(), strTargetNdoe);
                      findTarget = true;
                    }
                  }
                }
                targetEntityIndex++;
              } else {
                break;
              }
            }
          }
        }
      }

      if (findTarget) {
        // remove useless node
        for (SemanticNode node : removableNodes)  {
          nodes.remove(node);
        }
        removableNodes.clear();
        
        // clear bounary node
        for (int i = 0; i < nodes.size(); i++) {
          SemanticNode node = nodes.get(i);
          if (i > 0) {
            SemanticNode prevNode = nodes.get(i - 1);
            if (prevNode.isBoundaryNode() && node.isBoundaryNode() 
                && ((BoundaryNode)prevNode).isStart() && ((BoundaryNode)node).isEnd()) {
              removableNodes.add(prevNode);
              removableNodes.add(node);
            }
          }
        }
        
        // remove boundary node
        for (SemanticNode node : removableNodes)  {
          nodes.remove(node);
        }
        removableNodes.clear();
        
      }
    }
    rlist.add(nodes);
    return rlist;
    
  }
  
  private String getType(String ambType) {
    for (String type : ambTypesMap.keySet()) 
      for (String ambTypeDef : ambTypesMap.get(type)) 
        if (ambTypeDef.equals(ambType)) 
          return type;
    return null;
  }
  
  private void modifyIdByType(String type, String ambType, String text, StrNode strNode) {
    if ("FUND".equals(type)) {
      strNode.setIdForce(ambType, text.concat("f"));
    } else if ("HKSTOCK".equals(type) && text.length() < 5 && text.matches("[0-9]+")) {
      int remain = 5 - text.length();
      for (int i = 0; i < remain; i++) {
        text = "0".concat(text);
      }
      strNode.setIdForce(ambType, text);
    }
  }
  
  private List<Integer> findEntityIndice(ArrayList<SemanticNode> nodes) {
    List<Integer> entityIndice = new ArrayList<Integer>();
    String[][] allCodeTypes = {stockTypes, fundTypes, hkTypes};
    for (int i = 0; i < nodes.size(); i++) {
      SemanticNode node = nodes.get(i);
      StrNode targetNode = null;
      if (node.isStrNode()) {
        targetNode = (StrNode) node;
      } else if (node.isFocusNode()) {
        targetNode = ((FocusNode) node).getString();
      }

      if (targetNode != null) {
        for (String[] types : allCodeTypes) {
          for (String type : types) {
            String id = targetNode.getId(type);
            if (!Consts.STR_BLANK.equals(id)) {
              entityIndice.add(i);
              break;
            }
          }
        }
      }
    }
    return entityIndice;
  }
}
