package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    private static Connection connection;
    private static Properties properties;


    public static void main(String[] args) {
        try (Connection connection = initConnection()) {
            createTable();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("connection_rabbit", connection);
            JobDetail jobDetail = newJob(Rabbit.class)
                    .usingJobData(dataMap)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(initInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection initConnection() {
        properties = new Properties();
        try (InputStream input = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(input);
            Class.forName(properties.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(properties.getProperty("rabbit.url"),
                    properties.getProperty("rabbit.username"), properties.getProperty("rabbit.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static int initInterval() {
        String interval = properties.getProperty("rabbit.interval");
        if (!interval.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                    String.format("Incorrect value %s in file \"%s\"",
                            interval, "rabbit.properties"));
        }
        return Integer.parseInt(interval);
    }

    public static void createTable() {
        try (Statement statement = connection.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader("./db/scripts/schema.sql"))) {
            StringBuilder sql = new StringBuilder();
            reader.lines().forEach(sql::append);
            statement.execute(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
