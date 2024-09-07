package br.edu.infnet.micro.apigateway.Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${eureka.client.serviceUrl.defaultZone}")
    private String eurekaServerUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("eureka-server", r -> r
                .path("/eureka/**")
                .uri(eurekaServerUrl))
            .route("veiculo-service", r -> r
                .path("/veiculos/**")
                .uri("lb://veiculo-service"))
            .route("localizacao-service", r -> r
                .path("/localizacao/**")
                .uri("lb://localizacao-service"))
            .build();
    }

}