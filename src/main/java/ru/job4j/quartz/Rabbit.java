package ru.job4j.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Rabbit implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection connection = (Connection) jobExecutionContext
                .getJobDetail()
                .getJobDataMap()
                .get("connection_rabbit");
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into rabbit (created_date)"
                        + " values (?);")) {
            statement.setTimestamp(1,
                    Timestamp.valueOf(LocalDateTime.now()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
