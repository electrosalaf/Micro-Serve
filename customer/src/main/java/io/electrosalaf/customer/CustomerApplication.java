package io.electrosalaf.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "io.electrosalaf.customer",
                "io.electrosalaf.amqp",
        }
)
@EnableEurekaClient
@EnableFeignClients(
        basePackages = "io.electrosalaf.clients"
)
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}