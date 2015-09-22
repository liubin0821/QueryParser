package fundconf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.util.Util;


public class FundConfGenerator {
	public static LinkedHashMap<String, FundIndex> indexList = new LinkedHashMap<String, FundIndex>();
	public static ArrayList<String> propType = new ArrayList<String>();
	private static final String inputFN = "./src/tool/fundconf/fund.xml";
	private static final String ontoFN = "./src/tool/fundconf/fund_onto.xml";
	private static final String indexFN = "./src/tool/fundconf/fund_index.xml";
	private static final String dictFN = "./src/tool/fundconf/fund_onto.dict";
	
	public static void main(String[] args) throws Exception {
	    org.apache.log4j.PropertyConfigurator.configure("./conf/log4j.properties");
		Document doc = Util.readXMLFile(inputFN, true);
		Node node = doc.getDocumentElement().getElementsByTagName("nodes").item(0);
		assert(node.getNodeType() == Node.ELEMENT_NODE);
		Element fundRoot = (Element)((Element)node).getElementsByTagName("node").item(0);
		if(fundRoot.getNodeName().equals("_基金全部指标")) {
		    System.err.println("bad?");
		    System.exit(-1);
		}
		procFundNode(fundRoot);
		System.out.println(String.format("Got %d index", indexList.size()));
		geneOntoXml();
		geneIndXml();
//		geneifindDict();

	}
	
	private static Document creatDoc() throws ParserConfigurationException {
	    DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
	    domfac.setNamespaceAware(true);
	    DocumentBuilder dombuilder = domfac.newDocumentBuilder();
	    Document doc = dombuilder.newDocument();
	    doc.setXmlStandalone(true);
	    return doc;
	}

    private static void geneOntoXml() throws Exception {
        Document doc = creatDoc();
        Element root = doc.createElement("node");
        doc.appendChild(root);
	    for(FundIndex index : indexList.values()){
	    	Element classEle = doc.createElement("class");
	    	root.appendChild(classEle);
	    	
	    	classEle.setAttribute("label", index.getTitle());
	    	classEle.setAttribute("data_src", "IFIND");
	    	
	    	Element superElem = doc.createElement("superclass");
	    	classEle.appendChild(superElem);
	    	superElem.appendChild(doc.createTextNode("null"));
	    	
	    	Element subElem = doc.createElement("superclass");
            classEle.appendChild(subElem);
            subElem.appendChild(doc.createTextNode("null"));
	    	
	    	
	    	Element valueProp = doc.createElement("prop");
			classEle.appendChild(valueProp);
			
    		if(index.getType().equals("dt_date") ){
    			valueProp.setAttribute("label", "_日期");
    			valueProp.setAttribute("type", "DATE");
    			if(index.getUnit() != null){
    				valueProp.setAttribute("unit", index.getUnit());
    			}else{
    				valueProp.setAttribute("unit", "null");
    			}
    		}else if(index.getType().equals("dt_double")
    				|| index.getType().equals("dt_integer")){
    			valueProp.setAttribute("label", "_数值");
    			valueProp.setAttribute("type", "NUM");
    			if(index.getUnit() != null){
    				valueProp.setAttribute("unit",index.getUnit());
    			}else{
    				valueProp.setAttribute("unit","unit");
    			}
    		}else{
    			String str = "_"+index.getTitle();
    			valueProp.setAttribute("label", str);
    			valueProp.setAttribute("type", "STR");
    			if(index.getUnit() != null){
    				valueProp.setAttribute("unit",index.getUnit());
    			}else{
    				valueProp.setAttribute("unit","unit");
    			}
    		}
    		
	        
	    	ArrayList<FundParam> propList = index.paramList;
	    	if(propList.size() == 0) continue;
	    		    	
	    	for(FundParam par : propList){
	    	    Element paramEle = doc.createElement("prop");
	    		classEle.appendChild(paramEle);
	    		paramEle.setAttribute("label", par.getParam_title());
	    		if(par.getParam_type().equals("dt_string")){
	    			paramEle.setAttribute("type", "STR");
	    		}else if(par.getParam_type().equals("dt_integer") ||
	    				par.getParam_type().equals("dt_double")){
	    			paramEle.setAttribute("type", "NUM");
	    		}else{
	    			paramEle.setAttribute("type", "DATE");
	    		} 
	    		paramEle.setAttribute("unit", "unit");
	    	}	    	
	    }
	    
	    outputDom(doc, ontoFN);
	}
    
    private static void outputDom(Document doc, String outputFN) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", 4);
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult outStream = new StreamResult(outputFN);
        transformer.transform(new DOMSource(doc), outStream);
    }

    /*
	private static void geneifindDict() throws IOException {
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("./ifind.dict"), "utf-8"));
		for(FoudIndex index : indexList){
			bw.write("#"+index.getTitle()+"/*Cate=pretreat;Seg=;Value=onto_class:;$");
			bw.newLine();
		}
		for(String str : propType){
			bw.write("#"+str+"/*Cate=pretreat;Seg=;Value=onto_prop:;$");
			bw.newLine();
		}
		bw.close();
	}
*/
	private static void geneIndXml() throws Exception {
	    Document doc = creatDoc();
        Element root = doc.createElement("indexes");
        doc.appendChild(root);
        
	    for(FundIndex index : indexList.values()){
	    	Element indexEle = doc.createElement("index");
	    	root.appendChild(indexEle);
	    	
	    	indexEle.setAttribute("id", index.getName());
	    	indexEle.setAttribute("title",index.getTitle());
	    	if(index.getUnit_list() !=null){
	    		indexEle.setAttribute("unit-list", index.getUnit_list());
	    	}
	    	indexEle.setAttribute("pub-unit", "");
	    	
	    	ArrayList<FundParam> paramList = index.paramList;
	    	if(paramList.size() == 0) {
	    		continue;
	    	}
	    	
	    	Element paramsEle = doc.createElement("params");
	    	for(FundParam par : paramList){
	    	    Element paramEle = doc.createElement("param");
	    		paramEle.setAttribute("title", par.getParam_title());
	    		paramEle.setAttribute("ifind_type", par.getParam_type());
	    		paramEle.setAttribute("name", par.getParam_name());
	    		if(par.getParam_list() != null)
	    		    paramEle.setAttribute("list_name", par.getParam_list());
	    		if(par.getParam_default() != null){
	    			paramEle.setAttribute("default_val",par.getParam_default());
	    		}else{
	    			paramEle.setAttribute("default_val","");
	    		}
	    		paramsEle.appendChild(paramEle);
	    	}
	    	indexEle.appendChild(paramsEle);
	    } 
	    
	    outputDom(doc, indexFN);
	}

	private static void procFundNode(Element node) throws IOException {
	    NodeList children = node.getElementsByTagName("node");
	    for(int i = 0; i < children.getLength(); i++) {
	        Element indexNode = (Element) children.item(i);
	        if(indexNode.getAttribute("isgroup").length() > 0) { continue; }
	        
	        FundIndex index = new FundIndex();
	        index.setName(indexNode.getAttribute("name"));
	        index.setTitle(indexNode.getAttribute("title"));
	        index.setType(indexNode.getAttribute("type"));
	        index.setUnit(indexNode.getAttribute("unit"));
	        index.setUnit_list(indexNode.getAttribute("unit_list")); 
	        FundIndex oldIndex = indexList.get(index.title);
	        if(indexList.containsKey(index.title)) {
	            Element pp = (Element) indexNode.getParentNode().getParentNode();
	            System.err.println(String.format("重复：%s_%s:%s vs %s",
	                    pp.getAttribute("name"), index.title, index.name, oldIndex.name));
	        } else {
	            indexList.put(index.getTitle(), index);
	        }
	        
	        
	        Element params = (Element)node.getElementsByTagName("params").item(0);
	        if(params != null) {
	            handleParamNode(params, index);
	        }
	    }
	}
    
	private static void handleParamNode(Element paramNode, FundIndex index) {
        NodeList children = paramNode.getElementsByTagName("param");;
        for (int i = 0; i < children.getLength(); i++) {
            Element node = (Element) children.item(i);

            FundParam param = new FundParam();
            
            param.setParam_name(node.getAttribute("name"));
            param.setParam_type(node.getAttribute("type"));
            param.setParam_title(node.getAttribute("title"));
            param.setParam_list(node.getAttribute("param_list"));
            param.setParam_default(node.getAttribute("default"));
            index.paramList.add(param);         
        }	    
    }
}
