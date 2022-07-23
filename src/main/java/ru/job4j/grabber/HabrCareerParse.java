package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private String retrieveDescription(String link) throws IOException {
        StringBuilder rsl = new StringBuilder();
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements description = document.select(".collapsible-description__content");
        description.forEach(element -> rsl.append(element.text()));
        return rsl.toString();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Page" + i);
            Connection connection = Jsoup.connect(PAGE_LINK + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element dateElement = row.selectFirst(".basic-date");
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkedElement = titleElement.select(".vacancy-card__title-link").first();
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkedElement.attr("href"));
                String date = dateElement.attr("datetime");
                System.out.printf("%s %s %s%n", vacancyName, link, date);
            });
        }
    }
}
