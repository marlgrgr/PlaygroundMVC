package gracia.marlon.playground.mvc.queue;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import gracia.marlon.playground.mvc.services.LoadMoviesService;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

@Configuration
@Slf4j
public class QueueConfig {

	private final String region;

	private final String queueAccessKey;

	private final String queueSecretKey;
	
	private final String queueEndpointOverride;

	public QueueConfig(Environment env) {
		this.region = env.getProperty("sqs.queue.region", "");
		this.queueAccessKey = env.getProperty("sqs.queue.accessKey", "");
		this.queueSecretKey = env.getProperty("sqs.queue.secretKey", "");
		this.queueEndpointOverride = env.getProperty("sqs.queue.endpointOverride", "");
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "true")
	SqsAsyncClient sqsAsyncClient() {
		try {

			SqsAsyncClientBuilder sqsAsyncClientBuilder =  SqsAsyncClient.builder();
			
			if(!this.queueEndpointOverride.isEmpty()) {
				sqsAsyncClientBuilder.endpointOverride(URI.create(this.queueEndpointOverride));
			}
			
			sqsAsyncClientBuilder.region(Region.of(region)).credentialsProvider(
					StaticCredentialsProvider.create(AwsBasicCredentials.create(queueAccessKey, queueSecretKey)));
			
			return sqsAsyncClientBuilder.build();

		} catch (Exception e) {
			log.error("An error ocurred connecting to the queue", e);
			return null;
		}
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "true")
	QueueConsumer queueConsumer(LoadMoviesService loadMoviesService) {
		return new QueueConsumer(loadMoviesService);
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "true")
	SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
		return SqsTemplate.builder().sqsAsyncClient(sqsAsyncClient).build();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "true")
	QueuePublisherService queuePublisherServiceSQS(SqsTemplate sqsTemplate) {
		return new QueuePublisherServiceImpl(sqsTemplate);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "false")
	QueuePublisherService queuePublisherServiceNoSQS(AsyncConsumerService asyncConsumerService) {
		return new QueuePublisherNoSQSServiceImpl(asyncConsumerService);
	}
}
