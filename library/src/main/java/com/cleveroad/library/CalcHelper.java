package com.cleveroad.library;

class CalcHelper {
    static int sumArray(int array[]) {
        return sumArray(array, 0, array.length);
    }

    static int sumArray(int array[], int firstIndex, int count) {
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += array[i];
        }
        return sum;
    }
}
