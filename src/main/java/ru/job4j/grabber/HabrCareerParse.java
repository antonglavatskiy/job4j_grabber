package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final int COUNT = 1;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private Elements connectPage(String link, String query) {
        Elements rsl;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            rsl = document.select(query);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Failed to open page %s", link));
        }
        return rsl;
    }

    private String retrieveDescription(String link) {
        Elements description = connectPage(link, ".style-ugc");
        return description.text();
    }

    private Post createPost(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkedElement = element.select(".vacancy-card__title-link").first();
        Element dateElement = element.select(".basic-date").first();
        String vacancyName = titleElement.text();
        String vacancyLink = String.format("%s%s", SOURCE_LINK, linkedElement.attr("href"));
        String description = retrieveDescription(vacancyLink);
        LocalDateTime dateTime = dateTimeParser.parse(dateElement.attr("datetime"));
        return new Post(vacancyName, vacancyLink, description, dateTime);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int i = 1; i <= COUNT; i++) {
            Elements rows = connectPage(link + i, ".vacancy-card__inner");
            rows.forEach(row -> rsl.add(createPost(row)));
        }
        return rsl;
    }
}
