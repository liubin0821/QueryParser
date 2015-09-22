package com.myhexin.qparser;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import junit.framework.TestCase;

public class TestPhraseInfo extends TestCase {
    public void test() {
        System.out.print("Test PhraseInfo\n");
        try{
        	ApplicationContextHelper.loadApplicationContext();
        }
        catch(Exception e)
        {
            System.out.println("======= message =======");
            System.out.println(e.getMessage());
        }
        System.out.println("================");
        System.out.println(PhraseInfo.getSemanticPattern("11").toString());
        System.out.println(PhraseInfo.getSemanticPattern("11").getUiRepresentation());
        System.out.println(PhraseInfo.getSemanticPattern("11").getChineseRepresentation());
        System.out.println(PhraseInfo.getSemanticPattern("11").getDescription());
        assertEquals("UIRepresentation:(11)", PhraseInfo.getSemanticPattern("11").getUiRepresentation(), "$1 粘合 $2");
        assertEquals("UIRepresentation:(11)", PhraseInfo.getSemanticPattern("11").getChineseRepresentation(), "$1、$2粘合");
        assertEquals("UIRepresentation:(11)", PhraseInfo.getSemanticPattern("11").getSemanticArgument(1, false).getType(), SemanticArgument.SemanArgType.INDEXLIST);
    }
    
}
