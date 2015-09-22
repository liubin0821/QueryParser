package bench;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

/**
 * the Class BenchManager
 */
public class FileBenchManager extends BenchManager{
    /**
     * @rm.param parser
     * @rm.param stdQueryFileName
     * @throws IOException
     */
    public FileBenchManager(String stdQueryFileName) throws IOException {
    	super(stdQueryFileName);
    }
    
    public void loadStdQuery(String stdQueryFileName){
    	try {
            ArrayList<String> lines = Util.readTxtFile(stdQueryFileName, false);
            String line = null;
            for(int i = 0; i < lines.size(); i++){           	
                line = lines.get(i).trim();
                if(line.length() == 0) continue;
                int pos = line.indexOf(" ") + 1;
                
                BenchQuery bq = new BenchQuery(line.substring(pos));		
                Query query = ParserAgent.parse(bq);
                if(!ParserAgent.parseBenchQueryFileds(query, bq, this)) {
                    System.err.println("<<< 标准集问句解析失败：" + bq.text);
                    continue;
                }
                try {
					addBenchQuery(bq, false);
				} catch (CommandException e) {
					e.printStackTrace();
				}
            }
        } catch (DataConfException e) {
        }
    }
    
    /**
     * 将目前添加的新问句保存至正式的回归测试集
     * @rm.param textFile 保存的文本文件
     * @throws FileNotFoundException 
     * @throws UnsupportedEncodingException 
     */
    public void saveNewQuery(String textFile) throws IOException {
    	BufferedWriter bw = null;
    	
    	if(textFile != ""){
    		bw = new BufferedWriter(new OutputStreamWriter(
    				new FileOutputStream(textFile) ,"utf-8"));
    	}else{
    		bw = new BufferedWriter(new OutputStreamWriter(
    				new FileOutputStream("src/tool/rm.bench/standardQuery.txt") ,"utf-8"));
    	}
    	    	
    	for(SimiQuerySet querySet : getCode2SimiQuery().values()){
    		
//    		querySet.moveNewQueryToStd();
    		for(BenchQuery bq : querySet.getStdQuery()){
    			bw.write(bq.type+" "+bq.text);
				bw.newLine();
				bw.flush();
    		}    		
    	}    
    	
		bw.close();			
    }
    
    protected boolean save(BenchQuery benchQuery){
    	return true;
    }

	/* (non-Javadoc)
	 * @see rm.bench.BenchManager#addBenchQuery(rm.bench.BenchQuery, boolean)
	 */
	@Override
	public boolean addBenchQuery(BenchQuery query, boolean force)
			throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delBenchQuery(BenchQuery query) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveBenchQuery(BenchQuery bq) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateOldPatternDataBase(String saveFileName, boolean isCover)
			throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOldPatternDataBase(String saveFileName)
			throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}
}