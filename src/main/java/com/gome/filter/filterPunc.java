package com.gome.filter;

/**
 * Created by Micro_Kun on 2018/1/23
 */
public class filterPunc {

    public static String cleanRegx (String input){

        String regx =  "[`~!$%^&*()+_●→ˉ=|{}':',\\[\\].<>/?~！#￥%……&*（）——+|{}《》【】‘；：”“’。，、？[-]]";

        String out = input.replaceAll(regx,"");

        return out;

    }
    public static void main(String[] args)
    {
        String str = "-1bc5d5u2压力测试";
        System.out.print(cleanRegx(str));
    }


}
