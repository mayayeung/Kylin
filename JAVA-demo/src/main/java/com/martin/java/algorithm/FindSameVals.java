package com.martin.java.algorithm;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by DingJinZhu on 2021/6/9.
 * Description:
 */
public class FindSameVals {
    public static void main(String[] args) {
        int[] a = new int[]{3, 6, 23, 7, 2, 12, 54, 86};
        int[] b = new int[]{4, 5, 85, 2, 3, 54, 23, 12, 44};
        Set<Integer> val = getSame(a, b);
        for (Integer i : val) {
            System.out.println(i);
        }

    }

    public static Set<Integer> getSame(int[] a, int[] b) {
        Set<Integer> temp = new HashSet<>();
        Set<Integer> same = new HashSet<>();

        for (int i = 0; i < a.length; i++) {
            temp.add(a[i]);
        }

        for (int j = 0; j < b.length; j++) {
            if (!temp.add(b[j])) {
                same.add(b[j]);
            }
        }
        return same;
    }
}
