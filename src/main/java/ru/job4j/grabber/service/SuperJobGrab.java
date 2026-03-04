package ru.job4j.grabber.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.job4j.grabber.stores.Store;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

public class SuperJobGrab implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("Quartz job started");

        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        var parser = new HabrCareerParse(new HabrCareerDateTimeParser());

        try {
            var posts = parser.fetch();
            for (var post : posts) {
                store.save(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}