package com.myhexin.qparser.trietree;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class TrieTreeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTrieTreeStringArray() {
        String[] patterns = {"均线","中国制造","中国人","中国人民","中国人民的","中华民族","好样的", "人民", "线人", "国人", "民族"};
        TrieTree tt = new TrieTree(patterns);
        for (String pattern : patterns) {
        	if (pattern != null && !pattern.equals(""))
        		assertEquals(true, tt.mpMatchingOne(pattern));
        	else
        		assertEquals(false, tt.mpMatchingOne(pattern));
        }
        assertEquals(false, tt.mpMatchingOne("国中"));
        assertEquals(true, tt.mpMatchingOne("中国人都是好样的"));
        assertEquals(5, tt.mpMatchingResult("中国人民的未来").size()); // 人民:[2:3] 国人:[1:2] 中国人:[0:2] 中国人民:[0:3] 中国人民的:[0:4]
	}

	@Test
	public void testTrieTreeHashSetOfString() throws DataConfException {
        String noParserDict = "./src/test/com/myhexin/qparser/trietree/no_parser_dict.txt";
        List<String> lines = Util.readTxtFile(noParserDict, true);
        HashSet<String> dict = new HashSet<String>();
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String word = lines.get(iLine).trim();
            dict.add(word);
        }
        TrieTree tt = new TrieTree(dict); 
        for (String pattern : dict) {
        	if (pattern != null && !pattern.equals(""))
        		assertEquals(true, tt.mpMatchingOne(pattern));
        	else
        		assertEquals(false, tt.mpMatchingOne(pattern));
        }
        assertEquals(true, tt.mpMatchingOne("中国人都是好样的"));
        assertEquals(false, tt.mpMatchingOne("国中"));
        assertEquals(5, tt.mpMatchingResult("中国人民的").size()); // 人民:[2:3] 国人:[1:2] 中国人:[0:2] 中国人民:[0:3] 中国人民的:[0:4]
	}

	@Ignore
	public void testMpMatching() {
		
	}

	@Test
	public void testMpMatchingOne() {
		String[] patterns = {"均线","中国制造","中国人","中国人民","中国人民的","中华民族","好样的", "人民", "线人", "国人", "民族"};
        TrieTree tt = new TrieTree(patterns);
		assertEquals(true, tt.mpMatchingOne("中国人"));
		assertEquals(true, tt.mpMatchingOne("中国人民"));
		assertEquals(false, tt.mpMatchingOne("人生"));
	}

	@Test
	public void testMpMatchingResult() {
		String[] patterns = {"均线","中国制造","中国人","中国人民","中国人民的","中华民族","好样的", "人民", "线人", "国人", "民族"};
        TrieTree tt = new TrieTree(patterns);
		assertEquals(5, tt.mpMatchingResult("中国人民的").size()); // 人民:[2:3] 国人:[1:2] 中国人:[0:2] 中国人民:[0:3] 中国人民的:[0:4];
	}

	@Ignore
	public void testPrintMatchingResult() {
		
	}

}
