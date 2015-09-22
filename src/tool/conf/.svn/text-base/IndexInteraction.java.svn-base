package conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class IndexInteraction {
	public static Map<String, ArrayList<String>> indexInteractions_ = new HashMap<String, ArrayList<String>>();
	
    public static void readIndexInteractionFromFile(String fileName)
            throws FileNotFoundException, IOException {
	    //long a=System.currentTimeMillis();
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    String line = null;
	    while ((line = br.readLine()) != null) {
	    	if (line.startsWith("#"))
	    		continue;
	    	String[] oneLine = line.split(";");
	    	if(oneLine.length==2){
	    				if(indexInteractions_.containsKey(oneLine[1])){
	    					if(!indexInteractions_.get(oneLine[1]).contains(oneLine[0]))
	    					indexInteractions_.get(oneLine[1]).add(oneLine[0]);
	    				}else{
	    					ArrayList<String> temp = new ArrayList<String>();
	    					temp.add(oneLine[0]);
	    					indexInteractions_.put(oneLine[1], temp);
	    				}
	    			
	    		}else if(oneLine.length==1){
	    			if(indexInteractions_.containsKey(oneLine[0])){
	    				if(!indexInteractions_.get(oneLine[0]).contains(oneLine[0]))
    					indexInteractions_.get(oneLine[0]).add(oneLine[0]);
	    			}else{
    					ArrayList<String> temp = new ArrayList<String>();
    					temp.add(oneLine[0]);
    					indexInteractions_.put(oneLine[0], temp);
    				}
	    		}
	    		
	    	}	        	
	    }	
  
    public static void loadIndexInteraction(Document doc) throws DataConfException {
        if (null == doc) {
            throw new DataConfException(Param.INDEX_INTERACTION, 0, "多种可能型文件为空");
        }
        // 一个智能回答文集的一个family对应着一个SmartAnswerEntry
        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();
            XPathExpression expr = xpath.compile("//map");
            NodeList maps=  (NodeList) expr.evaluate(doc,XPathConstants.NODESET);
            int mapCount = maps.getLength();
            Node map = null;
            NodeList mapMembers = null;
            for( int i = 0; i < mapCount; i++ ){
                map = maps.item(i);
                String mapKey = map.getAttributes().getNamedItem("key").getNodeValue();
                mapMembers = map.getChildNodes();
                int memberCount = mapMembers.getLength();
                Node member = null;
                ArrayList<String> interactions = new ArrayList<String>();
                for( int j = 0; j < memberCount; j++ ){
                    member = mapMembers.item(j);
                    String name = member.getNodeName();
                    if(name.equals("interaction") ){
                       interactions.add(member.getTextContent());
                    }
                }
                if(interactions.size()>1)
                indexInteractions_.put(mapKey, interactions);
            }
        } catch (XPathExpressionException e) {
            throw new DataConfException(Param.INDEX_INTERACTION, 0, "多种可能型文件格式错误:" + e.getMessage());
        }
    }
    
    
    /**
    * 
    * @param fileName
    */
    public static void writeIndexInteractionToFile(String fileName){
    	  DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
    	  try{
    		 DocumentBuilder db=dbf.newDocumentBuilder();
    	     Document doc=db.newDocument();
    	     Element root=doc.createElement("maps");
    	     doc.appendChild(root);
    	     for(String key :indexInteractions_.keySet()){
    	    	 Element map =doc.createElement("map");
				map.setAttribute("key",key);
       	         for(String interaction:indexInteractions_.get(key)){
       	        	 Element interactionNode=doc.createElement("interaction");
           	         Text name=doc.createTextNode(interaction);
           	         interactionNode.appendChild(name);
           	         map.appendChild(interactionNode);
       	         }
       	         root.appendChild(map);
    	     }
    	     doc2XmlFile(doc,fileName);
    	    }catch(Exception e){
    	          e.printStackTrace(); }
    	 }
   
   
   public static boolean doc2XmlFile(Document document, String filename) {
	   boolean flag = true;
	   try {
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    /** 编码 */
	     transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(new File(filename));
	    transformer.transform(source, result);
	   } catch (Exception ex) {
	    flag = false;
	    ex.printStackTrace();
	   }
	   return flag;
	}   
   
   public static void main(String[] args) throws DataConfException, FileNotFoundException, IOException{
	loadIndexInteraction(Util.readXMLFile("./data/index_interaction.xml", true));
	//readIndexInteractionFromFile("E:/workspace/QueryPaser2-branch/src/tool/conf/指标别名.txt");    
	writeIndexInteractionToFile("E:/workspace/QueryPaser2-branch/src/tool/conf/index_interaction1.xml");
	System.err.println("SUCCESS");
	   
   }
   
   
} 
