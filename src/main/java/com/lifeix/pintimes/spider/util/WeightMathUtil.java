package com.lifeix.pintimes.spider.util;

/**
 * Created by neoyin on 15/11/11.
 */
public class WeightMathUtil {


    public static double contentLengthWeight(double weight,int contentlength){
        int detal = contentlength/250 -2 ;
        detal = Math.min(detal,10);
        return weight+detal;
    }

    public static void main(String[] args) {
        System.out.println(contentLengthWeight(0,260));
    }

}
