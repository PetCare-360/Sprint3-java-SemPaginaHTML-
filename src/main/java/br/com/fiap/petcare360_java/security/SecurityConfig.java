package br.com.fiap.petcare360_java.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.com.fiap.petcare360_java.model.AppUser;
import br.com.fiap.petcare360_java.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/auth/register", "/auth/login", "/swagger-ui/**", "/swagger-ui.html", "/v3/**").permitAll()
						.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
						.requestMatchers("/vet", "/vet/**").hasRole("VETERINARIO")
						.requestMatchers("/tutor", "/tutor/**").hasRole("CLIENTE")
						.requestMatchers("/pets/**", "/messages/**", "/appointments/**", "/recommendations/**").hasAnyRole("CLIENTE", "ADMIN", "VETERINARIO")
						.requestMatchers("/api/iot/**").hasAnyRole("ADMIN", "VETERINARIO")
						.anyRequest().authenticated())
				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable())
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint((request, response, authException) ->
								response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado")))
				.logout(logout -> logout
						.logoutUrl("/auth/logout")
						.logoutSuccessHandler((request, response, authentication) ->
								response.setStatus(HttpServletResponse.SC_NO_CONTENT)));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService(AppUserRepository userRepository) {
		return email -> {
			AppUser user = userRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("Usuário não localizado"));

			return User.builder()
					.username(user.getEmail())
					.password(user.getPasswordHash())
					.disabled(Boolean.FALSE.equals(user.getEnabled()))
					.authorities(user.getRoles().stream()
							.map(role -> role.getName())
							.toArray(String[]::new))
					.build();
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
