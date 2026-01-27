package ru.job4j;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.stores.MemStore;
import ru.job4j.grabber.stores.Store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) {

        var config = new Config();
        config.load("application.properties");

        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        ))
        {
            Store store = new JdbcStore(connection);
            var post = new Post(
                    1L,
                    "Super Java Job",
                    "https://java.com",
                    "Java developer position",
                    System.currentTimeMillis()
            );
            store.save(post);

            var scheduler = new SchedulerManager();
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
            Thread.sleep(10000);
            scheduler.close();
        }
        catch (SQLException | InterruptedException e) {
            log.error("When create a connection", e);
        }
    }
}