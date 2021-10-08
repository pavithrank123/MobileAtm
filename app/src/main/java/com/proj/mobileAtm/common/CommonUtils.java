package com.proj.mobileAtm.common;

public class CommonUtils {


    public static boolean checkIsNotNullAndEmpty(String t) {
        return t != null && !t.isEmpty();
    }

    public static boolean checkIsNullOrEmpty(String t) {
        return t == null || t.isEmpty();
    }


}
