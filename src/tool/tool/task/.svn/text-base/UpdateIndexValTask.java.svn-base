package tool.task;

import java.util.TimerTask;

import org.w3c.dom.Document;

import conf.stock.StockIndexConfUpdate;

/***
 * 更新stock_index.xml配置文件的定时任务
 * 
 * @author Administrator
 * 
 */
public class UpdateIndexValTask extends TimerTask {

	public UpdateIndexValTask(StockIndexConfUpdate updater, Document doc,
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

		if (updater != null && doc != null && path != null) {
			updater.updateXMLDate(doc);
			updater.writeToXML(doc, path);
		}
	}
}
