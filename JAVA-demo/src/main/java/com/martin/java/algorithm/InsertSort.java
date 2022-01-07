package com.martin.java.algorithm;

import java.util.Arrays;

/**
 * Created by DingJinZhu on 2021/6/9.
 * Description:
 */
public class InsertSort {
    public static void main(String[] args) {
        int[] val = insertSort(new int[]{3, 6, 4, 7, 9, 2, 8});
        System.out.println(Arrays.toString(val));
    }

    private static int[] insertSort(int[] arr) {
        int len = arr.length;
        for (int i = 1; i < len; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }else {
                    break;
                }
            }

        }
        return arr;
    }
}
