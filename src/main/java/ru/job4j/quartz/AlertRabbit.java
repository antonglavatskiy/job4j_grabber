package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    public static void main(String[] args) {
        Properties config = createConfig();
        try (Connection connection = initConnection(config)) {
            createTable(connection);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("connection_rabbit", connection);
            JobDetail jobDetail = newJob(Rabbit.class)
                    .usingJobData(dataMap)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(initInterval(config))
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

    public static Properties createConfig() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Connection initConnection(Properties config) {
        Connection connection = null;
        try {
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(config.getProperty("rabbit.url"),
                    config.getProperty("rabbit.username"), config.getProperty("rabbit.password"));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static int initInterval(Properties config) {
        String interval = config.getProperty("rabbit.interval");
        if (!interval.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                    String.format("Incorrect value %s in file \"%s\"",
                            interval, "rabbit.properties"));
        }
        return Integer.parseInt(interval);
    }

    public static void createTable(Connection connection) {
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
