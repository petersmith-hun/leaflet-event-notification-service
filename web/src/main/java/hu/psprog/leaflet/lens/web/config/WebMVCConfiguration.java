package hu.psprog.leaflet.lens.web.config;

import hu.psprog.leaflet.lens.web.converter.MailTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Additional Spring MVC configuration.
 *
 * @author Peter Smith
 */
@Configuration
public class WebMVCConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new MailTypeConverter());
    }
}
