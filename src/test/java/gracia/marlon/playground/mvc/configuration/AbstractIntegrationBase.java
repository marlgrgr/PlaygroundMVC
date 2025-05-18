package gracia.marlon.playground.mvc.configuration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationBase {

	private static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

	@SuppressWarnings("resource")
	private static final GenericContainer<?> redis = new GenericContainer<>("redis:7.4.3").withExposedPorts(6379);

	@SuppressWarnings("resource")
	private static final GenericContainer<?> activeMq = new GenericContainer<>("rmohr/activemq:5.15.9")
			.withExposedPorts(61616);

	@SuppressWarnings("resource")
	private static final LocalStackContainer localstack = new LocalStackContainer(
			DockerImageName.parse("localstack/localstack:3.0")).withServices(LocalStackContainer.Service.SQS);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static boolean containersStarted = false;

	private static boolean propertiesRegistered = false;
	
	private static boolean tokenGenerated = false;

	private static Path tempDir;

	private static File redissonConfigFile;

	private static String queueUrl;

	private static String sharedToken;

	static {
		startContainers();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			mongo.stop();
			redis.stop();
			activeMq.stop();
			localstack.stop();
		}));
	}

	private static void startContainers() {
		if (!containersStarted) {
			Startables.deepStart(mongo, redis, activeMq, localstack).join();
			containersStarted = true;

			try {
				tempDir = Files.createTempDirectory("redisson-config");
				redissonConfigFile = createRedissonConfigFile(redis.getHost(), redis.getMappedPort(6379));

				SqsClient sqsClient = SqsClient.builder()
						.endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
						.region(Region.of(localstack.getRegion()))
						.credentialsProvider(StaticCredentialsProvider.create(
								AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
						.build();

				String queueName = "test_queue";
				CreateQueueResponse createQueueResponse = sqsClient
						.createQueue(CreateQueueRequest.builder().queueName(queueName).build());

				queueUrl = createQueueResponse.queueUrl();

			} catch (IOException e) {
				throw new RuntimeException("Error initializing resources", e);
			}
		}
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		if (!propertiesRegistered) {
			startContainers();

			// redis
			registry.add("redis.config.path", redissonConfigFile::getAbsolutePath);
			registry.add("redis.config.maxIdleTime", () -> "10");

			// mongo
			registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);

			// h2
			registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
			registry.add("spring.datasource.driverClassName", () -> "org.h2.Driver");
			registry.add("spring.datasource.username", () -> "sa");
			registry.add("spring.datasource.password", () -> "");
			registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

			// jms
			registry.add("spring.activemq.broker-url",
					() -> "tcp://" + activeMq.getHost() + ":" + activeMq.getMappedPort(61616));
			registry.add("spring.activemq.user", () -> "admin");
			registry.add("spring.activemq.password", () -> "admin");

			// AWS SQS (Localstack)
			registry.add("sqs.queue.region", localstack::getRegion);
			registry.add("sqs.queue.accessKey", localstack::getAccessKey);
			registry.add("sqs.queue.secretKey", localstack::getSecretKey);
			registry.add("sqs.queue.endpointOverride",
					() -> localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
			registry.add("spring.cloud.aws.sqs.enabled", () -> "true");
			registry.add("sqs.queue.loadMovies.url", () -> queueUrl);
			registry.add("sqs.queue.loadMovies.name", () -> "test_queue");

			// JWT
			registry.add("jwt.secretKey", () -> "5v8y/B?E(H+MbQeThWmZq4t7w!z%C&F)");

			propertiesRegistered = true;
		}
	}

	@BeforeAll
    synchronized void setupTokenIfNeeded() throws Exception {
        if (!tokenGenerated) {
            generateToken();
            tokenGenerated = true;
        }
    }
	
	private synchronized void generateToken() throws Exception {
        if (tokenGenerated) {
            return; // Token already generated by another test class
        }
        
        System.out.println("Generating auth token for tests...");
        
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("admin");
        authRequestDTO.setPassword("admin123");

        MvcResult response = mockMvc
                .perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk()).andReturn();

        String initialToken = response.getResponse().getContentAsString();

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("admin123");
        changePasswordDTO.setNewPassword("Admin*123");
        changePasswordDTO.setConfirmNewPassword("Admin*123");

        mockMvc.perform(post("/api/v1/auth/changePassword").header("Authorization", "Bearer " + initialToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isNoContent());

        authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("admin");
        authRequestDTO.setPassword("Admin*123");

        response = mockMvc
                .perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk()).andReturn();

        sharedToken = response.getResponse().getContentAsString();
        System.out.println("Auth token generated successfully");
    }

	public String getToken() {
        if (!tokenGenerated) {
            try {
                generateToken();
                tokenGenerated = true;
            } catch (Exception e) {
                throw new RuntimeException("Error generating token", e);
            }
        }
        return sharedToken;
    }

	private static File createRedissonConfigFile(String host, int port) throws IOException {
		File configFile = tempDir.resolve("redisson-test.yaml").toFile();

		try (FileWriter writer = new FileWriter(configFile)) {
			writer.write("singleServerConfig:\n" + "  address: \"redis://" + host + ":" + port + "\"\n");
		}

		return configFile;
	}
}
