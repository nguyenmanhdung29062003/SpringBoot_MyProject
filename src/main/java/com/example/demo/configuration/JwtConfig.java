package com.example.demo.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

//Đánh dấu đây là class cấu hình Spring
@Configuration
public class JwtConfig {
	// Đánh dấu method này tạo ra một Spring Bean
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Converter cho roles
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles"); // Tìm claim có tên "roles" trong toke
        rolesConverter.setAuthorityPrefix("ROLE_"); // thêm prefix ROLE_
        
        // Converter cho scopes
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthoritiesClaimName("scope"); // claim name cho scope
        scopesConverter.setAuthorityPrefix(""); // không thêm prefix
        
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        
        // Kết hợp cả roles và scopes
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
        	// Convert roles
            Collection<GrantedAuthority> roles = rolesConverter.convert(jwt);
         // Convert scopes
            Collection<GrantedAuthority> scopes = scopesConverter.convert(jwt);
         // Gộp roles và scopes
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.addAll(roles);
            authorities.addAll(scopes);
            
            return authorities;
        });
        
        return jwtConverter;
    }
}
