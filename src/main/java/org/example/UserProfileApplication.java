package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileApplication.class, args);
    }
}

//SELECT BIN_TO_UUID(id) AS id, email, username, created_at FROM user_profiles;