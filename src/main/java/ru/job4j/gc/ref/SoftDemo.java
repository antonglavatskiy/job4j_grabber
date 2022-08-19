package ru.job4j.gc.ref;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SoftDemo {
    public static final int COUNT = 50_000_000;

    public static void main(String[] args) {
        example2();
    }

    public static void example1() {
        Object object = new Object();
        SoftReference<Object> soft = new SoftReference<>(object);
        object = null;
        System.out.println(soft.get());
    }

    public static void example2() {
        List<SoftReference<Object>> objects = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            objects.add(new SoftReference<Object>(
                    new Object() {
                String value = String.valueOf(System.currentTimeMillis());

                        @Override
                        protected void finalize() throws Throwable {
                            System.out.println("Object removed");
                        }
                    }));
        }
        System.gc();
        int countLiveObj = 0;
        for (SoftReference<Object> soft : objects) {
            Object obj = soft.get();
            if (obj != null) {
                countLiveObj++;
            }
        }
        System.out.println(countLiveObj == COUNT);
    }
}
