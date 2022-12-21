//package com.metoo.ws.core.config.socket;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class JobTest {
//
//    private static int i = 1;
//
////    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
//    public void w() throws InterruptedException {
//        System.out.println("任1务开始");
//        Thread.sleep(8000);
//
//    }
//
//    //    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
//    public void ee() throws InterruptedException {
//        System.out.println("任务2开始");
//        Thread.sleep(2000);
//
//    }
//
//    //    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
//    public void eee() throws InterruptedException {
//        Long time=System.currentTimeMillis();
//        System.out.println("任务3开始" );
//        Thread.sleep(8000);
//        System.out.println("===任务执行耗时："+(System.currentTimeMillis()-time)+"===");
//    }
//
//}
