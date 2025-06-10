package sg.edu.nus.iss.edgp.audit.logging.aws.sqs;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import sg.edu.nus.iss.edgp.audit.logging.configuration.AWSConfig;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@ExtendWith(MockitoExtension.class)
public class AWSConfigTest {

    private AWSConfig awsConfig;

    @BeforeEach
    public void setUp() {
        awsConfig = new AWSConfig();

        // Mock property values
        ReflectionTestUtils.setField(awsConfig, "accessKey", "dummyAccessKey");
        ReflectionTestUtils.setField(awsConfig, "secretKey", "dummySecretKey");
        ReflectionTestUtils.setField(awsConfig, "region", "us-east-1");
        ReflectionTestUtils.setField(awsConfig, "awsSqsQueueUrl", "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue");
        ReflectionTestUtils.setField(awsConfig, "awsSqsMessageVisibilitySecond", 30);
    }

    @Test
    public void testSqsAsyncClientBeanCreation() {
        SqsAsyncClient client = awsConfig.sqsAsyncClient();
        assertNotNull(client);
    }

    @Test
    public void testSqsMessageListenerContainerFactoryBeanCreation() {
        SqsMessageListenerContainerFactory<Object> factory = awsConfig.defaultSqsListenerContainerFactory();
        assertNotNull(factory);
    }
}

