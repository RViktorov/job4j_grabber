package ru.job4j.grabber.service;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Класс  для загрузки свойств: настройка периодичности, настройки соединения с базой данных.
public class Config {
    private static final Logger log = Logger.getLogger(Config.class);
    private final Properties properties = new Properties();

    public void load(String file) {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(file)) {
            properties.load(input);
        } catch (IOException io) {
            log.error(String.format("When load file : %s" , file), io);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
