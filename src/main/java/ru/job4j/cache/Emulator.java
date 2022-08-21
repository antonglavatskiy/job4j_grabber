package ru.job4j.cache;

import java.util.Scanner;

public class Emulator {
    private static final int UPLOAD_CACHE = 1;
    private static final int DOWNLOAD_CACHE = 2;
    private static final String ENTER_DIR_NAME = "Введите имя директории";
    private static final String ENTER_FILE_NAME = "Введите имя файла";
    private static final String SUCCESS = "Файл успешно закэширован";

    public static final String CHOICE_DIR = """
                1. Выбрать директорию для кэширования
                2. Выход
            """;

    public static final String CHOICE_CACHE = """
                1. Загрузить содержимое файла в кеш
                2. Получить содержимое файла из кэша
                3. Выход
            """;

    private static void startCache(Scanner scanner, DirFileCache cache) {
        boolean run = true;
        while (run) {
            System.out.println(CHOICE_CACHE);
            int cacheChoice = Integer.parseInt(scanner.nextLine());
            if (UPLOAD_CACHE == cacheChoice) {
                System.out.println(ENTER_FILE_NAME);
                cache.get(scanner.nextLine());
                System.out.println(SUCCESS);
            } else if (DOWNLOAD_CACHE == cacheChoice) {
                System.out.println(ENTER_FILE_NAME);
                System.out.println(cache.get(scanner.nextLine()));
                System.out.println();
            } else {
                run = false;
                System.out.println(CHOICE_DIR);
            }
        }
    }

    private static void start(Scanner scanner) {
        boolean run = true;
        System.out.println(CHOICE_DIR);
        while (run) {
            int userChoice = Integer.parseInt(scanner.nextLine());
            if (1 == userChoice) {
                System.out.println(ENTER_DIR_NAME);
                DirFileCache cache = new DirFileCache(scanner.nextLine());
                startCache(scanner, cache);
            } else {
                run = false;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        start(scanner);
    }
}
