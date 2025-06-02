package sg.edu.nus.iss.edgp.audit.logging.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Getter
@Setter
@Configuration
@Profile("!test")
public class AWSConfig {

	private static final Logger LOG = LogManager.getLogger(AWSConfig.class);

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.sqs.queue.url}")
	private String awsSqsQueueUrl;

	@Value("${cloud.aws.sqs.message.visibility.second}")
	private Integer awsSqsMessageVisibilitySecond;

	@Bean
	public SqsAsyncClient sqsAsyncClient() {
		LOG.info("SqsAsyncClient Initializing");
		AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey,secretKey);

		SqsAsyncClient sqsAsyncClient = SqsAsyncClient.builder().region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).build();
		LOG.info("SqsAsyncClient Initialized");

		return sqsAsyncClient;
	}

	@Bean
	public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
		LOG.info("SqsMessageListenerContainerFactory Initializing");
		SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory = SqsMessageListenerContainerFactory
				.builder().configure(options -> options.acknowledgementMode(AcknowledgementMode.ALWAYS))
				.sqsAsyncClient(sqsAsyncClient()).build();
		LOG.info("SqsMessageListenerContainerFactory Initialized");

		return defaultSqsListenerContainerFactory;
	}

}
