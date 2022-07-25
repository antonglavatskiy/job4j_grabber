package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties config = new Properties();

    public void config() throws IOException {
        try (InputStream inputStream = Grabber.class
                .getClassLoader()
                .getResourceAsStream("habr.properties")) {
           config.load(inputStream);
        }
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public Store store() {
        return new PsqlStore(config);
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("store", store);
        dataMap.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(dataMap)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(
                        Integer.parseInt(config.getProperty("habr.interval")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap jobDataMap = jobExecutionContext
                    .getJobDetail()
                    .getJobDataMap();
            Store store = (Store) jobDataMap.get("store");
            Parse parse = (Parse) jobDataMap.get("parse");
            List<Post> postList = parse.list();
            postList.stream()
                    .filter(post -> post.getTitle().contains("Java developer"))
                    .forEach(store::save);
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grabber = new Grabber();
        grabber.config();
        Scheduler scheduler = grabber.scheduler();
        Store store = grabber.store();
        grabber.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
    }
}
