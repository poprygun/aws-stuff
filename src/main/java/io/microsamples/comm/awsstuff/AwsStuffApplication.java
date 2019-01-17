package io.microsamples.comm.awsstuff;

import io.microsamples.comm.awsstuff.s3.S3Services;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

    @Autowired
    private S3Services s3Services;

    @SqsListener("microsamples-sqs-queue")
    public void processMessaage(String message) throws IOException {
        uploadFileToS3(message);
        log.info(message);
    }

    private void uploadFileToS3(String message) throws IOException {
        String pathname = Instant.now().toString();
        File file = new File(pathname);
        FileUtils.writeStringToFile(file, message, Charset.defaultCharset());
        s3Services.uploadFile(pathname, file);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    }
}

