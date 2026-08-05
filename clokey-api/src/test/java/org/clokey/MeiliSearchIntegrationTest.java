package org.clokey;

import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * 실제 Meilisearch 컨테이너를 띄워 검색엔진 연동 로직(인덱싱, 검색)을 검증하기 위한 베이스 클래스입니다. "test" 프로파일에서는 {@link
 * org.clokey.domain.search.repository.NoopSearchRepository} 가 검색 리포지토리를 대체하므로, 실제 Meilisearch 연동을
 * 검증하려면 "meilisearch-it" 프로파일을 함께 활성화해 {@link
 * org.clokey.domain.search.repository.MeiliSearchRepositoryImpl} 이 사용되도록 해야 합니다.
 */
@SpringBootTest
@ActiveProfiles({"test", "meilisearch-it"})
@Testcontainers
public abstract class MeiliSearchIntegrationTest {

    private static final String MEILISEARCH_MASTER_KEY = "test-master-key-must-be-16-bytes-or-more";

    @org.testcontainers.junit.jupiter.Container
    static final GenericContainer<?> MEILISEARCH_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("getmeili/meilisearch:v1.15"))
                    .withExposedPorts(7700)
                    .withEnv("MEILI_MASTER_KEY", MEILISEARCH_MASTER_KEY)
                    .withEnv("MEILI_NO_ANALYTICS", "true")
                    .waitingFor(Wait.forHttp("/health").forStatusCode(200));

    @DynamicPropertySource
    static void meilisearchProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.meilisearch.url",
                () ->
                        "http://"
                                + MEILISEARCH_CONTAINER.getHost()
                                + ":"
                                + MEILISEARCH_CONTAINER.getMappedPort(7700));
        registry.add("spring.data.meilisearch.api-key", () -> MEILISEARCH_MASTER_KEY);
    }

    @Autowired protected DatabaseCleaner databaseCleaner;
    @MockitoBean private FirebaseMessaging mockFirebaseMessaging;
    @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    void setUpMeiliSearchIntegrationTest() {
        databaseCleaner.execute();
    }
}
