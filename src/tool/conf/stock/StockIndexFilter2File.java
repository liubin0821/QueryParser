package conf.stock;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.ClassNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.util.Util;

import conf.tool.ToolUtil;

public class StockIndexFilter2File {
    private static HashMap<String, SemanticNode> ontoInfo;
    private static ArrayList<String> indexNames = new ArrayList<String>();
    static {
        try {
            Document doc;
            doc = Util.readXMLFile("./data/stock/stock_onto.xml", true);
            ontoInfo = OntoXmlReader.loadOnto(doc);
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
    }

    private void getIndexListByIndexValueType(PropType propType) {

    }

    private void getIndexListByIndexName(String indexName) {

    }

    private static void getIndexListByIndexValueUnit(Unit unit) {
        for (String indexName : ontoInfo.keySet()) {
            if (ontoInfo.get(indexName).type != NodeType.CLASS) {
                continue;
            }
            ClassNode index = (ClassNode) ontoInfo.get(indexName);
            if (!index.getValueUnits().contains(unit)) {
                continue;
            }
            indexNames.add(indexName);
        }
    }
    
    private static void removeIndexFromListByIndexName(String removeStr) {
        for (int i = 0;i<indexNames.size();i++) {
            String indexName = indexNames.get(i);
            if (indexName.contains(removeStr)) {
                indexNames.remove(i);
                i--;
            }
        }
    }

    public static void main(String[] args) {
        getIndexListByIndexValueUnit(Unit.PERCENT);
        removeIndexFromListByIndexName("增长率");
        removeIndexFromListByIndexName("涨跌幅");
        removeIndexFromListByIndexName("涨幅");
        removeIndexFromListByIndexName("跌幅");
        removeIndexFromListByIndexName("净利");
        removeIndexFromListByIndexName("利润");
        removeIndexFromListByIndexName("收益");
        removeIndexFromListByIndexName("换手");
        removeIndexFromListByIndexName("振幅");
        
        ToolUtil.write2Txt(indexNames, "./indexList.txt");
    }
    
}
