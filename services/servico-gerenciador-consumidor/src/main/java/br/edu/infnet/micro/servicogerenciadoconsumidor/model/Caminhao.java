package br.edu.infnet.micro.servicogerenciadoconsumidor.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "caminhoes")
@NoArgsConstructor
@AllArgsConstructor
public class Caminhao {
    @Id
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
