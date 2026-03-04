package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse { // класс отвечает за извлечение со станицы названия и ссылки на вакансию и времени
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";

    private static final int PAGES_TO_PARSE = 5; //количество страниц для парсинга
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGES_TO_PARSE; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);

                var document = Jsoup.connect(fullLink)
                        .userAgent("Mozilla")
                        .timeout(5000)
                        .get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    //название и ссылка
                    try {
                        var titleElement = row.select(".vacancy-card__title").first();
                        var linkElement = titleElement.child(0);

                        String vacancyName = titleElement.text();
                        String link = String.format("%s%s", SOURCE_LINK,
                                linkElement.attr("href"));
                        // получаем время из вакансии
                        var vacancyDoc = Jsoup.connect(link).get();
                        // Получаем описание
                        String description = retrieveDescription(link);

                        var timeElement = vacancyDoc.selectFirst("time");
                        long seconds;
                        if (timeElement != null) {
                            String datetime = timeElement.attr("datetime");
                            seconds = dateTimeParser.parse(datetime).toEpochSecond(ZoneOffset.UTC);
                        } else {
                            seconds = System.currentTimeMillis();
                        }

                        var post = new Post();
                        post.setTitle(vacancyName);
                        post.setLink(link);
                        post.setTime(seconds);
                        post.setDescription(description);
                        result.add(post);
                    } catch (IOException e) {
                        LOG.error("Error parsing vacancy: " + row.text(), e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        try {

            var vacancyDoc = Jsoup.connect(link)  // Получаем  вакансии
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();

            var descriptionElement = vacancyDoc.selectFirst(".vacancy-show");  // Получаем описания

            if (descriptionElement != null) {
                return descriptionElement.text();
            }
        } catch (IOException e) {
            LOG.error("Error retrieving description for " + link, e);
        }
        return "description not available";
    }

}