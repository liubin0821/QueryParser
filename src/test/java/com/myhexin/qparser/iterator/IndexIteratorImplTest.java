package com.myhexin.qparser.iterator;

import java.util.List;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseParser;

public class IndexIteratorImplTest {
	private static void testIndexIteratorImpl(String query) {
		Query q = new Query(query.toLowerCase());
		try {
			ParseResult pr = parser.parse(q, "## Add Index Of STR_INSTANCE");
			if (pr != null && pr.qlist != null) {
				List<SemanticNode> nodes = pr.qlist.get(0);
				System.out.println(nodes);
				IndexIteratorImpl iterator = new IndexIteratorImpl(nodes);
				while (iterator.hasNext()) {
					FocusNode fn = iterator.next();
					System.out.println(fn);
				}
				iterator.last();
				while (iterator.hasPrev()) {
					FocusNode fn = iterator.prev();
					System.out.println(fn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		testIndexIteratorImpl("董事长马云本科男");
		testIndexIteratorImpl("收盘价-开盘价");
		testIndexIteratorImpl("收盘价 开盘价均大于10");
		testIndexIteratorImpl("macd kdj金叉");
		testIndexIteratorImpl("昨日大于10");
	}

	public static PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");
	static {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	}
}
