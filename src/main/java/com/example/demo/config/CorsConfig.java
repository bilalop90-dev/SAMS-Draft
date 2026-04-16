package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")           // all your endpoints
                .allowedOrigins("http://localhost:3000")  // React runs here
                .allowedOrigins("http://localhost:5500")
                .allowedOrigins("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
//```
//
//        **This is critical.** Without this, React cannot talk to your backend at all. Add this to your `config` package right now.
//
//        ---
//
//        ### Your Coordination Workflow As Project Lead
//```
//WEEK 1 — Foundation
//│
//        ├── You share API documentation with Frontend team
//├── You share MongoDB models with Database team
//├── You add CORS configuration
//└── Everyone sets up their local environment
//
//WEEK 2 — Integration
//│
//        ├── Frontend calls your APIs on localhost
//├── Database team sets up indexes
//├── Testing team writes test cases from your docs
//└── Daily 10 min sync — who is blocked on what?
//
//WEEK 3 — Testing
//│
//        ├── Testing team runs all scenarios
//├── Frontend reports any API issues to you
//├── You fix and update API docs if anything changes
//└── Deployment team prepares server setup
//
//WEEK 4 — Deployment
//│
//        ├── Deployment team hosts Spring Boot on a server
//├── React frontend is hosted separately
//├── Both point to same MongoDB
//└── Final testing on live environment