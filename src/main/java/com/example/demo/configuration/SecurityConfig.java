package com.example.demo.configuration;

import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.experimental.NonFinal;

//Chú thích này cho phép phân quyền trên METHOD
@EnableMethodSecurity
////Chú thích này chỉ ra rằng lớp này là một nguồn định nghĩa bean cho ngữ cảnh ứng dụng, cấu hình.
@Configuration
//Annotation này cho phép hỗ trợ bảo mật web của Spring Security.
@EnableWebSecurity
public class SecurityConfig {
	@NonFinal
	@Value("${jwt.signerKey}")
	private String SIGNER_KEY;
	private final String[] PUBLIC_ENDPOINT_POST = { "/login", "/user", "/logoutaccount", "/introspect", "/refreshToken",
			"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**" };
	private final String[] PUBLIC_ENDPOINT_GET = { "/user/{id}", "/swagger-ui/**", "/v3/api-docs/**",
			"/swagger-resources/**", "/swagger-ui.html", "/webjars/**" };

	@Autowired
	private CustomJWTDecoder customJWTDecoder;

	@Bean
	// Nó định nghĩa một SecurityFilterChain, được sử dụng để cấu hình bảo mật cho
	// ứng dụng.
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.cors(Customizer.withDefaults());
		// ✅ 1. Enable CORS using the CorsConfigurationSource bean

		// những end point "/user, /login,/...." nào ta sẽ bảo vệ, và những end point ta
		// cho user truy cập vào mà k cần bả vệ
		// vd k cần bảo vệ : đăng ký tk, trang chủ, đăng nhập
		// ta tiến hành cấu hình bằng requestMatchers(...) chứa 2 param : Method và
		// EndPoint cho các endpoint mà k cần bảo vệ
		httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST)
				.permitAll().requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
//						kiểm tra trong TOKEN nếu là ADMIN thì được call đến endpoint này
				// .requestMatchers(HttpMethod.GET,"/users").hasAuthority("SCOPE_ADMIN")
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated());
		// tắt CSRF
		httpSecurity.csrf(AbstractHttpConfigurer::disable);

		// đối với những endpoint mà k mở public thì ta muốn user cung cấp một TOKEN hợp
		// lệ mới được access
		// sd oauth2-resource-service
		// xác thực TOKEN
		httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(JwtConfigurer -> JwtConfigurer.decoder(customJWTDecoder)) // xác
																															// thực
																															// TOKEN
																															// có
																															// xét
																															// th
																															// lôgout
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // khi xác thực false thì ta sẽ điều hướng
																				// user đi đâu, trong TH này ta chỉ cần
																				// trả về một cái ErrorMessage thôi

		);
		return httpSecurity.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		// bao gồm 2 tham số : secretKey và Thuật toán
		SecretKeySpec secretkey = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
		return NimbusJwtDecoder.withSecretKey(secretkey).macAlgorithm(MacAlgorithm.HS512).build();

	}

	// ✅ 2. Define the CorsConfigurationSource bean for Spring Security
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 👇 Replace with your frontend's actual origin(s)
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*")); // Or specify headers like "Authorization", "Content-Type"
		// configuration.setAllowCredentials(true); // Uncomment if your frontend sends
		// credentials and you need them

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply this CORS configuration to all paths
		return source;
	}

}
