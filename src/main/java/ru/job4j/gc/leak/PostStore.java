package ru.job4j.gc.leak;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PostStore {
    private static Map<Integer, Post> posts = new HashMap<>();
    private int id = 1;

    public Post add(Post post) {
        post.setId(id++);
        posts.put(post.getId(), post);
        return post;
    }

    public void removeAll() {
        posts.clear();
    }

    public static Collection<Post> getPosts() {
        return posts.values();
    }
}
