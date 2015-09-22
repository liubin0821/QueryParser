package originalconf;

import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.util.Util;

public class getQuoteGroupInfo {

    public static void main(String args[]) {
        HashMap<String, SemanticNode> allOnto = new HashMap<String, SemanticNode>();
        try {
            Document doc;
            doc = Util.readXMLFile("./data/stock/stock_onto.xml", true);
            allOnto = OntoXmlReader.loadOnto(doc);
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
    }
}
