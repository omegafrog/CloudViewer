package user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"core", "user"})
@ConfigurationPropertiesScan(basePackages = {"core", "user"})
public class CloudViewerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudViewerApplication.class, args);
    }
}
