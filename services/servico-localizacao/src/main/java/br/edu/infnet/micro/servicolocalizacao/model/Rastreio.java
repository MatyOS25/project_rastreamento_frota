package br.edu.infnet.micro.servicolocalizacao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rastreio {
    private String macAddress;
    private double latitude;
    private double longitude;
    private double altitude;
    private double velocidade;
    private String direcao;
    private String statusVeiculo;
    private long timestamp;
}
