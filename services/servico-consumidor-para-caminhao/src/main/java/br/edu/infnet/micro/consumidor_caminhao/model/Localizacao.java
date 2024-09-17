package br.edu.infnet.micro.consumidor_caminhao.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Localizacao {

    @JsonProperty("macAddress")
    private String macAddress;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("altitude")
    private double altitude;

    @JsonProperty("velocidade")
    private double velocidade;

    @JsonProperty("direcao")
    private String direcao;

    @JsonProperty("statusVeiculo")
    private String statusVeiculo;

    @JsonProperty("timestamp")
    private long timestamp;
}
