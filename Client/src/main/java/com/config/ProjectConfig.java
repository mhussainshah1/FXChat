package com.config;

import com.service.StageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.PrintWriter;
import java.io.StringWriter;

@Configuration
public class ProjectConfig {

    @Bean
    public StageService stageService() {
        return new StageService();
    }

/*    @Bean
    public MessageService message() {
        return new MessageService(new Label());
    }*/

    @Bean(name = "stringPrintWriter")
    @Scope("prototype")
    public PrintWriter printWriter() {
        // useful when dumping stack trace to file
        return new PrintWriter(new StringWriter());
    }
}
