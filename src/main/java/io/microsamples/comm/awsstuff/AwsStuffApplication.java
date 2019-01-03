package io.microsamples.comm.awsstuff;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@SpringBootApplication
public class AwsStuffApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsStuffApplication.class, args);
    }

}

@Component
class sender implements ApplicationListener<ApplicationReadyEvent>, Ordered {
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}")
    private String sqsEndpoint;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        queueMessagingTemplate.send(sqsEndpoint
                , MessageBuilder.withPayload(" sent something ".concat(Instant.now().toString())).build());
    }
}

@Component
@Log4j2
class receiver implements ApplicationListener<ApplicationReadyEvent>, Ordered {


    @SqsListener("microsamples-sqs-queue")
    public void processMessaage(String message){
        log.info(message);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    }
}

