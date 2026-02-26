package ru.job4j.grabber.service;

import ru.job4j.grabber.model.Post;

import java.util.List;

public interface Parse {
    List<Post> fetch();
}