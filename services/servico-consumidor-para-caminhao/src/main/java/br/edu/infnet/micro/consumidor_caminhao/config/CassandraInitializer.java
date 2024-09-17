package br.edu.infnet.micro.consumidor_caminhao.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CassandraInitializer {

    @Autowired
    private CassandraTemplate cassandraTemplate;

    @PostConstruct
    public void initializeCassandraSchema() {
        cassandraTemplate.getCqlOperations().execute(
            "CREATE TABLE IF NOT EXISTS localizacoes (" +
            "placa text, " +
            "timestamp timestamp, " +
            "mac_address text, " +
            "latitude double, " +
            "longitude double, " +
            "altitude double, " +
            "velocidade double, " +
            "direcao text, " +
            "status_veiculo text, " +
            "PRIMARY KEY ((placa), timestamp)" +
            ") WITH CLUSTERING ORDER BY (timestamp DESC)"
        );
    }
}
