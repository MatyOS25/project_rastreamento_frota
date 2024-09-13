package br.edu.infnet.micro.consumidor_caminhao.service;

import br.edu.infnet.micro.consumidor_caminhao.model.Caminhao;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FilaManager {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private final Map<String, Caminhao> filas = new ConcurrentHashMap<>();

    public void criarOuAtualizarFila(Caminhao caminhao) {
        String nomeFila = "caminhao." + caminhao.getPlaca();
        
        // Atualizar ou adicionar as informações do caminhão no mapa local
        filas.put(nomeFila, caminhao);

        // Criar ou atualizar a fila com as informações do caminhão
        Map<String, Object> arguments = new ConcurrentHashMap<>();
        arguments.put("x-mac-address", caminhao.getMacAddress());
        arguments.put("x-placa", caminhao.getPlaca());
        arguments.put("x-ativo", caminhao.isAtivo());
        arguments.put("x-motorista", caminhao.getMotorista());
        arguments.put("x-carreta", caminhao.getCarreta());

        // Criar a fila com os argumentos personalizados
        Queue queue = new Queue(nomeFila, true, false, false, arguments);
        rabbitAdmin.declareQueue(queue);

        System.out.println("Fila criada/atualizada para o caminhão com placa: " + caminhao.getPlaca());
    }

    public Caminhao obterInformacoesFila(String placa) {
        return filas.get("caminhao." + placa);
    }

    public boolean filaExiste(String placa) {
        return filas.containsKey("caminhao." + placa);
    }


}
