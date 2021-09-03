package com.daqsoft.travelCultureModule.hotel.util;

import com.google.gson.internal.LinkedTreeMap;

public class AnyUtils {

    public static String getString(Object o,String name){
        if (o instanceof LinkedTreeMap){
            return ((LinkedTreeMap) o).get(name).toString();
        }
        return "";
    }

    /*** 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

}
