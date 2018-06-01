package com.gome.filter;

import com.gome.process.Read_WriteText;

import java.util.Set;

/**
 * Created by lixiang-ds3 on 2017/7/17.
 */
public class filterFacets {

    public static void main(String[] args){

        Set<String> set2 = filterId.load("E:\\facters.txt");
        int count = 0;
        for(String str : set2){
            System.out.println(++count);
            String[] cat_facters = str.split("\t");
            String cat = cat_facters[0];
            String facters = cat_facters[1];
            facters = facters.replace(" ##", "");
            facters = facters.replace(" #", "");
            facters = facters.trim();
            String result = "";
            String[] fact = facters.split(" ");
            if(fact.length > 2){
                String first = fact[0];
                String second = fact[1];
                String third = fact[2];
                if(second.startsWith("价格")){
                    result = first + " " + third;
                }else{
                    result = first + " " + second;
                }
            }else if(fact.length == 2){
                String first = fact[0];
                String second = fact[1];
                if(!second.startsWith("价格")){
                    result = first + " " + second;
                }else{
                    result = first;
                }
            }else{
                if(!facters.startsWith("价格")){
                    result = facters;
                }
            }

            Read_WriteText.writeTxtFile("E:\\filter_facters.txt", cat+"\t" + result, "utf8", true);
        }
    }
}
