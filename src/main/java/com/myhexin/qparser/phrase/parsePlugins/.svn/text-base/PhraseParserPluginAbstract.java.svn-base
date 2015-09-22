package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.ResourceInst;
//import com.myhexin.qparser.tool.encode.xml.GetXmlFromObject;

public abstract class PhraseParserPluginAbstract {
    public String strTitle = "Plugin_Abstract";
    protected String pluginDesc = "#plugin# Detail of what the plugin do";
    //protected StringBuilder logsb_ = new StringBuilder();
    //protected Environment ENV = null;//环境
    
    public String getPluginDesc() {
    	return  pluginDesc;
    }
    
    public PhraseParserPluginAbstract(String title) {
        strTitle = title;
        
        Map<String, String> pluginDescMap = ResourceInst.getInstance().getPluginDescMap(); 
        if(pluginDescMap!=null && pluginDescMap.get(this.getClass().getSimpleName())!=null) {
        	pluginDesc = pluginDescMap.get(this.getClass().getSimpleName());
        }
    }
    
    public void init()
    {
        return;
    }
    
    /*private ArrayList<ArrayList<SemanticNode>> processProxy(String query, ArrayList<SemanticNode> nodes) {
    	String fileName = query.hashCode()+"-"+this.getClass().getSimpleName();
    	new GetXmlFromObject("unit_test/"+fileName+"Before.xml").createXML(nodes);
    	HashMap<String, Object> tempENV = process(null,nodes);//暂时这样,不然修改太多了
    	ArrayList<ArrayList<SemanticNode>> list = (ArrayList<ArrayList<SemanticNode>>) tempENV.get("resultList");
    	new GetXmlFromObject("unit_test/"+fileName+"After.xml").createXML(list);
        return list;
    }*/
    
    public abstract ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation);
    //Environment ENV ,//环境
	//ArrayList<SemanticNode> nodes //节点列表

    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }
    
    protected final StringBuilder getLogsb_(Environment ENV){
    	if (ENV.containsKey("logsb_")) 
    		return ENV.get("logsb_", StringBuilder.class, false);    	
    	return null;
    }
    
    protected final StringBuilder getLogsb_( ArrayList<SemanticNode> nodes){
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		if(listEnv.containsKey("queryEnv")){
    			Environment queryEnv = listEnv.get("queryEnv",Environment.class, false);
    			if (queryEnv.containsKey("logsb_")) 
    	    		return queryEnv.get("logsb_", StringBuilder.class, false);    
    		}
    	}
    	return null;
    }
    
    protected final boolean addToListEnv(ArrayList<SemanticNode> nodes,String key,Object value,boolean overWrite){
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		listEnv.put(key, value, overWrite);
    		return true;
    	}
    	return false;
   }
    
   @SuppressWarnings("unchecked")
   public static final <T> T getFromListEnv(ArrayList<SemanticNode> nodes,String key,Class<T> clazz,boolean remove){
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		if(listEnv.containsKey(key))
    			return (T)listEnv.get(key, remove);
    		if(clazz == String.class)
    			return (T)"";
    	}
    	return null;
   }
   
   
   protected String _nodesHtmlSingleMap(String name, Map<Integer, SemanticNode> indexMap) {
	   if(indexMap.isEmpty()==false) {
		   StringBuilder buf = new StringBuilder();
			Iterator<Integer> it = indexMap.keySet().iterator();
			while(it.hasNext()) {
					Integer k = it.next();
					SemanticNode val = indexMap.get(k);
					if(val==null) continue;
					buf.append("&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append("["+k+"]").append(val.toString()).append("<BR>");
					if(val.isFocusNode() && ((FocusNode)val).hasIndex() && ((FocusNode)val).getIndex()!=null) {
						FocusNode fNode =(FocusNode)val;
						ClassNodeFacade cf = fNode.getIndex();
						String alreadyPrintText = null;
						if(cf!=null) {
							alreadyPrintText = cf.getText();
							List<PropNodeFacade> props = cf.getAllProps();
							if(props!=null) {
								for(PropNodeFacade prop : props) {
									if(prop.getValue()!=null) {
										buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append(prop.getText()).append('=').append(prop.getValue().toString()).append("<BR>");
									}else{
										buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append(prop.getText()).append("=null").append("<BR>");
									}
								}
							}
						}

		   				if(fNode.focusList!=null) {
		   					for(FocusItem item : fNode.focusList) {
		   						ClassNodeFacade index2 = item.getIndex();
		   						if(index2!=null && index2.getText()!=null && !index2.getText().equals(alreadyPrintText)) {
		   							buf.append("-&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append("[focuslist]").append(index2.toString()).append("<BR>");
		   							List<PropNodeFacade> props = cf.getAllProps();
		   							if(props!=null) {
		   								for(PropNodeFacade prop : props) {
		   									if(prop.getValue()!=null) {
		   										buf.append("-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append(prop.getText()).append('=').append(prop.getValue().toString()).append("<BR>");
		   									}else{
		   										buf.append("-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[").append(name).append(']').append(prop.getText()).append("=null").append("<BR>");
		   									}
		   								}
		   							}
		   						}
		   					}
		   				}
					}
			}
			return buf.toString();
		}else{
			return null;
		}
   }
   
   protected String _nodesHtmlAllBindings(ArrayList<SemanticNode> nodes) {
	   	StringBuilder  buf = new StringBuilder();
	   	for(SemanticNode sn : nodes) {
	   		if(sn.getType()==NodeType.FOCUS){
	   			FocusNode fn = (FocusNode) sn;
				if(fn.hasIndex() && fn.getIndex()!=null) {
					buf.append(sn.toString());
					if(fn.isBoundToIndex()) {
		   				buf.append(sn.toString()).append("|b-index|").append(sn.getBoundToIndexPropInfo());
		   			}else if(fn.isBoundToSynt) {
		   				buf.append(sn.toString()).append("|b-synt");
		   			}
					buf.append("<BR>");
					
					ClassNodeFacade index = fn.getIndex();
					List<PropNodeFacade> props = index.getAllProps();
					if(props!=null) {
						for(PropNodeFacade prop : props) {
							if(prop.getValue()!=null)
								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(prop.getText()).append('=').append(prop.getValue().toString()).append("<BR>");
							else{
								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(prop.getText()).append("=null").append("<BR>");
							}
						}
					}
				}else{
					buf.append(sn.toString()).append("<BR>");
				}
	   		}
	   		else if(sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() ) {
	   			BoundaryNode bNode = (BoundaryNode)sn;
	   			buf.append(sn.toString()).append("<BR>");
	   			SyntacticPatternExtParseInfo extInfo = bNode.extInfo;
	   			if(extInfo==null) continue;
	   			
	   			
	   			//缺省指标
	   			String msg = _nodesHtmlSingleMap("absentDefalutIndexMap", extInfo.absentDefalutIndexMap);
	   			if(msg!=null) buf.append(msg);
	   			
	   			msg = _nodesHtmlSingleMap("fixedArgumentMap", extInfo.fixedArgumentMap);
	   			if(msg!=null) buf.append(msg);
	   			
	   			msg = _nodesHtmlSingleMap("semanticPropsMap", extInfo.semanticPropsMap);
	   			if(msg!=null) buf.append(msg);
	   			
	   			if(extInfo.existedIndexNode!=null && extInfo.existedIndexNode.type == NodeType.FOCUS && ((FocusNode)extInfo.existedIndexNode).hasIndex()) {
	   				FocusNode fNode = (FocusNode)extInfo.existedIndexNode;
	   				ClassNodeFacade index = fNode.getIndex();
	   				
	   				if (index != null) {
	   					PropNodeFacade nDateProp = index.getNDateProp();
	   					if(nDateProp!=null && nDateProp.getValue()!=null) {
	   						buf.append("&nbsp;&nbsp;&nbsp;&nbsp;[existedIndexNode]").append(nDateProp.getText()).append('=').append(nDateProp.getValue().toString()).append("<BR>");
	   					}
	   					
	   					List<PropNodeFacade> propList = index.getClassifiedProps(PropType.DATE);
	   					if(propList!=null) {
	   						for(PropNodeFacade pn: propList) {
	   							if (pn.isDateProp() && pn.getValue() != null) {
	   								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;[existedIndexNode]").append(pn.getText()).append('=').append(pn.getValue().toString()).append("<BR>");
	   	   						}
	   						}
	   					}
	   				}
	   				
	   				
	   			}
	   			
	   			Iterator iter = extInfo.referToIndexNodeMap.entrySet().iterator();
	        	while (iter.hasNext()) {
	        		Map.Entry entry = (Map.Entry) iter.next();
	        		SemanticNode referToIndexNode = (SemanticNode) entry.getValue();
	        		if(referToIndexNode!=null && referToIndexNode.isBoundToIndex()) {
	        			buf.append("&nbsp;&nbsp;&nbsp;&nbsp;[referToIndexNodeMap]").append(referToIndexNode.toString()).append("|b-index|").append(referToIndexNode.getBoundToIndexPropInfo()).append("<BR>");
	        		}
	        	}
	   		}
	   		else
	   		{
	   			if(sn.isBoundToIndex()) {
	   				buf.append(sn.toString()).append("|b-index|").append(sn.getBoundToIndexPropInfo()).append("<BR>");
	   			}else if(sn.isBoundToSynt) {
	   				buf.append(sn.toString()).append("|b-synt").append("<BR>");
	   			}else{
	   				buf.append(sn.toString()).append("<BR>");
	   			}
	   		}
	   		
	   		
	   	}
	   	return buf.toString();
   }
   
   /* 
    * 由于本插件extInfo.referToIndexNodeMap中指标的绑定
    * 所以只打印这部分绑定信息
    * 
    * @param nodes
    * @return
    */
   /*protected String _nodesHtml(ArrayList<SemanticNode> nodes) {
   	StringBuilder  buf = new StringBuilder();
   	for(SemanticNode sn : nodes) {
   		
   		//PhraseParserPluginSimpleBindStrToIndex
   		if(sn.getType()==NodeType.FOCUS){
				FocusNode fn = (FocusNode) sn;
				if(fn.hasIndex() && fn.getIndex()!=null) {
					buf.append(fn.toString()).append("<BR>");
					ClassNodeFacade index = fn.getIndex();
					List<PropNodeFacade> props = index.getClassifiedProps(PropType.STR);
					if(props!=null) {
						for(PropNodeFacade prop : props) {
							if(prop.isStrProp() && prop.getValue()!=null) {
								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(prop.getText()).append('=').append(prop.getValue().toString()).append("<BR>");
							}
						}
					}
				}else{
					buf.append(fn.toString()).append("<BR>");
				}
   		}else if(sn.getType()==NodeType.STR_VAL) {
   			if(sn.isBoundToIndex()) {
   				buf.append(sn.toString()).append("|b-index|").append(sn.getBoundToIndexPropInfo()).append("<BR>");
   			}else{
   				buf.append(sn.toString()).append("<BR>");
   			}
   			
   		}
   		
   		//PhraseParserPluginMatchSyntacticPatternsByChunk
   		//只绑定了existedIndexNode, 和extInfo.referToIndexNodeMap中的DataNode
   		else if(sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() ) {
   			BoundaryNode bNode = (BoundaryNode)sn;
   			buf.append(sn.toString()).append("<BR>");
   			SyntacticPatternExtParseInfo extInfo = bNode.extInfo;
   			if(extInfo==null) continue;
   			if(extInfo.existedIndexNode==null) continue;
   			if(extInfo.existedIndexNode.type == NodeType.FOCUS && ((FocusNode)extInfo.existedIndexNode).hasIndex()) {
   				FocusNode fNode = (FocusNode)extInfo.existedIndexNode;
   				ClassNodeFacade index = fNode.getIndex();
   				if (index != null) {
   					PropNodeFacade nDateProp = index.getNDateProp();
   					if(nDateProp!=null && nDateProp.getValue()!=null) {
   						buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(nDateProp.getText()).append('=').append(nDateProp.getValue().toString()).append("<BR>");
   					}
   					
   					List<PropNodeFacade> propList = index.getClassifiedProps(PropType.DATE);
   					if(propList!=null) {
   						for(PropNodeFacade pn: propList) {
   							if (pn.isDateProp() && pn.getValue() != null) {
   								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(nDateProp.getText()).append('=').append(nDateProp.getValue().toString()).append("<BR>");
   	   						}
   						}
   					}
   				}
   			}
   			
   			Iterator iter = extInfo.referToIndexNodeMap.entrySet().iterator();
        	while (iter.hasNext()) {
        		Map.Entry entry = (Map.Entry) iter.next();
        		SemanticNode referToIndexNode = (SemanticNode) entry.getValue();
        		if(referToIndexNode!=null && referToIndexNode.isBoundToIndex()) {
        			buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(referToIndexNode.toString()).append("|b-index|").append(referToIndexNode.getBoundToIndexPropInfo()).append("<BR>");
        		}
        	}
   		}
   		
   		
   		
   		else {
   			buf.append(sn.toString()).append("<BR>");
   		}
   	}
   	return buf.toString();
   }*/
   
   
   
   
   
   
   
   
   
   
   
    
    /*public void print(ArrayList<ArrayList<SemanticNode>> qlist) {
    	if(qlist!=null) {
    		System.out.println(strTitle);
    		for(ArrayList<SemanticNode> nodes : qlist) {
    			
    			Iterator iterator = new SyntacticIteratorImpl(nodes);
    	        if (!iterator.hasNext())
    				continue;
    	    	while (iterator.hasNext()) {
    	    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    	    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
    	    		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.extInfo;
    	    		
    	    		ArrayList<Integer> elelist;
    	        	int start = 0;
    	            for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
    	                for(int pos: elelist) {
    	                    if(pos == -1) {
    	                    	SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
    	                        
    	                        if(defaultNode != null && defaultNode.getType() == NodeType.FOCUS) {
    	            	            FocusNode fNode = (FocusNode)defaultNode;
    	            	            print(fNode);
    	                        }
    	                        //continue;
    	                    } else {
    	                    	start = pos;
    	    	                SemanticNode sn = nodes.get(boundaryInfos.bStart + pos);
    	    	                
    	    	                if(sn.getType() != NodeType.FOCUS)
    	    	                    continue;
    	    	                FocusNode fNode = (FocusNode)sn;
    	    	                // skip self node
    	    	                print(fNode);
    	                    }
    	                }
    	            }
    	    	}
    			
    			for(SemanticNode node : nodes) {
    				System.out.println("[NODE]" + node.getText() + ": " + node.toString());
    				print(node);
    			}
    			System.out.println("#");
    		}
    		System.out.println("#" + strTitle + "##################");
    	}
    }*/
    
    /*private void print(SemanticNode node) {
    	if(node.isFocusNode()) {
			FocusNode fNode = (FocusNode) node;
			if(fNode.getIndex()!=null) {
				 List<PropNode> props = fNode.getIndex().getAllProps();
				 for(PropNode prop : props) {
					 if(prop.getValue()!=null) {
						 System.out.print("    \t[PROP]");
						 System.out.println(prop.getText()  + " : " + prop.getValue().toString());
					 }else{
						 System.out.print("    \t[PROP]");
						 System.out.println(prop.getText()  + " : NULL");
					 }
					 
				 }
			}else{
				System.out.println("    \t[INDEX] NULL");
			}
		}
    }*/
}

