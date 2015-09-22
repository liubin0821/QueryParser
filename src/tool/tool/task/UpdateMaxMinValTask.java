package tool.task;


import java.util.TimerTask;

import org.w3c.dom.Document;

import conf.stock.StockIndexConfUpdate;
import conf.stock.servlet.StockIndexServlet;
/***
 * 定时写入stock_index.xml中最大值最小值的TimerTask
 * @author Administrator
 *
 */
public class UpdateMaxMinValTask extends TimerTask {

	public UpdateMaxMinValTask(StockIndexConfUpdate updater, Document doc,
			String path) {
		this.updater = updater;
		this.doc = doc;
		this.path = path;
	}

	private StockIndexConfUpdate updater = null;
	private Document doc = null;
	private String path = null;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		/** 需要更新才写入文件 **/
		if (updater != null && doc != null && path != null
				&& StockIndexServlet.count() > 0) {
			updater.writeToXML(doc, path);
			StockIndexServlet.countClear() ;
		}
	}
}
