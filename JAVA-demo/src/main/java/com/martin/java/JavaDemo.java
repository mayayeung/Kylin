package com.martin.java;

import java.util.ArrayList;
import java.util.List;

import jdk.internal.net.http.common.Log;

/**
 * Created by DingJinZhu on 2023/1/10.
 * Description:
 */
public class JavaDemo {

    public static void main(String[] args) {
        int a = 1005;
        int b = 100;

        float c = (float)a/b;

        System.out.println(c);
        System.out.println("abcde");


        List<Size> list = new ArrayList<>();
        list.add(new Size(3264, 2448));
        list.add(new Size(2560, 1920));
        list.add(new Size(1920, 1088));
        list.add(new Size(1920, 1080));
        list.add(new Size(1800, 1080));
        list.add(new Size(1600, 1200));
        list.add(new Size(1600, 720));

        Size preview = new Size(2560, 1440);
        Size res = new JavaDemo().getPictureMaxSize(list, preview);
        System.out.println(res.width + " * " + res.height);
    }


    private Size getPictureMaxSize(List<Size> l, Size size) {
        Size s = null;
        for (int i = 0; i < l.size(); i++) {
            System.out.println("getPictureMaxSize : w =" + l.get(i).width + "; h = " + l.get(i).height);
            if (l.get(i).width >= size.width && l.get(i).height >= size.height
                    && l.get(i).height != l.get(i).width) {
                if (s == null) {
                    s = l.get(i);
                } else {
                    if (s.height * s.width > l.get(i).width * l.get(i).height) {
                        s = l.get(i);
                    }
                }
            }
        }
        return s;
    }
}
