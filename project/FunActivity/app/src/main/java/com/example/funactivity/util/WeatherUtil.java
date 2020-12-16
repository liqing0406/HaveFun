package com.example.funactivity.util;

import com.example.funactivity.R;

public class WeatherUtil {
    public int getImg(String data, String wea) {
        int src;
        if (data.compareTo("06:00") >= 0 && data.compareTo("18:00") <= 0) {
            switch (wea) {
                case "晴":
                    src = R.drawable.w1;
                    break;
                case "多云":
                    src = R.drawable.w2;
                    break;
                case "小雨":
                    src = R.drawable.w3;
                    break;
                case "大雨":
                    src = R.drawable.w4;
                    break;
                case "阵雨":
                    src = R.drawable.w5;
                    break;
                case "中雨":
                    src = R.drawable.w6;
                    break;
                case "暴雨":
                    src = R.drawable.w7;
                    break;
                case "大暴雨":
                    src = R.drawable.w8;
                    break;
                case "特大暴雨":
                    src = R.drawable.w9;
                    break;
                case "冻雨":
                    src = R.drawable.w10;
                    break;
                case "小到中雨":
                    src = R.drawable.w11;
                    break;
                case "中到大雨":
                    src = R.drawable.w12;
                    break;
                case "大到暴雨":
                    src = R.drawable.w13;
                    break;
                case "暴雨到大暴雨":
                    src = R.drawable.w14;
                    break;
                case "大暴雨到特大暴雨":
                    src = R.drawable.w15;
                    break;
                case "雷阵雨":
                    src = R.drawable.w16;
                    break;
                case "雷阵雨伴有冰雹":
                    src = R.drawable.w17;
                    break;
                case "雨夹雪":
                    src = R.drawable.w18;
                    break;
                case "阵雪":
                    src = R.drawable.w19;
                    break;
                case "小雪":
                    src = R.drawable.w20;
                    break;
                case "中雪":
                    src = R.drawable.w21;
                    break;
                case "大雪":
                    src = R.drawable.w22;
                    break;
                case "暴雪":
                    src = R.drawable.w23;
                    break;
                case "小到中雪":
                    src = R.drawable.w24;
                    break;
                case "中到大雪":
                    src = R.drawable.w25;
                    break;
                case "大到暴雪":
                    src = R.drawable.w26;
                    break;
                case "雾":
                    src = R.drawable.w27;
                    break;
                case "霾":
                    src = R.drawable.w28;
                    break;
                case "浮尘":
                    src = R.drawable.w29;
                    break;
                case "扬沙":
                    src = R.drawable.w30;
                    break;
                case "强沙尘暴":
                    src = R.drawable.w31;
                    break;
                case "沙尘暴":
                    src = R.drawable.w32;
                    break;
                case "阴":
                    src = R.drawable.w33;
                    break;
                case "台风":
                    src = R.drawable.w34;
                    break;
                case "龙卷风":
                    src = R.drawable.w35;
                    break;
                default:
                    src = R.drawable.w1;
                    break;
            }
        } else {
            switch (wea) {
                case "晴":
                    src = R.drawable.wn1;
                    break;
                case "多云":
                    src = R.drawable.wn2;
                    break;
                case "小雨":
                    src = R.drawable.wn3;
                    break;
                case "大雨":
                    src = R.drawable.wn4;
                    break;
                case "阵雨":
                    src = R.drawable.wn5;
                    break;
                case "中雨":
                    src = R.drawable.wn6;
                    break;
                case "暴雨":
                    src = R.drawable.wn7;
                    break;
                case "大暴雨":
                    src = R.drawable.wn8;
                    break;
                case "特大暴雨":
                    src = R.drawable.wn9;
                    break;
                case "冻雨":
                    src = R.drawable.wn10;
                    break;
                case "小到中雨":
                    src = R.drawable.wn11;
                    break;
                case "中到大雨":
                    src = R.drawable.wn12;
                    break;
                case "大到暴雨":
                    src = R.drawable.wn13;
                    break;
                case "暴雨到大暴雨":
                    src = R.drawable.wn14;
                    break;
                case "大暴雨到特大暴雨":
                    src = R.drawable.wn15;
                    break;
                case "雷阵雨":
                    src = R.drawable.wn16;
                    break;
                case "雷阵雨伴有冰雹":
                    src = R.drawable.wn17;
                    break;
                case "雨夹雪":
                    src = R.drawable.wn18;
                    break;
                case "阵雪":
                    src = R.drawable.wn19;
                    break;
                case "小雪":
                    src = R.drawable.wn20;
                    break;
                case "中雪":
                    src = R.drawable.wn21;
                    break;
                case "大雪":
                    src = R.drawable.wn22;
                    break;
                case "暴雪":
                    src = R.drawable.wn23;
                    break;
                case "小到中雪":
                    src = R.drawable.wn24;
                    break;
                case "中到大雪":
                    src = R.drawable.wn25;
                    break;
                case "大到暴雪":
                    src = R.drawable.wn26;
                    break;
                case "雾":
                    src = R.drawable.wn27;
                    break;
                case "霾":
                    src = R.drawable.wn28;
                    break;
                case "浮尘":
                    src = R.drawable.wn29;
                    break;
                case "扬沙":
                    src = R.drawable.wn30;
                    break;
                case "强沙尘暴":
                    src = R.drawable.wn31;
                    break;
                case "沙尘暴":
                    src = R.drawable.wn32;
                    break;
                case "阴":
                    src = R.drawable.wn33;
                    break;
                case "台风":
                    src = R.drawable.wn34;
                    break;
                case "龙卷风":
                    src = R.drawable.wn35;
                    break;
                default:
                    src = R.drawable.wn1;
                    break;
            }

        }
        return src;
    }
}
