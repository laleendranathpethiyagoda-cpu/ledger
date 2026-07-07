package com.lalin.ledger.repo;


import com.lalin.ledger.jooq.pg.tables.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
@Getter
@Setter
public class ClientRepo {

    private DataSourceConnectionProvider dataSource; //injecting this as the dialect is not getting
    //picked up by the dslContext bean

    public UUID createNewClient(String clientName, String email, String description, String contactPerson) {
        return  DSL.using(dataSource, SQLDialect.POSTGRES)
                .insertInto(Client.CLIENT,
                        Client.CLIENT.CLIENTNAME,
                        Client.CLIENT.EMAIL,
                        Client.CLIENT.DESCRIPTION,
                        Client.CLIENT.CONTACTPERSON)
                .values(clientName, email, description, contactPerson)
                .returning(Client.CLIENT.CLIENTID)
                .fetchOne(Client.CLIENT.CLIENTID);
    }
}
