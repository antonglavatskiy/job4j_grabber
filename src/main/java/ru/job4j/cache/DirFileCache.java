package ru.job4j.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

public class DirFileCache extends AbstractCache<String, String> {
    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    @Override
    protected String load(String key) {
        StringJoiner rsl = new StringJoiner("\n");
        try {
            Files.readAllLines(Path.of(cachingDir, key))
                    .forEach(rsl::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl.toString();
    }
}
