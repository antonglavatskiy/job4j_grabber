package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
        HabrCareerParse habr = new HabrCareerParse(new HabrCareerDateTimeParser());
        System.out.println(habr.list(PAGE_LINK).size());
    }

    private String retrieveDescription(String link) {
        String rsl = null;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element description = document.selectFirst(".style-ugc");
            rsl = description.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            try {
                Connection connection = Jsoup.connect(link);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkedElement = titleElement.select(".vacancy-card__title-link").first();
                    Element dateElement = row.selectFirst(".basic-date");
                    String vacancyName = titleElement.text();
                    String vacancyLink = String.format("%s%s", SOURCE_LINK, linkedElement.attr("href"));
                    String description = retrieveDescription(vacancyLink);
                    LocalDateTime dateTime = dateTimeParser.parse(dateElement.attr("datetime"));
                    rsl.add(new Post(vacancyName, vacancyLink, description, dateTime));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsl;
    }
}
