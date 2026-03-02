package ru.job4j.grabber.service;

import io.javalin.Javalin;
import ru.job4j.grabber.stores.Store;

public class Web {
    private final Store store;

    public Web(Store store) {
        this.store = store;
    }

    public void start(int port) {
        // Создаем сервер Javalin

        var app = Javalin.create(config -> {
            config.defaultContentType = "text/html; charset=utf-8";
        });

        // Указываем порт, на котором будет работать сервер
        app.start(port);

        // Формируем страницу с вакансиями
        var page = new StringBuilder();
        store.getAll().forEach(post -> page.append(post.toString()).append(System.lineSeparator()));

        // Настраиваем обработчик для корневого URL
        app.get("/", ctx -> {
            ctx.contentType("text/html; charset=utf-8");
            ctx.result(page.toString());
        });
    }

}