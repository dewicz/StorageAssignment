package storage.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "storage.controller")  // Ensure controller is scanned
public class StorageApp {
    public static void main(String[] args) {
        SpringApplication.run(StorageApp.class, args);
    }
}
