package com.metoo.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@ServletComponentScan(basePackages ={ "com.metoo.ws"})
@EnableScheduling // 开启定时任务（启动类增加该注解，使项目启动后执行定时任务）
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Long time=System.currentTimeMillis();
        SpringApplication.run(Application.class);
        System.out.println("===应用启动耗时："+(System.currentTimeMillis()-time)+"===");
    }

}
