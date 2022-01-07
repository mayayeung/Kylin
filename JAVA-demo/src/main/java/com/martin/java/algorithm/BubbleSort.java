package com.martin.java.algorithm;

import java.util.Arrays;

/**
 * Created by DingJinZhu on 2021/6/9.
 * Description: 冒泡排序的时间复杂度为O(n2)，空间复杂度为O(1)。
 */
public class BubbleSort {

    public static void main(String[] args) {
        int[] val = bubbleSort(new int[]{6, 7, 2, 4, 9, 10});
        System.out.println(Arrays.toString(val));

    }

    private static int[] bubbleSort(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                //相邻元素两两对比
                if (arr[j] > arr[j + 1]) {
                    //元素交换
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }
}
