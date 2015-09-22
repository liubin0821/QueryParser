/**
 * 
 */
package com.myhexin.qparser.strategyoptm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.server.BaseParser;
import com.myhexin.server.ParserPlugins;

/**
 * @author chenhao
 *
 */
public class StrategyOptmParser extends BaseParser {
	private static Logger logger_ = LoggerFactory.getLogger(StrategyOptmParser.class);
	private static ParserPlugins parserPlugins = (ParserPlugins) ApplicationContextHelper
			.getBean("parserPluginsStrategyOptm");
	private static ParserPlugins parserPluginsDebug = (ParserPlugins) ApplicationContextHelper
			.getBean("parserPluginsStrategyOptmDebug");
	private static String[] _stockKeyWordList = { "_股票简称", "_股票代码" };

	/* (non-Javadoc)
	 * @see com.myhexin.server.BaseParser#afterParser(com.myhexin.qparser.ParseResult)
	 */
	@Override
	protected String afterParser(ParseResult parseResult) {
		List<SemanticNode> nodeList = parseResult.qlist == null ? new ArrayList<SemanticNode>() : parseResult.qlist
				.get(0);
		SyntacticIteratorImpl syntacticIteratorImpl = new SyntacticIteratorImpl(nodeList);
		List<StrategyOptmModel> models = new ArrayList<StrategyOptmModel>();
		while (syntacticIteratorImpl.hasNext()) {
			BoundaryInfos boundaryInfos = syntacticIteratorImpl.next();
			BoundaryNode boundaryNode = (BoundaryNode) nodeList.get(boundaryInfos.bStart);
			SyntacticPatternExtParseInfo extInfo = boundaryNode.extInfo;
			StrategyOptmModel model = convert(nodeList.subList(boundaryInfos.start, boundaryInfos.bEnd));
			if (extInfo != null) {
				Map<Integer, SemanticNode> indexMap = extInfo.absentDefalutIndexMap;
				if (indexMap != null) {
					for (Entry<Integer, SemanticNode> entry : indexMap.entrySet()) {
						if (entry.getValue().type == NodeType.FOCUS) {
							model.setIndexList(entry.getValue().getText());
						}
					}
				}
				Map<Integer, SemanticNode> argumentMap = extInfo.fixedArgumentMap;
				if (argumentMap != null) {
					for (Entry<Integer, SemanticNode> entry : argumentMap.entrySet()) {
						if (entry.getValue().type == NodeType.FOCUS) {
							model.setIndexList(entry.getValue().getText());
						}
					}
				}
			}
			String patternId = boundaryInfos.syntacticPatternId;
			SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId); //句式
			if (syntPtn != null) {
				SemanticBind semanticBind = syntPtn.getSemanticBind();
				ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
				for (SemanticBindTo sbt : bindToList) {
					int semanticId = sbt.getBindToId();
					SemanticOpModel opModel = SemanticCondInfo.getInstance().getSemanticOpInfo(semanticId);
					model.setOpString(opModel.getKeyWord());
				}
			}
			if (!model.isEmpty()) {
				models.add(model);
			}
		}

		return new Gson().toJson(models);
	}

	@Override
	public ParserPlugins getPlugins() {
		return parserPlugins;
	}

	@Override
	public ParserPlugins getPluginsDebug() {
		return parserPluginsDebug;
	}

	private StrategyOptmModel convert(List<SemanticNode>nodeList){
		DateNode dateNode = null;
		FocusNode focusNode = null;
		StrNode strNode = null;
		ClassNodeFacade classNode = null;
		String sInfo = "";
		int iStart = 0;
		StrategyOptmModel rtn = new StrategyOptmModel();
		for (SemanticNode node : nodeList) {
			switch (node.type) {
			case DATE:
				// handle date
				dateNode = (DateNode) node;
				if (dateNode.getDateinfo() != null && dateNode.getDateinfo().getFrom() != null
						&& dateNode.getDateinfo().getTo() != null) {
					DateInfoNode fromDateInfo = dateNode.getDateinfo().getFrom();
					DateInfoNode toDateInfo = dateNode.getDateinfo().getTo();
					String from = fromDateInfo.toString();
					String to = toDateInfo.toString();
					if (!from.equals(to)) {
						rtn.setDateList(from + "_" + to);
					} else {
						rtn.setDateList(from);
					}
				}
				break;
			case FOCUS:
				// handle index
				focusNode = (FocusNode) node;
				if (focusNode.hasIndex()) {
					classNode = focusNode.getIndex();
					
					if (classNode != null) {
						rtn.setIndexList(classNode.getText());
					}
				}

				// handle stock
				// example:
				// info:[_新闻主体]:id:300033|[_a股]:id:300033|[_公司名称]:id:300033|[_股票简称]:id:300033
				strNode = focusNode.getString();
				if (strNode != null && strNode.info != null) {
					sInfo = strNode.info;
					for (String keyword : _stockKeyWordList) {
						iStart = sInfo.indexOf(keyword, 0);
						if (iStart > 0) {
							iStart += keyword.length() + 5;// length of ]:id:->5
							if (iStart + 5 < sInfo.length()) {
								// stock is 6 digit
								sInfo = sInfo.substring(iStart, iStart + 6);
								rtn.setStkCodeList(sInfo);

							}
						}
					}
				}
				break;
			case STR_VAL:
				// handle stock
				// example:
				// { "type":"ASTOCK", "id": "600036", "str_sub_type":"_股票代码",
				// "info":"[_a股]:id:600036|[_股票代码]:id:600036", "text":"600036" }
				strNode = (StrNode) node;
				for (String keyword : _stockKeyWordList) {
					if (strNode.subType != null && strNode.subType.contains(keyword) && strNode.info != null) {
						sInfo = strNode.info;
						// length of ]:id:->5
						iStart = sInfo.indexOf(keyword, 0) + keyword.length() + 5;
						if (iStart + 5 < sInfo.length()) {
							// stock is 6 digit
							sInfo = sInfo.substring(iStart, iStart + 6);
							rtn.setStkCodeList(sInfo);
						}
					}
				}
				break;
			default:
				break;
			}
		}
		return rtn;
	}

}

