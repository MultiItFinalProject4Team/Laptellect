package com.multi.laptellect.recommend.komoran;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import java.util.List;

public class KomoranTest {
    public static void main(String[] args) throws Exception {

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setFWDic("user_data/fwdic.user");
        komoran.setUserDic("user_data/user.dic");

        String input = "아 나의 운명은 어떻게 흘러갈까 인생은 바람과 같아서 저리 흘러가나 이리 흘러 오나 그 누구도 알수가 없구나";
        KomoranResult analyzeResultList = komoran.analyze(input);
        List<Token> tokenList = analyzeResultList.getTokenList();

        System.out.println("==========print 'getTokenList()'==========");
        for (Token token : tokenList) {
            System.out.println(token);
            System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
            System.out.println();

        }
        System.out.println("==========print 'getNouns()'==========");
        System.out.println(analyzeResultList.getNouns());
        System.out.println();

        // 3. print analyzed result as pos-tagged text
        System.out.println("==========print 'getPlainText()'==========");
        System.out.println(analyzeResultList.getPlainText());
        System.out.println();

        // 4. print analyzed result as list
        System.out.println("==========print 'getList()'==========");
        System.out.println(analyzeResultList.getList());
        System.out.println();

        // 5. print morphes with selected pos
        System.out.println("==========print 'getMorphesByTags()'==========");
        System.out.println(analyzeResultList.getMorphesByTags("NP", "NNP", "JKB"));
    }
}