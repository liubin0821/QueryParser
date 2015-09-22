package resouremanager.param;

import org.w3c.dom.Document;

import resouremanager.pub.DataCarrier;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

/**
 * stock_onto.xml
 * 单例模式，内存中只能存在一份stock_onto.xml
 * @author Administrator
 *
 */
public class StockOntoXML extends DataCarrier {
	private static Document ontoDoc = null  ;
	private StockOntoXML(String fileName){
		super(fileName) ;
		try {
			ontoDoc = Util.readXMLFile(fileName) ;
		} catch (DataConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Document getInstance(String fileName) throws DataConfException{
		if(ontoDoc == null)
			ontoDoc = Util.readXMLFile(fileName) ;		
		return ontoDoc ;
		
	}
}
