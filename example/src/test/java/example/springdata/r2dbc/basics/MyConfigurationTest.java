package example.springdata.r2dbc.basics;

import example.springdata.r2dbc.basics.configuration.H2Config;
import example.springdata.r2dbc.basics.configuration.PostgresConfig;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InfrastructureConfiguration.class)
public class MyConfigurationTest {
    //create table TRAVEL_TIME_ENTITY (id varchar(255) not null,  name varchar(255), pubDate timestamp with time zone not null, retrievedFromThirdParty timestamp with time zone, type varchar(255), length int4, travelTime int4, velocity int4, primary key (pubDate, id));
    List<String> statements = Arrays.asList(//
            "DROP TABLE IF EXISTS customer;",
            "CREATE TABLE customer ( id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);");
    @Test
    public void h2ManualConfigTest() {
        Hooks.onOperatorDebug();
        // ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        DatabaseClient client = DatabaseClient.create(new H2Config().connectionFactory());

        statements.forEach(it -> client.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }

    @Test
    public void h2PoolManualConfigTest() {
        Hooks.onOperatorDebug();
        //ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "h2:mem") // driver identifier, PROTOCOL is delegated as DRIVER by the pool.
                .option(HOST, "localhost")
                .option(USER, "sa")
                .option(PASSWORD, "admin")
                .option(DATABASE, "trafficdata")
                .build());

        DatabaseClient client = DatabaseClient.create(connectionFactory);

        statements.forEach(it -> client.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }
    @Test
    public void postgresManualConfigTest() {
        Hooks.onOperatorDebug();
        //ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");
        options.put("statement_timeout", "5m");

        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .username("postgres")
                .password("admin")
                .database("trafficdata")
                .build());

        DatabaseClient client = DatabaseClient.create(connectionFactory);


        statements.forEach(it -> client.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }

    @Test
    public void postgresPoolManualConfigTest() {
        Hooks.onOperatorDebug();
        //ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql") // driver identifier, PROTOCOL is delegated as DRIVER by the pool.
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "postgres")
                .option(PASSWORD, "admin")
                .option(DATABASE, "trafficdata")
                .build());

        DatabaseClient client = DatabaseClient.create(connectionFactory);


        List<String> statements = Arrays.asList(//
                "DROP TABLE IF EXISTS customer;",
                "CREATE TABLE customer ( id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);");

        statements.forEach(it -> client.execute(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }


}
