package com.myhexin.qparser.util.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.myhexin.qparser.phrase.util.Consts;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateResultBuilder {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(TemplateResultBuilder.class.getName());
	
	private Configuration cfg_;
	private static Set<String> notExistsTempleteSet =new HashSet<String>();
	/*public static FilenameFilterEndWithFtl ftl_filefilter = new FilenameFilterEndWithFtl();
	static class FilenameFilterEndWithFtl implements FilenameFilter{
		@Override
		public boolean accept(File dir, String name) {
			if(name.endsWith(".ftl")) {
				return true;
			}else
				return false;
		}
	}*/
	
	public TemplateResultBuilder(String templateFolder) {
		
		cfg_ = new Configuration(); // 创建一个Configuration实例
		/*existsTempleteSet = new HashSet<String>();
		
		//把模板文件加进去
		existsTempleteSet = new HashSet<String>();
		File templateFolderFileObj = new File(templateFolder);
		if(templateFolderFileObj.exists() && templateFolderFileObj.isDirectory()) {
			String[] templateFiles = templateFolderFileObj.list(ftl_filefilter);
			if(templateFiles!=null) {
				for(String fn : templateFiles) {
					existsTempleteSet.add(fn);
				}
			}
		}//把模板文件加进去 结束
		 */				
		cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), templateFolder);
		//System.out.println("TemplateResultBuilder(){}");
	}

	
	public void createTemplate(Map<String,?> data, String templateFileName, Writer out) {
		try {
			if(notExistsTempleteSet.contains(templateFileName)) {
				out.write(Consts.STR_BLANK);
			}
			
			Template t = cfg_.getTemplate(templateFileName);
			if(t!=null) {
				t.process(data, out);
			}else{
				if(!notExistsTempleteSet.contains(templateFileName)) {
					notExistsTempleteSet.add(templateFileName);
				}
				logger_.info("[FTL_WARNING]找不到模板:"+templateFileName);
				out.write(Consts.STR_BLANK);
			}
			
		} catch (TemplateException e) {
			//不严重的错误,所以用log.info
			logger_.info("[FTL_ERROR] 模板出错, " + e.getMessage());
		} catch (IOException e) {
			//不严重的错误,所以用log.info
			//logger_.error("找不到模板:"+templateFileName);
			logger_.info("[FTL_ERROR] 找不到模板 :" + templateFileName + ", " + e.getMessage());
			if(!notExistsTempleteSet.contains(templateFileName)) {
				notExistsTempleteSet.add(templateFileName);
			}
		}
		
    }
	
	
    public String createTemplate(Map<String,?> data, String templateFileName) {
    	Writer out = new StringWriter();
    	createTemplate(data, templateFileName, out);
    	return out.toString();
    }
}
