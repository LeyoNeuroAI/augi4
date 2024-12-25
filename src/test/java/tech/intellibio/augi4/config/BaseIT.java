package tech.intellibio.augi4.config;

import io.restassured.RestAssured;
import io.restassured.config.SessionConfig;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import tech.intellibio.augi4.Augi4Application;
import tech.intellibio.augi4.chat_message.ChatMessageRepository;
import tech.intellibio.augi4.chat_session.ChatSessionRepository;
import tech.intellibio.augi4.country.CountryRepository;
import tech.intellibio.augi4.document.DocumentRepository;
import tech.intellibio.augi4.feedback.FeedbackRepository;
import tech.intellibio.augi4.plan.PlanRepository;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.project_file.ProjectFileRepository;
import tech.intellibio.augi4.prompt.PromptRepository;
import tech.intellibio.augi4.role.RoleRepository;
import tech.intellibio.augi4.user.UserRepository;


/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context with a
 * Datasource connected to the Testcontainers Docker instance. The instance is reused for all tests,
 * with all data wiped out before each test.
 */
@SpringBootTest(
        classes = Augi4Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("it")
@Sql({"/data/clearAll.sql", "/data/roleData.sql", "/data/userData.sql"})
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class BaseIT {

    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17.1");
    public static final String PROFESSIONAL = "professional";
    public static final String ADMIN = "admin";
    public static final String PASSWORD = "Bootify!";
    private static String professionalSession = null;
    private static String adminSession = null;

    static {
        postgreSQLContainer.withReuse(true)
                .start();
    }

    @LocalServerPort
    public int serverPort;

    @Autowired
    public ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatSessionRepository chatSessionRepository;

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FeedbackRepository feedbackRepository;

    @Autowired
    public PlanRepository planRepository;

    @Autowired
    public ProgramRepository programRepository;

    @Autowired
    public CountryRepository countryRepository;

    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public ProjectFileRepository projectFileRepository;

    @Autowired
    public PromptRepository promptRepository;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.config = RestAssured.config().sessionConfig(new SessionConfig().sessionIdName("SESSION"));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public String readResource(final String resourceName) {
        try {
            return StreamUtils.copyToString(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
        } catch (final IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    public String professionalSession() {
        if (professionalSession == null) {
            // init session
            professionalSession = RestAssured
                    .given()
                        .accept(ContentType.HTML)
                    .when()
                        .get("/professional/login")
                    .sessionId();

            // perform login
            professionalSession = RestAssured
                    .given()
                        .sessionId(professionalSession)
                        .csrf("/professional/login")
                        .accept(ContentType.HTML)
                        .contentType(ContentType.URLENC)
                        .formParam("email", PROFESSIONAL)
                        .formParam("password", PASSWORD)
                    .when()
                        .post("/professional/login")
                    .sessionId();
        }
        return professionalSession;
    }

    public String adminSession() {
        if (adminSession == null) {
            // init session
            adminSession = RestAssured
                    .given()
                        .accept(ContentType.HTML)
                    .when()
                        .get("/admin/login")
                    .sessionId();

            // perform login
            adminSession = RestAssured
                    .given()
                        .sessionId(adminSession)
                        .csrf("/admin/login")
                        .accept(ContentType.HTML)
                        .contentType(ContentType.URLENC)
                        .formParam("email", ADMIN)
                        .formParam("password", PASSWORD)
                    .when()
                        .post("/admin/login")
                    .sessionId();
        }
        return adminSession;
    }

}
