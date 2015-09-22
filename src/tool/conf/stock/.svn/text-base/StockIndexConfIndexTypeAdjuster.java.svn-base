package conf.stock;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import originalconf.LoadOriginalXml;
import originalconf.OriginalIndex;
import originalconf.OriginalParam;

import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.EnumDef.DataSource;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.ClassNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.util.Util;


public class StockIndexConfIndexTypeAdjuster {
    
    private static LoadOriginalXml originalInfo = new LoadOriginalXml();
    private static List<String> notFoundIndex = new ArrayList<String>();
    private static HashMap<String, SemanticNode>  ontoInfo ;
    
    static {
        try {
            Document doc;
            doc = Util.readXMLFile("./data/stock/stock_onto.xml", true);
            ontoInfo = OntoXmlReader.loadOnto(doc);
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
       
    }
    
    public static void main(String[] args) throws Exception {
        String oldFile = "./data/stock/stock_onto.xml";
        Document doc = Util.readXMLFile(oldFile, true);
        Element root = doc.getDocumentElement();
        NodeList infoNodes = root.getChildNodes();
        for (int i = 0; i < infoNodes.getLength(); i++) {
            Node infoNodeI = infoNodes.item(i);
            if (infoNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            resetClassNode(infoNodeI);
            }
        String newOntoFile = "./data/stock/stock_onto_new.xml";
        xml2File(doc, newOntoFile);
        System.out.println("共"+notFoundIndex.size()+"个指标没在原始文件中找到");
        String notFoundFile = "./notFound.txt";
        lines2File(notFoundIndex,notFoundFile);
    }
    
    private static void resetClassNode(Node infoNodeI) {
        NamedNodeMap infos = infoNodeI.getAttributes();
        String classTitle = infos.getNamedItem("label").getNodeValue();
        OriginalIndex originalIndex = originalInfo
                .getOriginalIndexByIndexName(classTitle);
        if (originalIndex == null) {
            ClassNode indexNode = (ClassNode) ontoInfo.get(classTitle);
            boolean notNeedAdd = !indexNode.isNumIndex()
                    && indexNode.getProps(PropType.NUM).isEmpty()||indexNode.getDataSrc()!=DataSource.IFIND;
            if (!notNeedAdd) {
                notFoundIndex.add(classTitle);
                return;
            }
        }
        NodeList otherInfos = infoNodeI.getChildNodes();
        for (int i = 0; i < otherInfos.getLength(); i++) {
            Node info = otherInfos.item(i);
            if (info.getNodeType() != Node.ELEMENT_NODE
                    || !info.getNodeName().equals("prop")) {
                continue;
            }
            resetPropNode(info, originalIndex);
        }

    }

    private static void resetPropNode(Node info, OriginalIndex originalIndex) {
        NamedNodeMap propInfos = info.getAttributes();
        String propLabel = propInfos.getNamedItem("label").getNodeValue();
        String type = propInfos.getNamedItem("type").getNodeValue();
        boolean isValProp = propLabel.charAt(0)==MiscDef.COND_VALUE_PROP_MARK;
        boolean isNumProp = type.equals("NUM");
        if (!isNumProp) {
            return;
        }else if(!isValProp){
            OriginalParam originalParam = originalIndex.getOriginalParamByParamName(propLabel);
            if(originalParam==null){
                System.out.println(originalIndex.getTitle()+"   "+propLabel+"  没找到");
                return;
            }
            String repType = getRepTypeBy(originalParam.getType());
            if(repType==null){
                System.out.println(originalIndex.getTitle()+"   "+propLabel+"  不对");
                return;
            }
            propInfos.getNamedItem("type").setNodeValue(repType);
            return;
        }
        String repType = getRepTypeBy(originalIndex.getType());
        if(repType==null){
            System.out.println(originalIndex.getTitle()+"   "+originalIndex.getType()+"  不对");
            return;
        }
        String repTitle = repType.equals("DOUBLE")?"_浮点型数值":"_整型数值";
        propInfos.getNamedItem("label").setNodeValue(repTitle);
        propInfos.getNamedItem("type").setNodeValue(repType);
    }

    private static String getRepTypeBy(String type) {
        return type.indexOf("double") != -1 ? "DOUBLE" : type
                .indexOf("integer") != -1 ? "LONG" : null;
    }

    public static void xml2File(Document doc,String fileName) {
        System.out.println("生成“"+fileName+"”文件……");
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            // 设置输出的encoding为改变gb2312
            // transformer.setOutputProperty( "encoding ", "gb2312 ");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileName);
            transformer.transform(source, result);
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;// 写文件错误
        } catch (TransformerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;// 写文件错误
        }
        System.out.println("生成结束");
    }
    
    public static void lines2File(List<String> notFoundIndex2, String fileName) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(new File(
                    fileName)));
            System.out.println("生成“" + fileName + "”文件...");
            for (int i = 0; i < notFoundIndex2.size(); i++) {
                br.write(notFoundIndex2.get(i) + "\n");
                br.flush();
            }
            System.out.println("生成结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
}
