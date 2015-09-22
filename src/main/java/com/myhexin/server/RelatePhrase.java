package com.myhexin.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.phrase.KeyWordItem;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.util.Util;

public class RelatePhrase {

	public static Document getRelatePhrases(String query)
	{
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
	                .newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        document = builder.newDocument();
			Query q = new Query(query.toLowerCase());
			Document doc = Util.readXMLFile("data/stock/stock_phrase.xml", true);
            List<KeyWordItem> keywords = new ArrayList<KeyWordItem>();
            //Parser.parser.getKeywords(q, keywords);
            List<String> synIds = new ArrayList<String>();
            List<String> words = new ArrayList<String>();
            List<String> semaIds = new ArrayList<String>();
            List<String> keyGroupIds = new ArrayList<String>();
            List<String> indexGroupIds = new ArrayList<String>();
            StringBuffer sbKeys = new StringBuffer();
            for (int i = 0; i < keywords.size(); i ++)
            {
            	KeyWordItem item = keywords.get(i);
            	words.add(item.keyword);
            	if (sbKeys.length() > 0)
            	{
            		sbKeys.append(",");
            	}
            	sbKeys.append(item.keyword);
            	List<String> ids = item.synIds;
            	if (ids == null)
            	{
            		continue;
            	}
            	for (int j = 0; j < ids.size(); j ++)
            	{
            		if (containsStr(synIds, ids.get(j)))
            		{
            			continue;
            		}
            		synIds.add(ids.get(j));
            	}
            }
            for (int i = 0; i < synIds.size(); i ++)
            {
            	SyntacticPattern synPtn = PhraseInfo.getSyntacticPattern(synIds.get(i));
            	if (synPtn == null)
            	{
            		continue;
            	}
            	SemanticBind bind = synPtn.getSemanticBind();
            	if (bind != null && bind.getId() != null)
            	{
            		SemanticPattern semcPtn = PhraseInfo.getSemanticPattern(bind.getId());
            		if (semcPtn != null && !containsStr(semaIds, bind.getId()))
            		{
            			semaIds.add(bind.getId());
            		}
            	}
            }
            Element root = document.createElement("Related");
            document.appendChild(root);
            Element element = document.createElement("RelatedKeywords");
            root.appendChild(element);
            Text text = document.createTextNode(sbKeys.toString());
            element.appendChild(text);
            Element kgElem = document.createElement("RelatedKeywordGroups");
            root.appendChild(kgElem);
            Element igElem = document.createElement("RelatedIndexGroups");
            root.appendChild(igElem);
            Element rseElem = document.createElement("RelatedSemanticPatterns");
            root.appendChild(rseElem);
            Element rsyElem = document.createElement("RelatedSyntacticPatterns");
            root.appendChild(rsyElem);
     /*       XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile("");
            expression.*/
            XPather xPather = XPather.getInstance();
            xPather.extract("//SemanticPattern", doc);
            List<Node> nodes = xPather.getResultNodes();
            for (int i = 0; i < nodes.size(); i ++)
            {
            	if (nodes.get(i).getAttributes() != null 
            			&& nodes.get(i).getAttributes().getNamedItem("id") != null)
            	{
            		Attr attr = (Attr)nodes.get(i).getAttributes().getNamedItem("id");
            		if (containsStr(semaIds, attr.getValue()))
            		{
            			Node newNode = document.importNode(nodes.get(i), true);
            			rseElem.appendChild(newNode);
            			List<String> groups = getIndexOrKeywordGroup(newNode, false);
            			for (String str : groups)
            			{
            				if (!containsStr(indexGroupIds, str))
                			{
                				indexGroupIds.add(str);
                			}
            			}
            		}
            	}            	
            }
            xPather.extract("//SyntacticPattern", doc);
            nodes = xPather.getResultNodes();
            for (int i = 0; i < nodes.size(); i ++)
            {
            	if (nodes.get(i).getAttributes() != null 
            			&& nodes.get(i).getAttributes().getNamedItem("id") != null)
            	{
            		Attr attr = (Attr)nodes.get(i).getAttributes().getNamedItem("id");
            		if (containsStr(synIds, attr.getValue()))
            		{
            			Node newNode = document.importNode(nodes.get(i), true);
                    	rsyElem.appendChild(newNode);
                    	List<String> groups = getIndexOrKeywordGroup(newNode, true);
                    	for (String str : groups)
            			{
            				if (!containsStr(keyGroupIds, str))
            				{
            					keyGroupIds.add(str);
            				}         				
            			}
            		}
            	}            	
            }
            xPather.extract("//KeywordGroup", doc);
            nodes = xPather.getResultNodes();
            for (int i = 0; i < nodes.size(); i ++)
            {
            	if (nodes.get(i).getAttributes() != null 
            			&& nodes.get(i).getAttributes().getNamedItem("id") != null)
            	{
            		Attr attr = (Attr)nodes.get(i).getAttributes().getNamedItem("id");
            		if (containsStr(keyGroupIds, attr.getValue()))
            		{
            			Node newNode = document.importNode(nodes.get(i), true);
                    	kgElem.appendChild(newNode);
            		}
            	}
            }
            xPather.extract("//IndexGroup", doc);
            nodes = xPather.getResultNodes();
            for (int i = 0; i < nodes.size(); i ++)
            {
            	if (nodes.get(i).getAttributes() != null 
            			&& nodes.get(i).getAttributes().getNamedItem("id") != null)
            	{
            		Attr attr = (Attr)nodes.get(i).getAttributes().getNamedItem("id");
            		if (containsStr(indexGroupIds, attr.getValue()))
            		{
            			Node newNode = document.importNode(nodes.get(i), true);
                    	igElem.appendChild(newNode);
            		}
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	
	private static boolean containsStr(List<String> strs, String s)
	{
		for (int i = 0; i < strs.size(); i ++)
		{
			if (strs.get(i).equals(s))
			{
				return true;
			}
		}
		return false;
	}
	
	private static List<String> getIndexOrKeywordGroup(Node node, boolean bKeywordGroup)
	{
		List<String> groups = new ArrayList<String>();
		NodeList children = node.getChildNodes();
		String attrName = bKeywordGroup ? "KeywordGroup" : "SpecificIndexGroup";
		for (int i = 0; i < children.getLength(); i ++)
		{
			Node child = children.item(i);
			String name = child.getNodeName();
			if (!name.equals("Argument") && !name.equals("SyntacticElement"))
			{
				continue;
			}
			if (child.getAttributes() != null 
					&& child.getAttributes().getNamedItem(attrName) != null)
			{
				Attr attr = (Attr)child.getAttributes().getNamedItem(attrName);
				String value = attr.getValue().trim();
				if (value.length() > 0)
				{
					groups.add(value);
				}
			}
		}
		return groups;
	}
}
