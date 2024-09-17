package br.edu.infnet.micro.consumidor_caminhao.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("localizacoes")
public class LocalizacaoCassandra {

    @PrimaryKeyColumn(name = "placa", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String placa;

    @PrimaryKeyColumn(name = "timestamp", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant timestamp;

    private String macAddress;
    private double latitude;
    private double longitude;
    private double altitude;
    private double velocidade;
    private String direcao;
    private String statusVeiculo;
}

