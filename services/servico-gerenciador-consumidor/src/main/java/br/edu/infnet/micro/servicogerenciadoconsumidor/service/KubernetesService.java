package br.edu.infnet.micro.servicogerenciadoconsumidor.service;

import br.edu.infnet.micro.servicogerenciadoconsumidor.model.Caminhao;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KubernetesService {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesService.class);

    @Autowired
    private CoreV1Api coreV1Api;

    @Value("${kubernetes.namespace}")
    private String namespace;

    @Value("${kubernetes.consumer.image}")
    private String consumerImage;

    public void criarOuAtualizarPodParaCaminhao(Caminhao caminhao) {
        String podName = "consumidor-" + caminhao.getMacAddress().replace(":", "-");
        try {
            V1Pod existingPod = coreV1Api.readNamespacedPod(podName, namespace, null);
            if (existingPod == null) {
                criarPod(podName, caminhao);
            } else {
                atualizarPod(existingPod, caminhao);
            }
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                criarPod(podName, caminhao);
            } else {
                logger.error("Erro ao verificar pod existente", e);
            }
        }
    }

    private void criarPod(String podName, Caminhao caminhao) {
        logger.info("Criando pod para o caminhão com MAC Address: {}", caminhao.getMacAddress());
        
        // Sanitize o nome do pod
        String sanitizedPodName = sanitizeName(podName);
        
        // Sanitize o MAC address para uso como label
        String sanitizedMacAddress = sanitizeName(caminhao.getMacAddress());
        
        V1Pod pod = new V1PodBuilder()
                .withNewMetadata()
                    .withName(sanitizedPodName)
                    .addToLabels("app", "consumidor-caminhao")
                    .addToLabels("mac-address", sanitizedMacAddress)
                .endMetadata()
                .withNewSpec()
                    .addNewContainer()
                        .withName("consumidor")
                        .withImage(consumerImage)
                        .addNewEnv().withName("MAC_ADDRESS").withValue(caminhao.getMacAddress()).endEnv()
                        .addNewEnv().withName("PLACA").withValue(caminhao.getPlaca()).endEnv()
                        .addNewEnv().withName("ATIVO").withValue(String.valueOf(caminhao.isAtivo())).endEnv()
                        .addNewEnv().withName("MOTORISTA").withValue(caminhao.getMotorista()).endEnv()
                        .addNewEnv().withName("CARRETA").withValue(caminhao.getCarreta()).endEnv()
                    .endContainer()
                .endSpec()
                .build();

        try {
            coreV1Api.createNamespacedPod(namespace, pod, null, null, null, null);
            logger.info("Pod criado para o caminhão com MAC Address: {}", caminhao.getMacAddress());
        } catch (ApiException e) {
            logger.error("Erro ao criar pod: Código de status: {}, Corpo da resposta: {}", e.getCode(), e.getResponseBody(), e);
        }
    }

    private String sanitizeName(String name) {
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9-.]", "-")
                   .replaceAll("^[^a-z0-9]+", "")
                   .replaceAll("[^a-z0-9]+$", "");
    }

    private void atualizarPod(V1Pod existingPod, Caminhao caminhao) {
        logger.info("Atualizando pod para o caminhão com MAC Address: {}", caminhao.getMacAddress());
        
        V1Pod updatedPod = new V1PodBuilder(existingPod)
                .editSpec()
                    .editContainer(0)
                        .withEnv()
                            .addNewEnv().withName("MAC_ADDRESS").withValue(caminhao.getMacAddress()).endEnv()
                            .addNewEnv().withName("PLACA").withValue(caminhao.getPlaca()).endEnv()
                            .addNewEnv().withName("ATIVO").withValue(String.valueOf(caminhao.isAtivo())).endEnv()
                            .addNewEnv().withName("MOTORISTA").withValue(caminhao.getMotorista()).endEnv()
                            .addNewEnv().withName("CARRETA").withValue(caminhao.getCarreta()).endEnv()
                    .endContainer()
                .endSpec()
                .build();

        try {
            coreV1Api.replaceNamespacedPod(existingPod.getMetadata().getName(), namespace, updatedPod, null, null, null, null);
        } catch (ApiException e) {
            logger.error("Erro ao atualizar pod", e);
        }
    }

    public void removerPodParaCaminhao(String macAddress) {
        String podName = "consumidor-" + sanitizeName(macAddress);
        try {
            coreV1Api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
            logger.info("Pod removido para o caminhão com MAC Address: {}", macAddress);
        } catch (ApiException e) {
            if (e.getCode() != 404) {
                logger.error("Erro ao remover pod", e);
            } else {
                logger.warn("Pod não encontrado para o caminhão com MAC Address: {}", macAddress);
            }
        }
    }
}
