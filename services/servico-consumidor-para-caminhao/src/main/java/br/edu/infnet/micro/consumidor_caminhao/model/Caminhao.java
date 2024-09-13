package br.edu.infnet.micro.consumidor_caminhao.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Caminhao {
    private String id;
    private String macAddress;
    private String placa;
    private boolean ativo;
    private String motorista;
    private String carreta;
    

    public static class CaminhaoBuilder {
        private boolean ativo = true; // Define um valor padrão para ativo
        
        public CaminhaoBuilder desativado() {
            this.ativo = false;
            return this;
        }
    }
}
