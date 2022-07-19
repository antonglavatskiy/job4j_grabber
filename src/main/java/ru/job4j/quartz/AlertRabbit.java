package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail jobDetail = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(initInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static int initInterval() {
        int rsl = 0;
        try (InputStream input = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            String interval = properties.getProperty("rabbit.interval");
            if (!interval.matches("[0-9]+")) {
                throw new IllegalArgumentException(
                        String.format("Incorrect value %s in file \"%s\"",
                                interval, "rabbit.properties"));
            }
            rsl = Integer.parseInt(interval);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }
}
