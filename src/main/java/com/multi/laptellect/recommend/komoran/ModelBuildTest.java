package com.multi.laptellect.recommend.komoran;

import kr.co.shineware.nlp.komoran.modeler.builder.ModelBuilder;

import java.io.File;

public class ModelBuildTest {


    public static void main(String[] args) {
        modelSave();
        modelLoad();
    }

    private static void modelLoad() {
        ModelBuilder builder = new ModelBuilder();
        builder.load("models");
    }

    private static void modelSave() {
        ModelBuilder builder = new ModelBuilder();
        //external dictionary for out of vocabulary
        builder.setExternalDic("user_data"+ File.separator+"wiki.titles");
        //training corpus path must include dictionary, grammar and irregular dictionary
        builder.buildPath("corpus_build");
        //path to save models
        builder.save("models");
    }

}