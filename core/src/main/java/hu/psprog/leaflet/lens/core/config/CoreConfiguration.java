package hu.psprog.leaflet.lens.core.config;

import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.text.SimpleDateFormat;
import java.util.Collections;

/**
 * Mail module configuration.
 *
 * @author Peter Smith
 */
@Configuration
public class CoreConfiguration {

    private static final String TEMPLATE_RESOLVER_PREFIX = "/mail/";
    private static final String TEMPLATE_RESOLVER_SUFFIX = ".html";
    private static final String CHARACTER_ENCODING = "UTF-8";

    @Bean
    public RequestAuthentication requestAuthentication() {
        return Collections::emptyMap;
    }

    @Bean
    @Autowired
    public SimpleDateFormat mailDateFormat(MailProcessorConfigurationProperties mailProcessorConfigurationProperties) {
        return new SimpleDateFormat(mailProcessorConfigurationProperties.getDatePattern());
    }

    @Bean
    @Autowired
    public TemplateEngine emailTemplateEngine(MessageSource messageSource) {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlEmailTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);

        return templateEngine;
    }

    private ITemplateResolver htmlEmailTemplateResolver() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(1);
        templateResolver.setPrefix(TEMPLATE_RESOLVER_PREFIX);
        templateResolver.setSuffix(TEMPLATE_RESOLVER_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(CHARACTER_ENCODING);
        templateResolver.setCacheable(false);

        return templateResolver;
    }
}
