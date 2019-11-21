package example.springdata.r2dbc.basics.configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Profile("dev")
@Configuration
@EnableR2dbcRepositories
public class H2Config extends AbstractR2dbcConfiguration {

    @Bean("myH2ConnectionFactory")
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.
//                parse("r2dbc:h2:mem:;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE////dev?MODE=PostgreSQL"));
             parse("r2dbc:h2:mem:////dev?MODE=PostgreSQL"));
    }
}
