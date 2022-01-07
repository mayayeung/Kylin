package com.martin.java.algorithm;

import java.util.Arrays;

/**
 * Created by DingJinZhu on 2021/6/9.
 * Description:
 */
public class SelectionSort {
    public static void main(String[] args) {
        int[] val = selectionSort(new int[]{3, 6, 4, 7, 9, 2, 8});
        System.out.println(Arrays.toString(val));
    }

    public static int[] selectionSort(int[] arr) {
        int len = arr.length;
        int temp;
        int minIndex;
        for (int i = 0; i < len - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < len; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            //将第i轮的最小值求出来,交换到i的位置
            temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
        return arr;
    }
}
