package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class TrumpCard {
    public static void loadData(ArrayList<String> confLines)
            throws DataConfException, PatternSyntaxException {
        TrumpCard nono = new TrumpCard();
        ArrayList<Card_> cards = new ArrayList<Card_>();
        Pattern firstLinePtn = Pattern.compile("<plan name=\".+?\"" +
        		" visible=\"true\" expression=\".+?\">");
        StringBuilder sb = new StringBuilder();
        for(int iLine = 0; iLine < confLines.size();) {
            String line = null;
            while(iLine < confLines.size() &&
                    !(line = confLines.get(iLine)).startsWith("pattern=")){
                iLine++;
            }
            if(iLine >= confLines.size()) { break; }
            
            String ptnText = line.substring("pattern=".length()).trim();
            Pattern ptn = Pattern.compile(ptnText);
            sb.setLength(0); iLine++;
            while(iLine < confLines.size() &&
                    !(line = confLines.get(iLine)).startsWith("pattern=")){
                if(sb.length() == 0 && !firstLinePtn.matcher(line).matches()) {
                    throw new DataConfException(Param.IFIND_TRUMP_CARD_FILE,
                            iLine, "XML has a bad header");
                }
                sb.append('\t').append(line).append('\n');
                iLine++;
            }
            String cardText = sb.toString();
            if(!cardText.endsWith("</plan>\n")) {
                throw new DataConfException(Param.IFIND_TRUMP_CARD_FILE,
                        iLine, "XML has a bad tail");
            }
            
            cards.add(nono.new Card_(ptn, sb.toString()));
        }
        cards_ = cards;
    }
    
    public static String play(String text, String qid) {
        for(int iCard = 0; iCard < cards_.size(); iCard++) {
            Card_ card = cards_.get(iCard);
            Matcher mo = card.ptn_.matcher(text); 
            if(!mo.find()) continue;
            return String.format(CARD_FMT_, qid, Util.escapeXML(text), card.card_);
        }
        return null;
    }
    
    private static ArrayList<Card_> cards_;
    private static final String CARD_FMT_ =
            //header
            "<?xml version=\"1.0\"" +
    		" encoding=\"gbk\"?>\n<result><error>0</error>\n" +
    		"\t<CustomInds have=\"0\">\n\t</CustomInds>\n" +
    		"\t<qid>%s</qid>\n\t<query>%s</query>\n" +
    		"\t<nodes name=\"用户添加方案1\" major_version=\"1\"" +
    		" minor_version=\"0\" visible=\"true\">\n" +
    		//body
    		"%s" +
    		//tail
    		"\t</nodes>\n\t<skips>\n\t</skips>\n\t<transforms>\n" +
    		"\t</transforms>\n\t<relateds cmd=\"\"></relateds>\n</result>\n";
    
    private class Card_ {
        public Pattern ptn_;
        public String card_;
        public Card_(Pattern ptn, String card) {
            ptn_ = ptn; card_ = card;
        }
    }
}
