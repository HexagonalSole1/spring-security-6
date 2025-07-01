package org.devquality.safetyauthservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class ApplicationConfig {

    private Security security = new Security();
    private Features features = new Features();

    @Data
    public static class Security {
        private Jwt jwt = new Jwt();
        private Cors cors = new Cors();

        @Data
        public static class Jwt {
            private String header = "Authorization";
            private String prefix = "Bearer ";
        }

        @Data
        public static class Cors {
            private String allowedOrigins;
            private String allowedMethods;
            private String allowedHeaders;
            private String exposedHeaders;
            private boolean allowCredentials = true;
            private long maxAge = 3600;
        }
    }

    @Data
    public static class Features {
        private boolean emailVerification = false;
        private boolean phoneVerification = false;
        private boolean twoFactorAuth = false;
    }
}