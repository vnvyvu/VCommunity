package com.vyvu.vcommunity.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

public class CoreUtils {
    public static HashMap<String, Object> obj2Map(Object obj) {
        HashMap<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(obj) != null && !field.getName().equals("id") && !field.getName().equals("view")) {
                    map.put(field.getName(), field.get(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static String passwordGen() {
        String core = "ArC%ghcM+3dR}NO2$>X_z58E4ZabBteuFDinPlV=1pqT~GkvSxmoUH&-0/JKI76@#sy^{W*L|!(j)9?wQfY<";
        int min = 10, max = core.length() - min * 2;
        Random r = new Random();
        int length = r.nextInt(max - min + 1) + min;
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = core.charAt(r.nextInt(max - min + 1) + min);
        }
        return new String(password);
    }
}
