package com.lalin.ledger.config.db;

import com.lalin.ledger.exception.handler.LedgerExceptionHandler;
import lombok.AllArgsConstructor;
import org.jooq.impl.DataSourceConnectionProvider;

import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@AllArgsConstructor
@Configuration
public class InitConfig {

    private final DataSource dataSource;

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }
    @Bean
    public LedgerExceptionHandler exceptionTransformer() {
        return new LedgerExceptionHandler();
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());

        return jooqConfiguration;
    }
    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }
}

