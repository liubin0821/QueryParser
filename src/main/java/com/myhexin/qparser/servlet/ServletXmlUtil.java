package com.myhexin.qparser.servlet;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.model.RefCodeInfo;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;
//import com.myhexin.qparser.util.condition.model.BackTestCondAnnotationComparator;

class ServletXmlUtil {
	
	public static String getConditionXmlOutput(String query,  String postDataStr, long timeDistance, List<BackTestCondAnnotation> jsonResults) {
		return buildXmlResult(query, postDataStr, timeDistance, jsonResults);
	}
	
	public static String getConditionOnly(List<BackTestCondAnnotation> jsonResults) {
		BackTestCondAnnotation jsonResult = null;
		if(jsonResults!=null && jsonResults.size()>0) {
			jsonResult = jsonResults.get(0);
			return jsonResult.getResultCondJson();
		}/*else{
			jsonResult = new BackTestCondAnnotation();
		}*/
		
		return null;
		
	}
	/*
     * 把结果按表单参数转换成相应格式
     * 
     * @param isXml
     * @param query
     * @param chunk
     * @param postDataStr
     * @param timeDistance
     * @param jsonResult
     * @return
     */
    /*private String getResultJson(String isXml, String query, String chunk, String postDataStr, long timeDistance, List<BackTestCondAnnotation> jsonResults) {
    	String result = null;
    	if(isXml!=null && isXml.equals("1")) {
			result = buildXmlResult(query, postDataStr, timeDistance, jsonResults);
		}else if(chunk!=null && chunk.equals("1")) {
			BackTestCondAnnotation jsonResult = null;
			if(jsonResults!=null && jsonResults.size()>0) {
				jsonResult = jsonResults.get(0);
			}else{
				jsonResult = new BackTestCondAnnotation();
			}
			
			result = jsonResult.getChunkJson();
		}else{
			BackTestCondAnnotation jsonResult = null;
			if(jsonResults!=null && jsonResults.size()>0) {
				jsonResult = jsonResults.get(0);
			}else{
				jsonResult = new BackTestCondAnnotation();
			}
			
			result = jsonResult.getResultCondJson();
		}
    	return result;
    	
    }*/
    
    private static void buildXmlResult_createResult(Document doc, Element response, Element inst, BackTestCondAnnotation jsonResult) {
    	buildXmlResult_createResult(doc, response, inst, jsonResult, false);
    }
    
    private static void buildXmlResult_createResult(Document doc, Element response, Element inst, BackTestCondAnnotation jsonResult, boolean createMultResult) {
    	Element multi_result = null;
    	if(createMultResult) {
    		multi_result  = doc.createElement("multi_result");
    		response.appendChild(multi_result);
    	}else{
    		multi_result = response;
    	}
    	
		
		String json = "";
		String score = "";
		String qType = "";
		String outputs = "";
		String domains = "";
		String condHtml = Consts.STR_BLANK;
		if(jsonResult!=null) {
			json = jsonResult.getResultCondJson();
			score = jsonResult.getScoreStr();
			qType = jsonResult.getQueryType();
			outputs = jsonResult.getOutputs();
			domains = jsonResult.getDomainStr();
			condHtml = jsonResult.getConditionHtml();
		}
		inst  = doc.createElement("str");
		inst.setAttribute("name", "result");
		inst.setTextContent(json);
		//logger_.info(jsonResult.getResultCondJson());
		multi_result.appendChild(inst);
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "chunks_info");
		inst.setTextContent(Consts.STR_BLANK); //为了兼容php端
		multi_result.appendChild(inst);
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "score");
		inst.setTextContent(score);
		multi_result.appendChild(inst);
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "query_type");
		inst.setTextContent(qType) ;
		multi_result.appendChild(inst);
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "standard_query");
		inst.setTextContent(outputs) ;
		multi_result.appendChild(inst);
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "domains");
		inst.setTextContent(domains) ;
		multi_result.appendChild(inst);
		
		
		//指标计算公式节点
		inst  = doc.createElement("str");
		inst.setAttribute("name", "calc_expr_str");
		multi_result.appendChild(inst);
		inst.setTextContent(Consts.STR_BLANK);
		/*if(jsonResult!=null && jsonResult.getCalcExprTreeStr()!=null) {
			inst.setTextContent(jsonResult.getCalcExprTreeStr()) ;
		}else{
			inst.setTextContent("");
		}*/
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "calc_expr");
		multi_result.appendChild(inst);
		inst.setTextContent(Consts.STR_BLANK);
		/*if(jsonResult!=null && jsonResult.getCalcExprTree()!=null) {
			BackTestExprTreeNode calcExprTree = jsonResult.getCalcExprTree();
			addCalcExprNode(doc, inst, calcExprTree, 0);
		}else{
			inst.setTextContent("");
		}*/
		
		inst  = doc.createElement("str");
		inst.setAttribute("name", "condHtml");
		inst.setTextContent(condHtml);
		multi_result.appendChild(inst);
    }
    
    
    /*private static void addCalcExprNode(Document doc, Element parent,BackTestExprTreeNode calcExprNode, int index) {
    	Element node  = doc.createElement("expr_node");
    	node.setAttribute("value", calcExprNode.getValue());
    	node.setAttribute("index", index+"");
    	parent.appendChild(node);
    	List<String> propNameList = calcExprNode.getPropNames();
    	if(propNameList!=null) {
    		for(String s : propNameList) {
    			Element attrnode  = doc.createElement("attr");
    			attrnode.setAttribute("name", s);
    			attrnode.setAttribute("value", calcExprNode.getPropValue(s));
    			node.appendChild(attrnode);
    		}
    	}
    	
    	if(calcExprNode.getChildrenList()!=null && calcExprNode.getChildrenList().size()>0) {
    		for(int i=0;i<calcExprNode.getChildrenList().size();i++) {
    			BackTestExprTreeNode child = calcExprNode.getChildrenList().get(i);
    			addCalcExprNode(doc, node, child, i);
    		}
    	}
    }*/
	
	/**
     * 返回XML结果,给Servlet用的
     * ??写在这里???
     * 
     * @param query
     * @param postData
     * @param timeDistance
     * @param jsonResult
     * @return
     */
    private static String buildXmlResult(String query, String postData, long timeDistance, List<BackTestCondAnnotation> jsonResults)  {
    	try{
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element response = doc.createElement("response");
			doc.appendChild(response);
			
			
			Element lst = doc.createElement("lst");
			lst.setAttribute("name", "responseHeader");
			response.appendChild(lst);
			
			Element inst  = doc.createElement("int");
			inst.setAttribute("name", "status");
			inst.setTextContent("0");
			lst.appendChild(inst);
			
			inst  = doc.createElement("int");
			inst.setAttribute("name", "QTime");
			inst.setTextContent(timeDistance+"");
			lst.appendChild(inst);
			
			inst  = doc.createElement("str");
			inst.setAttribute("name", "handler");
			inst.setTextContent(ConditionServlet.class.getName());
			lst.appendChild(inst);
			
			/*inst  = doc.createElement("str");
			inst.setAttribute("name", "host");
			inst.setTextContent(ResourceInst.getInstance().getHostName());
			lst.appendChild(inst);*/
			
			inst  = doc.createElement("str");
			inst.setAttribute("name", "ip");
			inst.setTextContent(RefCodeInfo.getInstance().getIp());
			lst.appendChild(inst);
			
			Element param  = doc.createElement("lst");
			param.setAttribute("name", "params");
			lst.appendChild(param);
			
			inst  = doc.createElement("str");
			inst.setAttribute("name", "query");
			inst.setTextContent(query);
			param.appendChild(inst);
			
			inst  = doc.createElement("str");
			inst.setAttribute("name", "postData");
			inst.setTextContent(postData);
			param.appendChild(inst);
		
			BackTestCondAnnotation jsonResult = null;
			if(jsonResults!=null && jsonResults.size()>0) {
				jsonResult = jsonResults.get(0);
			}else{
				jsonResult = new BackTestCondAnnotation();
			}
			
			//默认结果
			//result
			buildXmlResult_createResult(doc, response, inst, jsonResult);
			
			//其他结果
			if(jsonResults!=null && jsonResults.size()>1) {
				for(int i=1;i<jsonResults.size();i++) {
					jsonResult = jsonResults.get(i);
					buildXmlResult_createResult(doc, response, inst, jsonResult, true);
					
				}
			}else{ //加一个空的multi_result,避免web端出错
				buildXmlResult_createResult(doc, response, inst, null, true);
				
			}
			
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult result = new StreamResult(writer);
	        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.transform(domSource, result);
	        return writer.toString();
    	}catch(Exception e) {
    		e.printStackTrace();
    		StringBuilder buf = new StringBuilder();
    		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    		buf.append("<response>");
    		buf.append("<str name=\"result\">");
    		
    		BackTestCondAnnotation jsonResult = null;
			if(jsonResults!=null && jsonResults.size()>0) {
				jsonResult = jsonResults.get(0);
			}else{
				jsonResult = new BackTestCondAnnotation();
			}
    		
    		buf.append(jsonResult.getResultCondJson());
    		buf.append("</str>");
    		buf.append("<str name=\"chunks_info\">");
    		buf.append(Consts.STR_BLANK);
    		buf.append("</str>");
    		buf.append("</response>");
    		return buf.toString();
    	}
    } 
}
