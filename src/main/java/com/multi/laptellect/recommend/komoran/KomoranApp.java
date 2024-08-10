//package com.multi.laptellect.recommend.komoran;
//
//import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
//import kr.co.shineware.nlp.komoran.core.Komoran;
//import kr.co.shineware.nlp.komoran.model.KomoranResult;
//import kr.co.shineware.nlp.komoran.model.Token;
//
//import java.util.List;
//
//public class KomoranApp {
//    public static void main(String[] args) {
//        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//        String strToAnalyze = "아 나의 운명은 어떻게 흘러갈까 인생은 바람과 같아서 저리 흘러가나 이리 흘러 오나 그 누구도 알수가 없구나.";
//
//        KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);
//        System.out.println(analyzeResultList.getPlainText());
//
//        List<Token> tokenList = analyzeResultList.getTokenList();
//        for (Token token : tokenList) {
//            System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());
//        }
//    }
//}
