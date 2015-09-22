package com.myhexin.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.tokenize.NoParserMatch;
import com.myhexin.server.processor.ParserProcessorAbstract;
import com.myhexin.server.service.ParserServiceAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:24:19
 * @description Chunk解析服务类
 */
public class ChunkParserService extends ParserServiceAbstract {

	/**
	 * @description 无参构造函数，建议不要删除（Spring中可能会用到）
	 */
	public ChunkParserService() {
	}

	/**
	 * @descrption Chunk解析服务服务函数具体实现
	 * @return 返回类型ParserResult，成员result变量中保存String类型（json格式）结果
	 */
	@Override
	public ParserResult serve(ParserParam param) {
		String queryText = param.getQueryText();
		String queryType = param.getQueryType();
		Gson json = new Gson();

		String result = "";
		String[] chunks = queryText.trim().split("_&_");
		for (String chunk : chunks) {
			if (result.length() != 0)
				result += "_&_";
			result += getChunk(chunk);
		}

		List<String> resultList = new ArrayList<String>();
		resultList.add(result);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("errno", String.valueOf(0));
		map.put("errmsg", "");
		map.put("query", queryText);
		map.put("result", resultList);

		// 返回值
		ParserResult parserResult = new ParserResult();
		parserResult.setResult(json.toJson(map));
		return parserResult;
	}

	private String getChunk(String chunk) {
		if (!isPeopleChunk(chunk)) {
			return chunk;
		}

		ParserParam param = new ParserParam();
		param.setQueryText(chunk);

		ParserResult result = processor.process(param);

		List<ArrayList<SemanticNode>> qlist = ((ParseResult) result.getResult()).qlist;
		if (qlist != null && qlist.size() == 1
				&& qlist.get(0).get(0).isExecutive){
			String standardStatement = PhraseParserPluginAbstract.getFromListEnv(qlist.get(0), "standardStatement",String.class, false);
			if (!standardStatement.contains("_&_") && 
					!NoParserMatch.noParserWellTrieTree.mpMatchingOne(standardStatement))
				chunk = standardStatement;
		}
		return chunk;
	}

	private boolean isPeopleChunk(String chunk) {
		final Pattern peopleReg = Pattern
				.compile(".*(总经理|董事|独董|财务总监|董秘|监事|高管).*");

		if (chunk.length() < 30) {
			if (peopleReg.matcher(chunk).matches()) {
				return true;
			}
		}
		return false;
	}

	// 测试main函数
	public static void main(String[] args) {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		ChunkParserService service = (ChunkParserService) ApplicationContextHelper
				.getBean("chunkParserService");
		System.out.println(service.getChunk("董事长属马"));
		System.out.println(service.getChunk("董事长性别男"));
		System.out.println(service.getChunk("董事长未婚"));
		System.out.println(service.getChunk("董事长清华大学"));
		System.out.println(service.getChunk("董事长2013年底任职"));
		System.out.println(service.getChunk("董事长姓王"));
		System.out.println(service.getChunk("董事长本科毕业"));
	}

}
