package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Attendance Monitoring System")
                        .version("1.0.0")
                        .description("""
                    Backend API for the Smart Attendance Monitoring and Analytics System.
                    
                    Authentication:
                    - Teacher registers via /auth/register
                    - Teacher creates student accounts via /auth/create-student
                    - Both roles login via /auth/login
                    - Login response contains role and studentId for routing
                    
                    Role based access:
                    - TEACHER: full dashboard, all students, mark attendance
                    - STUDENT: own attendance only, own report download
                    
                    Important rules:
                    - date format must be YYYY-MM-DD
                    - status must be exactly Present or Absent
                    - studentId in attendance must match an existing student rollNumber
                    """)
                        .contact(new Contact()
                                .name("Backend Team")));
    }
}