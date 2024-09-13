package br.edu.infnet.micro.servicogerenciadoconsumidor.service;

import br.edu.infnet.micro.servicogerenciadoconsumidor.model.Caminhao;
import br.edu.infnet.micro.servicogerenciadoconsumidor.repository.CaminhaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class GerenciadorConsumidoresService {

    private static final Logger logger = LoggerFactory.getLogger(GerenciadorConsumidoresService.class);

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Autowired
    private KubernetesService kubernetesService;

    @Scheduled(fixedRateString = "${consumer.check.interval}")
    public void verificarEAtualizarConsumidores() {
        logger.info("Iniciando verificação de consumidores");
        List<Caminhao> todosOsCaminhoes = caminhaoRepository.findAll();
        
        for (Caminhao caminhao : todosOsCaminhoes) {
            if (caminhao.isAtivo()) {
                kubernetesService.criarOuAtualizarPodParaCaminhao(caminhao);
            } else {
                kubernetesService.removerPodParaCaminhao(caminhao.getMacAddress());
            }
        }
        
        logger.info("Verificação de consumidores concluída");
    }

    public Caminhao adicionarCaminhao(Caminhao caminhao) {
        logger.info("Adicionando novo caminhão: {}", caminhao.getMacAddress());
        Caminhao novoCaminhao = caminhaoRepository.save(caminhao);
        if (novoCaminhao.isAtivo()) {
            kubernetesService.criarOuAtualizarPodParaCaminhao(novoCaminhao);
        }
        return novoCaminhao;
    }

    public Optional<Caminhao> atualizarCaminhao(String id, Caminhao caminhaoAtualizado) {
        logger.info("Atualizando caminhão com ID: {}", id);
        return caminhaoRepository.findById(id)
            .map(caminhaoExistente -> {
                boolean eraAtivo = caminhaoExistente.isAtivo();
                caminhaoExistente.setMacAddress(caminhaoAtualizado.getMacAddress());
                caminhaoExistente.setPlaca(caminhaoAtualizado.getPlaca());
                caminhaoExistente.setAtivo(caminhaoAtualizado.isAtivo());
                caminhaoExistente.setMotorista(caminhaoAtualizado.getMotorista());
                caminhaoExistente.setCarreta(caminhaoAtualizado.getCarreta());
                
                Caminhao caminhaoSalvo = caminhaoRepository.save(caminhaoExistente);
                
                if (caminhaoSalvo.isAtivo()) {
                    kubernetesService.criarOuAtualizarPodParaCaminhao(caminhaoSalvo);
                } else if (eraAtivo && !caminhaoSalvo.isAtivo()) {
                    kubernetesService.removerPodParaCaminhao(caminhaoSalvo.getMacAddress());
                }
                
                return caminhaoSalvo;
            });
    }

    public boolean excluirCaminhao(String id) {
        logger.info("Excluindo caminhão com ID: {}", id);
        return caminhaoRepository.findById(id).map(caminhao -> {
            kubernetesService.removerPodParaCaminhao(caminhao.getMacAddress());
            caminhaoRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    public List<Caminhao> listarTodosCaminhoes() {
        logger.info("Listando todos os caminhões");
        return caminhaoRepository.findAll();
    }

    public Optional<Caminhao> buscarCaminhaoPorId(String id) {
        logger.info("Buscando caminhão com ID: {}", id);
        return caminhaoRepository.findById(id);
    }
}
