package ru.job4j.gc.prof;

import java.util.Random;
import java.util.Scanner;

public class TestSort {
    private Scanner scanner = new Scanner(System.in);
    private Data data;

    public void init(int elements) {
        showMenu();
        int num;
        while ((num = scanner.nextInt()) != 5) {
            if (num == 1) {
                data = new RandomArray(new Random());
                data.insert(elements);
            }
            if (num == 2) {
                new BubbleSort().sort(data);
            }
            if (num == 3) {
                new InsertSort().sort(data);
            }
            if (num == 4) {
                new MergeSort().sort(data);
            }
        }
    }

    private void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Create array");
        System.out.println("2. Bubble sort");
        System.out.println("3. Insert sort");
        System.out.println("4. Merge sort");
        System.out.println("5. Exit");
    }


    public static void main(String[] args) {
        int elements = 500_000;
        TestSort testSort = new TestSort();
        testSort.init(elements);

        /*
        -XX:+UseSerialGC
        -XX:+UseParallelGC
        -XX:+UseG1GC
        -XX:+UseZGC
         */

    }
}
