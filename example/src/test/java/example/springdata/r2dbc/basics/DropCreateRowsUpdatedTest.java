package example.springdata.r2dbc.basics;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/*
    The tests are successful when a null is returned by the publisher after it subscribes to rowsUpdated().

    This issue is present in version 0.1.0.M2
    In BUILD-SNAPSHOT this issue is fixed.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InfrastructureConfiguration.class)
public class DropCreateRowsUpdatedTest {

    DatabaseClient client = DatabaseClient.create (ConnectionFactories.get(ConnectionFactoryOptions.builder()
            .option(DRIVER, "postgresql")
            .option(HOST, "localhost")
            .option(PORT, 5432)
            .option(USER, "postgres")
            .option(PASSWORD, "admin")
            .option(DATABASE, "test")
            .build()));

    String CREATE_TABLE_LEGOSET = "CREATE TABLE legoset (\n" //
            + "    id          integer CONSTRAINT id PRIMARY KEY,\n" //
            + "    name        varchar(255) NOT NULL,\n" //
            + "    manual      integer NULL\n," //
            + "    cert        bytea NULL\n" //
            + ");";


    //Even though the sql statement is successfully executed in the database,
    //the result is that the publisher returns null upon completion.
    //This behavior is the same for the 'drop table' and the other methods in this class.

    @Test(expected = NullPointerException.class)
    public void createTableBlock() {
        Hooks.onOperatorDebug();
        dropTableReturnRowsUpdatedReturnsNull();

        client.execute(CREATE_TABLE_LEGOSET)
                    .fetch()
                    .rowsUpdated()
                    .map(rowsUpdated-> {Assert.assertNotNull(rowsUpdated); return rowsUpdated;})
                    .blockOptional()
                    .orElseThrow(NullPointerException::new); // Result: NullPointerException is thrown.

    }

    @Test
    public void dropTableReturnRowsUpdatedReturnsNull() {
        Hooks.onOperatorDebug();

        String sql = "DROP TABLE IF EXISTS legoset";
        client.execute(sql)
                .fetch()
                .rowsUpdated()
                .map(rowsUpdated-> {Assert.assertNotNull(rowsUpdated); return rowsUpdated;})
                .doOnNext(Assert::assertNotNull)     // Result: rowsUpdated != null  (as expected)
                .doOnSuccess(Assert::assertNotNull)  // Result:  throws assertion (not expected)
                .as(StepVerifier::create)
                .expectError(AssertionError.class)
                .verify(Duration.ZERO);
    }

    @Test
    public void createTableVerifyComplete() {
        Hooks.onOperatorDebug();
        dropTableReturnRowsUpdatedReturnsNull();

        Mono<String> monoSql = Mono.just(CREATE_TABLE_LEGOSET);
        monoSql.flatMap( sql-> client.execute(sql)
                                    .fetch()
                                    .rowsUpdated()
                                    .map(rowsUpdated-> {
                                         Assert.assertNotNull(rowsUpdated);
                                        return rowsUpdated;
                                    })
                         )
                        .doOnNext(Assert::assertNotNull)     // result: rowsUpdated != null   (as expected)
                        .doOnSuccess(Assert::assertNotNull)  // result: throws assertion
                        .as(StepVerifier::create)
                        .expectError(AssertionError.class)
                        .verify();
    }

}
