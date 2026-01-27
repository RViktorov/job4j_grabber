package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.util.*;

public class MemStore implements Store {
    private final Map<Long, Post> mem = new HashMap<>();

    @Override
    public void save(Post post) {
        mem.put(post.getId(), post);
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(mem.values());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(mem.get(id));
    }
}

