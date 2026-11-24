package tienda.neonfoxgaming.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

import tienda.neonfoxgaming.repository.UsuarioRepository;

@Configuration
public class SecurityConfig {
    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarios) {
        return email -> usuarios.findByEmailIgnoreCase(email.trim())
                .map(usuario -> User.withUsername(usuario.getEmail())
                        .password(usuario.getPasswordHash())
                        .roles(usuario.getRole().toUpperCase(Locale.ROOT))
                        .disabled("Inactivo".equalsIgnoreCase(usuario.getEstado()))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/auth/**", "/api/v2/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v2/productos/**",
                                "/api/v1/categorias/**", "/api/v2/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos", "/api/v2/pedidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/*", "/api/v2/pedidos/*").permitAll()
                        .requestMatchers("/api/v1/**", "/api/v2/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
