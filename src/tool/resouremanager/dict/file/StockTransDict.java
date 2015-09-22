package resouremanager.dict.file;

import java.util.List;

import resouremanager.pub.DataCarrier;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

/***
 * stock_trans.dict文件数据封装
 * 单例模式
 * @author Administrator
 *
 */
public class StockTransDict extends DataCarrier {

	/**
	 * @param args
	 */
	public List<String> stockTransList = null;
	private static StockTransDict stockTransDict = null;
	
	private StockTransDict(String fileName){
		super(fileName) ;
		if(stockTransList == null){
			try {
				stockTransList = Util.readTxtFile(this.fileName);
			} catch (DataConfException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static StockTransDict getStockTransDict(String fileName){
		if(stockTransDict == null){
			stockTransDict = new StockTransDict(fileName) ; 
		} 
		return stockTransDict ;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
