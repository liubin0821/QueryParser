package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.CommonOutput;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

/**
 * 返回给用户的解析结果
 */
public class ParseResult {
    /** URL for indexer search */
    public String searchUrl_ = "";
    /** parse result for WINDOW project, formatted in Base64 encoded JSON */
    public String windRlt_ = "";
    /** parse result (an XML string) for ifind client */
    public String ifindClientRlt_ = "";
    /** user interface data from/to web, formatted in Base64 encoded JSON*/
    public String uiData_ = "";
    /** info for query suggest module */
    public String suggestData_ = "";
    /** process log **/
    public String processLog = "";
    /** standard chinese representation*/
    public ArrayList<ArrayList<SemanticNode>> qlist = null;
    public List<String> standardQueries = null;
    public List<Integer> standardQueriesScore = null;
    public List<List<String>> standardQueriesIndex = null; // index列表的列表，每个query一个列表
    public List<List<String>> standardQueriesIndexWithProp = null; // index列表的列表，每个query一个列表
    public List<List<String>> standardQueriesSyntacticSemanticIds = null; // 句式号、语义号列表的列表，每个query一个列表，句式号|语义号
    public List<String> luaExpression = null;
    public List<String> wordSegment=null;/**问句的分词结果*/
	public String getSearchUrl_() {
		return searchUrl_;
	}
	public void setSearchUrl_(String searchUrl_) {
		this.searchUrl_ = searchUrl_;
	}
	public String getWindRlt_() {
		return windRlt_;
	}
	public void setWindRlt_(String windRlt_) {
		this.windRlt_ = windRlt_;
	}
	public String getIfindClientRlt_() {
		return ifindClientRlt_;
	}
	public void setIfindClientRlt_(String ifindClientRlt_) {
		this.ifindClientRlt_ = ifindClientRlt_;
	}
	public String getUiData_() {
		return uiData_;
	}
	public void setUiData_(String uiData_) {
		this.uiData_ = uiData_;
	}
	public String getSuggestData_() {
		return suggestData_;
	}
	public void setSuggestData_(String suggestData_) {
		this.suggestData_ = suggestData_;
	}
	public String getProcessLog() {
		return processLog;
	}
	public void setProcessLog(String processLog) {
		this.processLog = processLog;
	}
	public List<String> getStandardQueries() {
		return standardQueries;
	}
	public void setStandardQueries(List<String> standardQueries) {
		this.standardQueries = standardQueries;
	}
	
    /**
     * get Query Score; 
     */
    public static List<Integer> getQueryScores(ArrayList<ArrayList<SemanticNode>> qlist) {
        List<Integer> scores = new ArrayList<Integer>();
        if (qlist == null || qlist.size() == 0) {
            scores.add(0);
        } else {
            for (int i = 0; i < qlist.size(); i++) {
                scores.add(qlist.get(i).get(0).score);
            }
        }
        return scores;
    }
    
    /**
     * 取问句的句式语义ID
     */
    public static List<List<String>> getQueriesSyntacticSemanticIds(ArrayList<ArrayList<SemanticNode>> qlist) {
    	List<List<String>> idsList = new ArrayList<List<String>>();
        if (qlist == null || qlist.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < qlist.size(); i++) {
            	List<String> ids = getQueryIds(qlist.get(i));
            	if (ids != null)
            		idsList.add(ids);
            }
        }
        return idsList;
    }
    
    /**
     * 取问句的句式语义ID
     */
    public static List<String> getQueryIds(ArrayList<SemanticNode> nodes) {
    	List<String> ids = new ArrayList<String>();
        if (nodes == null || nodes.size() == 0) {
            return null;
        } else {
        	int boundaryStart = 0;
            //int boundaryEnd = nodes.size() - 1;
            boolean bMathch = false;
            boolean bEndMatch = false;
            for (int i = 0; i < nodes.size(); i++) {
                SemanticNode sNode = nodes.get(i);
                if (sNode.type == NodeType.BOUNDARY) {
                    BoundaryNode bNode = (BoundaryNode) sNode;
                    if (bNode.isStart()) {
                        bMathch = true;
                        boundaryStart = i;
                    } else if (bNode.isEnd()) {
                        bEndMatch = true;
                        //boundaryEnd = i;
                    }
                }
                if (bMathch && bEndMatch) {
                	String id = "";
                    BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryStart);
                    String patternId = bNode.getSyntacticPatternId();
                    // YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
                    if (BoundaryNode.getImplicitPattern(patternId) != null) {
                        id = patternId + "|" + patternId;
                    } else {
	                    SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
	                    String bindid = syntPtn.getSemanticBind().getId();
	                    String semanticBindRoot = syntPtn.getSemanticBind().getSemanticBindToIds();
	                    if (bindid != null) {
	                    	id = patternId + "|" + bindid;
	                    } else {
		                    id = patternId + "|" + semanticBindRoot;
	                    }
                    }
                	if (!id.equals(""))
                    	ids.add(id);
                    bMathch = false;
                    bEndMatch = false;
                }
            }
        }
        return ids;
    }
    
    /**
     * change to ChineseRepresentation
     */
    public static List<String> toStandardQueries(Query query, ArrayList<ArrayList<SemanticNode>> qlist, String split_) {
        List<String> standardQueries = new ArrayList<String>();
        if (qlist == null || qlist.size() == 0) {
            standardQueries.add(query.text);
        } else {
            for (int i = 0; i < qlist.size(); i++) {
                List<String> stds = CommonOutput.changeToStandardStatement(qlist.get(i), split_);
                //Statement.buildTree(qlist.get(i), split_);
                standardQueries.addAll(stds);
            }
        }
        return standardQueries;
    }
  
 
	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-8 下午2:52:35
     * @description:   	
     * @param qlist2
     * @return
     */
    public static List<String> getLuaExpression(ArrayList<ArrayList<SemanticNode>> qlist) {
    	 List<String> luaExpress = new ArrayList<String>();
         if (qlist == null || qlist.size() == 0) {
        	 luaExpress.add("");
         } else {
             for (int i = 0; i < qlist.size(); i++) {
            	 if(qlist.get(i).get(0).type==NodeType.ENV){
            		 Environment listEnv = (Environment) qlist.get(i).get(0);
            		 if(listEnv.containsKey("luaResult"))
            			 luaExpress.add(listEnv.get("luaResult", String.class, true));
            	 }
            	 //luaExpress.add(qlist.get(i).get(0).luaResult);
             }
         }
         return luaExpress;
    }
    
    
  //取到句式ID
  		public  String getSyntacitcIds() {
  			StringBuilder s = new StringBuilder();
  			if(qlist!=null && qlist.size()>0) {
  			   ArrayList<SemanticNode> nodes = qlist.get(0);
  			   for (SemanticNode sn : nodes) {
  			       if (sn.type == NodeType.BOUNDARY) {
  		                BoundaryNode bn = (BoundaryNode) sn;
  		                if (bn.isStart()) {
  		                	if(s.length()>0) s.append(" ");
  		                    s.append(bn.getSyntacticPatternId());
  		                }
  		            }
  				}
  			} 
  			return s.toString();
  		}
  		
  		//取到语义ID
  		public  String getSemanticIds() {
  			StringBuilder s = new StringBuilder();
  			if(qlist!=null && qlist.size()>0) {
  				   ArrayList<SemanticNode> nodes = qlist.get(0);
  				   for (SemanticNode sn : nodes) {
  				       if (sn.type == NodeType.BOUNDARY) {
  			                BoundaryNode bn = (BoundaryNode) sn;
  			                if (bn.isStart()) {
  			                    String syntacticPatternId = bn.getSyntacticPatternId();
  			                    if(s.length()>0) s.append(" ");
  			                    s.append(syntacticPatternId);
  			                    if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
  			                    	SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
  			                    	String semanticPatternId = syntPtn.getSemanticBind().getId();
  			                        semanticPatternId = semanticPatternId!=null ? semanticPatternId : syntPtn.getSemanticBind().getSemanticBindToIds();
  			                        s.append(":").append(semanticPatternId);
  			                    }
  			                }
  			            }
  					}
  				 } 
  			
  			return s.toString();
  		}  
  		
  		
  	//取到句式和语义ID
  	public  String getSyntacticSemanticIds() {
  		StringBuilder s = new StringBuilder();
  		if(qlist!=null && qlist.size()>0) {
  			ArrayList<SemanticNode> nodes = qlist.get(0);
  			for (SemanticNode sn : nodes) {
  				if (sn.type == NodeType.BOUNDARY) {
  					BoundaryNode bn = (BoundaryNode) sn;
  		            if (bn.isStart()) {
  		            	String syntacticPatternId = bn.getSyntacticPatternId();
  		                if(s.length()>0) s.append(" ");
  		                s.append(syntacticPatternId);
  		                if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
  		                	SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
  		                	SemanticBind semanticBind = syntPtn.getSemanticBind();
  		                	ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
  		      			
  		                	if (bindToList != null) {
  		                		s.append(":");
  		                		int i=0;
  		                		for (SemanticBindTo sbt : bindToList) {
  		                			int semanticId = sbt.getBindToId();
  		                			s.append(semanticId);
  		                			if(i++<bindToList.size()-1) {
  		                				s.append(",");
  		                			}
  		                		}
  		                	}
  		                }
  		            }
  				}
  			}
  		} 
  			
  		return s.toString();
  	}
  	
  	public String getIndexNames() {
  		StringBuilder buf = new StringBuilder();
  		if(qlist!=null && qlist.size()>0) {
  			ArrayList<SemanticNode> nodes = qlist.get(0);
  			SyntacticIteratorImpl iterator = new SyntacticIteratorImpl(nodes);
  			while (iterator.hasNext()) {
  				BoundaryInfos boundaryInfos = iterator.next();
  				String patternId = boundaryInfos.syntacticPatternId;
  				if(patternId==null)  {
  					for(int i=boundaryInfos.bStart;i<boundaryInfos.bEnd && i<nodes.size();i++) {
  						SemanticNode node = nodes.get(i);
  						if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  							buf.append(((FocusNode)node).getIndex().getText()).append(" ");
  						}
  					}
  				}else if(patternId.equals("FREE_VAR") || patternId.equals("STR_INSTANCE")) {
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					if(bNode!=null && bNode.extInfo!=null) {
  						SemanticNode node = null;
  						int indexId = 1;
  						int newIndexId = bNode.extInfo.getElementNodePos(indexId);
  				    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
  				    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
  				    	}else{
  				    		indexId =boundaryInfos.bStart+ newIndexId;
  				    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
  				    	}
  				    	if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  							buf.append(((FocusNode)node).getIndex().getText()).append(" ");
  						}
  					}
  				}else if(patternId.equals("KEY_VALUE")) {
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					SemanticNode keyNode = ConditionBuilderUtil.getNodeKeyValue(1, bNode, boundaryInfos, nodes);
  			    	SemanticNode valueNode = ConditionBuilderUtil.getNodeKeyValue(2, bNode, boundaryInfos, nodes);
  			    	
  			    	if(keyNode!=null && keyNode.isFocusNode() && ((FocusNode)keyNode).hasIndex() && ((FocusNode)keyNode).getIndex()!=null) {
							buf.append(((FocusNode)keyNode).getIndex().getText()).append(" ");
  			    	}
  			    	if(valueNode!=null && valueNode.isFocusNode() && ((FocusNode)valueNode).hasIndex() && ((FocusNode)valueNode).getIndex()!=null) {
							buf.append(((FocusNode)valueNode).getIndex().getText()).append(" ");
					}
  				}else{
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId); //句式
  					if (syntPtn == null) {
  						continue;
  					}
  					SemanticBind semanticBind = syntPtn.getSemanticBind();
  					ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
  					if (bindToList != null) {
  						List<ConditionModel> prevConds=null; //处理嵌套语义,这里存的是上一个语义的OpModel
  						for (SemanticBindTo sbt : bindToList) {
  							int semanticId = sbt.getBindToId();
  							ArrayList<SemanticBindToArgument> args = sbt.getSemanticBindToArguments();
  							for (SemanticBindToArgument arg : args) {
  								SemanticNode node = null;
  								int elementId = arg.getElementId();
  								if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) { //是指标,创建指标condition
  									if(arg.getBindToType() == BindToType.SYNTACTIC_ELEMENT){
  										if (arg.getSource() == Source.FIXED && bNode.extInfo != null && bNode.extInfo.fixedArgumentMap != null) {
  											node = bNode.extInfo.fixedArgumentMap.get(elementId);
  										} else if (bNode.extInfo != null) {
  											int newIndexId = bNode.extInfo.getElementNodePos(elementId);

  											if (newIndexId == -1 && bNode.extInfo != null && bNode.extInfo.absentDefalutIndexMap != null) {
  												node = bNode.extInfo.absentDefalutIndexMap.get(elementId);
  											} else {
  												newIndexId = boundaryInfos.bStart + newIndexId;
  												node = nodes.get(newIndexId);
  											}
  										}
  									}
  								}
  								
  								if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  		  							buf.append(((FocusNode)node).getIndex().getText()).append(" ");
  		  						}
  							}
  						}
  					}
  				}
  			}
  			
  		}
  		return buf.toString().trim();
  	}
  	
  	public int getScore() {
  		if(qlist!=null && qlist.size()>0) {
  			ArrayList<SemanticNode> nodes = qlist.get(0);
  			if(nodes!=null && nodes.get(0)!=null) {
  				return nodes.get(0).getScore();
  			}
  		}
  		
  		return 0;
  	}
}


