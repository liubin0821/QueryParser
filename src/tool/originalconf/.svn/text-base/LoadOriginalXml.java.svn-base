package originalconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class LoadOriginalXml {
    private static String originalFile = "./src/tool/originalconf/stock.xml";
    private static HashMap<String, OriginalIndex> originalIndexInfos = load(originalFile);
    
    public OriginalIndex getOriginalIndexByIndexName(String indexName){
        if(!originalIndexInfos.containsKey(indexName)){
            return null;
        }
        return originalIndexInfos.get(indexName);
    }

    private static HashMap<String, OriginalIndex> load(String fileName) {
        HashMap<String, OriginalIndex> allOriginalIndex = new HashMap<String, OriginalIndex>();
        NodeList nodes;
        try {
            nodes = loadFile(fileName);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (XPathExpressionException e) {
            System.out.println(e.getMessage());
            return null;
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node nodeI = nodes.item(i);
            Node nodeP = nodeI.getParentNode().getParentNode();
            String pStr = null;
            if(nodeP!=null){
                pStr=nodeP.getAttributes().getNamedItem("name").getNodeValue();
            }
            Node nodeTitle = nodeI.getAttributes().getNamedItem("title");
            if(nodeTitle==null){
                continue;
            }
            String title1 = nodeTitle.getNodeValue().toLowerCase();
            String title2 = title1.replace("-", ".");//统一
            String title3 = title2.replace(".", "");
            String type = nodeI.getAttributes().getNamedItem("type").getNodeValue();
            
            OriginalIndex originalIndex = new OriginalIndex();
            originalIndex.setParent(pStr);
            originalIndex.setTitle(title1);
            originalIndex.setType(type);
            try {
                loadOriginalParams(originalIndex,nodeI);
            } catch (XPathExpressionException e) {
                System.out.println("XPath有问题");
            }
            allOriginalIndex.put(title1, originalIndex);
            allOriginalIndex.put(title2, originalIndex);
            allOriginalIndex.put(title3, originalIndex);
        }
        return allOriginalIndex;
    }


    private static void loadOriginalParams(OriginalIndex originalIndex,
            Node nodeI) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        XPathExpression xPathExpression = xPath.compile("child::params/param");
        NodeList params = (NodeList) xPathExpression.evaluate(nodeI,
                XPathConstants.NODESET);
        if (params != null) {
            for (int i = 0; i < params.getLength(); i++) {
                HashMap<String, String> prop = new HashMap<String, String>();
                Node paramsI = (Node) params.item(i);
                String paramsName = paramsI.getAttributes()
                        .getNamedItem("name").getNodeValue();
                String paramsType = paramsI.getAttributes()
                        .getNamedItem("type").getNodeValue();
                String title = paramsI.getAttributes().getNamedItem("title")
                        .getNodeValue();
                OriginalParam param = new OriginalParam();

                param.setTitle(title);
                param.setType(paramsType);

                originalIndex.addParam(param);
            }
        }

    }


    private static NodeList loadFile(String fileName) throws FileNotFoundException,
            XPathExpressionException {
        System.out.println("开始读取原始文件……");
        File xmlDocument = new File(fileName);
        InputSource inputSource = new InputSource(new FileInputStream(
                xmlDocument));
        // 创建 XpathExpression 对象
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        XPathExpression xPathExpression = xPath
                .compile("//node[not(@isgroup)]");
        // 结合 XML 文件和 XPATH 的表达式就可以得到相关节点的内容
        NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource,
                XPathConstants.NODESET);
        System.out.println("读取原始文件结束，返回记录数：" + nodes.getLength());
        return nodes;
    }
}
