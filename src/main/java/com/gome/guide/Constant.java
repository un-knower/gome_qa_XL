package com.gome.guide;

/**
 * Created by lixiang-ds3 on 2017/7/9.
 */
import java.util.ResourceBundle;

public class Constant {

    private static  String dictionaryPath="";

    static{
        ResourceBundle resource = ResourceBundle.getBundle("gome_nlp_token");
        String path = resource.getString("dictionary");
        setDictionaryPath(path);
    }

    public static String getDictionaryPath() {
        return dictionaryPath;
    }

    public static void setDictionaryPath(String dictionaryPath) {
        Constant.dictionaryPath = dictionaryPath;
    }
}