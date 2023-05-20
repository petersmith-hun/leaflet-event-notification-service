package hu.psprog.leaflet.lens.web.config;

import hu.psprog.leaflet.lens.core.domain.MailType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Stream;

/**
 * Spring Web Security configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String ENDPOINT_ACTUATOR = "/actuator/**";

    private static final String PATH_TEMPLATE = "/mail/%s";
    private static final String SCOPE_TEMPLATE = "SCOPE_write:mail:%s";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return registerPathsByMailTypes(http)

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwtConfigurer -> {}))

                .build();
    }

    private HttpSecurity registerPathsByMailTypes(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(registry -> {

            registry.requestMatchers(HttpMethod.GET, ENDPOINT_ACTUATOR)
                            .permitAll();

            Stream.of(MailType.values())
                    .map(mailType -> mailType.name().toLowerCase())
                    .map(this::createPathToScopeMapping)
                    .forEach(pathToScopeMapping -> registry
                            .requestMatchers(HttpMethod.POST, pathToScopeMapping.getLeft())
                            .hasAuthority(pathToScopeMapping.getRight()));
        });
    }

    private Pair<String, String> createPathToScopeMapping(String mailTypePathVariable) {

        return Pair.of(
                String.format(PATH_TEMPLATE, mailTypePathVariable),
                String.format(SCOPE_TEMPLATE, mailTypePathVariable));
    }
}
