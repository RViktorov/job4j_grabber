package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParserTest {
    private final DateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void whenParseValidDateThenReturnLocalDateTime() {
        String input = "2026-02-28T15:01:03+03:00";

        LocalDateTime result = parser.parse(input);
        assertEquals(LocalDateTime.of(2026, 2, 28, 15, 1, 3), result);
    }

}