package com.myhexin.qparser.util.itoperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.myhexin.qparser.servlet.op.OpDb;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.xmlreader.XmlReader;

public class SemanticCondInfoProcess {

	private static Document readSemanticConfigXML(String path) throws Exception {

		return Util.readXMLFile(path);
	}

	private static Set<String> techOp = new HashSet<String>();
	static {
		String[] names = new String[] { "金叉", "死叉", "上穿","有效上穿", "低位即将金叉", "长期粘合", "走平",
				"中轨以下", "五线金叉", "躺底上勾", "三次底背离", "绿圈", "在所有均线下", "绿柱放大",
				"在所有均线上", "两次回踩", "上轨附近 ", "三线金叉", "下轨附近", "拒绝死叉", "顶背离",
				"中轨以上 ", "二次金叉", "指标创新低 ", "红柱回升", "上穿", "渔家出海", "即将金叉",
				"有效下穿", "中轨附近 ", "回踩上轨", "两次探底", "走平即将粘合", "二次死叉", "指标创新高 ",
				"金叉", "突破中轨", "红二波", "红柱高于黄线", "跌破中轨", "弱势", "即将死叉", "指标创新低",
				"上穿0轴", "0轴上首次死叉", "短线出现转向", "长期上移", "下穿", "白龙出水", "短期大于长期",
				"均线背离", "走平后上移", "低位死叉", "三线合一", "所有均线走平后上移", "高位二次死叉", "均线之吻",
				"下移 ", "反转风洞", "绿柱缩短", "二次上穿100", "喇叭口", "零下金叉", "低位",
				"上穿所有均线", "中位死叉", "强势", "顶部钝化", "均匀放量", "低位粘合", "下破", "收窄",
				"超买", "零下死叉", "多头排列", "一直走平", "指标创新高", "高位死叉", "买入信号", "底背离",
				"所有均线上移", "下轨以下", "卖出信号", "下轨以上", "底部钝化", "最有价值信号", "上穿下轨",
				"翻绿", "小绿柱", "跌破上轨", "支撑", "上破", "四线粘合向上", "均线发散", "回踩不破",
				"超卖反转", "向上发散", "垂直上移", "死叉", "回踩", "四线金叉", "将死不死", "所有均线走平",
				"短期均线多头排列", "反抽", "卷土重来", "突破上轨", "四均线强势上攻", "有效金叉", "开口张开",
				"绿柱首次缩短", "上移", "中位金叉", "中线超跌", "中位", "跌破下轨", "空头排列", "下穿0轴",
				"三线向上", "四线粘合", "高位", "上穿0值", "回踩中轨", "红圈", "低位金叉", "阻力",
				"上下震荡", "红柱缩短", "高位金叉", "拐头向下", "下移", "突破下轨", "二次上穿-100",
				"拐头向上", "中线买入", "二次突破中轨", "三次金叉", "出现第二个红柱", "二次跌破下轨", "开口缩小",
				"上轨以下", "回踩下轨", "上轨以上", "向上粘合", "低位二次金叉", "下破所有均线", "多头金叉",
				"翻红", "两次上穿", "中轨支撑", "即将拐头向上", "二次底背离", "下轨附近 ", "中轨拐头向上",
				"红柱放大", "即将上穿", "粘合", "极限宽", "短线超买", "二次顶背离", "超卖", "平行" };
		for (String s : names) {
			techOp.add(s);
		}
	}
	

	public static void insertSemanticOps(int id, String text, String word, String opClazzName, String opName, String opProp, String sonSize) {
		Connection con = null;
		String sql = "INSERT INTO parser_cond_ops (semantic_id, ch_fulltext, ex_word, opClazzName, opName, opProperty, opSonSize) VALUES(?,?,?,?,?,?,?)";
		try{
			con = OpDb.getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, text);
			pstmt.setString(3, word);
			pstmt.setString(4, opClazzName);
			pstmt.setString(5, word);
			pstmt.setString(6, opProp);
			pstmt.setString(7, sonSize);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insertIndexOps(int id,  String text, String indexOp, String indexOpVal) {
		Connection con = null;
		String sql = "INSERT INTO parser_cond_index_ops (semantic_id,ch_fulltext, index_op_nm, index_op_val) VALUES(?,?,?,?)";
		try{
			con = OpDb.getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, text);
			pstmt.setString(3, indexOp);
			pstmt.setString(4, indexOpVal);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void load(Document doc) {
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root,
				"SemanticPattern");
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			String id = XmlReader.getAttributeByName(node, "id");
			if (id == null || id.equals("")) {
				continue;
			}
			String chineseRepresentation = XmlReader
					.getChildElementValueByTagName(node,
							"ChineseRepresentation");
			if (chineseRepresentation != null
					&& chineseRepresentation.length() > 0) {
				List<String> list = new ArrayList<String>();
				StringBuilder b = new StringBuilder();
				for (int j = 0; j < chineseRepresentation.length(); j++) {
					char c = chineseRepresentation.charAt(j);
					if (c < 255) {
						if (b.length() > 0) {
							list.add(b.toString());
							b = new StringBuilder();
						}
					} else {
						b.append(c);
					}
				}
				if (b.length() > 0) {
					list.add(b.toString());
				}
				String w = list.toString();
				System.out
						.println(id + ", " + chineseRepresentation + ", " + w);
				if (w.charAt(w.length() - 1) == ',')
					w = w.substring(0, w.length() - 1);
				if (w.charAt(w.length() - 1) == ']')
					w = w.substring(0, w.length() - 1);

				if (w.length() == 1 && w.equals("["))
					w = "";
				else if (w.length() > 0 && w.charAt(0) == '[')
					w = w.substring(1, w.length());

				String opClazzName = null;
				for (String s1 : techOp) {
					if (chineseRepresentation.indexOf(s1) >= 0) {
						opClazzName = "TECH";
					}
				}

				insertSemanticOps(Integer.parseInt(id),chineseRepresentation, w, opClazzName, null, null, null);
				System.out.println(id + ", " + chineseRepresentation + ", " + w);

				if (chineseRepresentation.indexOf(">=") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, ">=", null);
				} else if (chineseRepresentation.indexOf(">") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, ">", null);
				}

				if (chineseRepresentation.indexOf("<=") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "<=", null);
				} else if (chineseRepresentation.indexOf("<") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "<", null);
				}

				if (chineseRepresentation.indexOf("+") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "+", null);
				}
				if (chineseRepresentation.indexOf("-") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "-", null);
				}
				if (chineseRepresentation.indexOf("*") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "*", null);
				}
				if (chineseRepresentation.indexOf("/") >= 0) {
					insertIndexOps(Integer.parseInt(id),
							chineseRepresentation, "/", null);
				}

			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Document doc = readSemanticConfigXML(args[0]);
		load(doc);
	}

}
