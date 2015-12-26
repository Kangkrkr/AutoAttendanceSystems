package aas.project.tera.com.autoattendancesystem;

import java.util.HashMap;

/**
 * Created by Administrator on 2015-05-22.
 */
public class MapRegister {

    private static HashMap<String, String> map = new HashMap<>();

    public static HashMap<String, String> getInstance(){

        map.put("1", "월");
        map.put("2", "화");
        map.put("3", "수");
        map.put("4", "목");
        map.put("5", "금");
        map.put("6", "토");
        map.put("0", "일");

        map.put("9", "1");
        map.put("10", "2");
        map.put("11", "3");
        map.put("12", "4");
        map.put("13", "5");
        map.put("14", "6");
        map.put("15", "7");
        map.put("16", "8");
        map.put("17", "9");
        map.put("18", "A");
        map.put("19", "B");
        map.put("20", "C");
        map.put("21", "D");
        map.put("22", "E");
        map.put("23", "F");

        return map;
    }
}
