package hu.psprog.leaflet.lens.web.config;

import hu.psprog.leaflet.lens.core.domain.MailType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
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

    private static final String PATH_TEMPLATE = "/mail/%s";
    private static final String SCOPE_TEMPLATE = "SCOPE_write:mail:%s";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return registerPathsByMailTypes(http)

                .csrf()
                    .disable()

                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()

                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)

                .build();
    }

    private HttpSecurity registerPathsByMailTypes(HttpSecurity http) throws Exception {

        var authorizeRequests = http.authorizeRequests();

        Stream.of(MailType.values())
                .map(mailType -> mailType.name().toLowerCase())
                .map(this::createPathToScopeMapping)
                .forEach(pathToScopeMapping -> authorizeRequests
                        .mvcMatchers(HttpMethod.POST, pathToScopeMapping.getLeft())
                            .hasAuthority(pathToScopeMapping.getRight()));

        return http;
    }

    private Pair<String, String> createPathToScopeMapping(String mailTypePathVariable) {

        return Pair.of(
                String.format(PATH_TEMPLATE, mailTypePathVariable),
                String.format(SCOPE_TEMPLATE, mailTypePathVariable));
    }
}
