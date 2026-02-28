package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return OffsetDateTime.parse(parse).toLocalDateTime();
    }

}