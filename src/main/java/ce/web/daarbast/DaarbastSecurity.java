package ce.web.daarbast;

import java.util.Arrays;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import ce.web.daarbast.security.PathConfiguration;
import ce.web.daarbast.security.RsaKeyConfiguration;
import ce.web.daarbast.security.userdetails.DaarbastUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({ RsaKeyConfiguration.class, PathConfiguration.class })
public class DaarbastSecurity {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector,
            DaarbastUserDetailsService userDetailsService, PathConfiguration pathConfiguration, JwtDecoder jwtDecoder)
            throws Exception {
        return http
                .csrf(it -> it.disable())
                .cors(it -> it.disable())
                .headers(it -> it.disable())
                .authorizeHttpRequests(it -> {
                    pathConfiguration.permitted().forEach(path -> it.requestMatchers(path).permitAll());
                    it.anyRequest().authenticated();
                })
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder)))
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return (HttpServletRequest request) -> {
            var cookies = request.getCookies();
            return cookies == null ? null
                    : Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName().equals(SecurityConstants.authorizationKey))
                            .map(Cookie::getValue)
                            .filter(value -> value.startsWith(SecurityConstants.tokenPrefix))
                            .map(value -> value.substring(SecurityConstants.tokenPrefix.length()))
                            .findFirst()
                            .orElse(null);
        };
    }

    @Bean
    public JwtDecoder jwtDecoder(RsaKeyConfiguration rsaKeyConfiguration) {
        return NimbusJwtDecoder.withPublicKey(rsaKeyConfiguration.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(RsaKeyConfiguration rsaKeyConfiguration) {
        var jwk = new RSAKey.Builder(rsaKeyConfiguration.publicKey())
                .privateKey(rsaKeyConfiguration.privateKey()).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
