package br.edu.infnet.micro.consumidor_caminhao.config;

import br.edu.infnet.micro.consumidor_caminhao.ConsumidorCaminhaoApplication;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class EurekaConfig {

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        String hostname = config.getHostname();
        String ipAddress = config.getIpAddress();
        String instanceId = String.format("%s:%s:%s", hostname, ConsumidorCaminhaoApplication.class.getSimpleName(), UUID.randomUUID().toString());
        config.setInstanceId(instanceId);
        config.setIpAddress(ipAddress);
        return config;
    }
}
