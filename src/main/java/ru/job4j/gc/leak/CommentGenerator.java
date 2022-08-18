package ru.job4j.gc.leak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentGenerator implements Generate {
    public static final String PATH_PHRASES = "src/main/java/ru/job4j/gc/leak/files/phrases.txt";
    public static final String SEPARATOR = System.lineSeparator();
    public static final int COUNT = 50;

    private static List<Comment> comments = new ArrayList<>();
    private List<String> phrases;
    private UserGenerator userGenerator;
    private Random random;

    public CommentGenerator(UserGenerator userGenerator, Random random) {
        this.userGenerator = userGenerator;
        this.random = random;
        read();
    }

    public static List<Comment> getComments() {
        return comments;
    }

    private void read() {
        try {
            phrases = read(PATH_PHRASES);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void generate() {
        comments.clear();
        List<Integer> ints = new ArrayList<>();
        random.ints(0, phrases.size())
                .distinct().limit(3).forEach(ints::add);
        for (int i = 0; i < COUNT; i++) {
            StringBuilder comment = new StringBuilder();
            comment.append(phrases.get(ints.get(0)))
                    .append(SEPARATOR)
                    .append(phrases.get(ints.get(1)))
                    .append(SEPARATOR)
                    .append(phrases.get(ints.get(2)));
            comments.add(new Comment(comment.toString(),
                    userGenerator.randomUser()));
        }
    }
}
