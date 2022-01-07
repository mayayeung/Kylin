package com.martin.java.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by DingJinZhu on 2021/6/9.
 * Description:
 */
public class FindMaxTimes {
    public static void main(String[] args) {
        int[] arr = new int[]{12, 34, 5, 3, 4, 8, 7, 4, 1, 2, 3, 4, 5, 6, 7, 87, 6, 5, 4, 3, 4, 5, 4, 1, 4};
        System.out.println(findMaxTimes(arr));

    }

    private static int findMaxTimes(int[] arr) {
        HashMap<Integer, LinkedList<Integer>> elemMap = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            int t = arr[i];
            if (elemMap.get(t) != null) {
                elemMap.get(t).add(t);
            } else {
                LinkedList<Integer> temp = new LinkedList();
                temp.add(t);
                elemMap.put(t, temp);
            }
        }

        int val = 0;
        int largeVal = 0;
        for (Map.Entry<Integer, LinkedList<Integer>> entry : elemMap.entrySet()) {
            if (entry.getValue().size() > largeVal) {
                largeVal = entry.getValue().size();
                val = entry.getKey();
            }
        }

        return largeVal;
    }
}
