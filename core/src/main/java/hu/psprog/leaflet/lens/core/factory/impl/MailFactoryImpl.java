package hu.psprog.leaflet.lens.core.factory.impl;

import hu.psprog.leaflet.lens.core.config.MailProcessorConfigurationProperties;
import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import hu.psprog.leaflet.lens.core.exception.MailException;
import hu.psprog.leaflet.lens.core.factory.MailFactory;
import hu.psprog.leaflet.lens.core.validator.MailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link MailFactory}.
 * In order to generate the {@link Mail} object, this implementation executes the following steps:
 *  1) Determines the {@link MailRegistration} object based on the {@link MailType} specified on the {@link MailRequest}.
 *  2) Executes some preliminary validation steps.
 *  3) Determines the proper recipients.
 *  4) Translates the subject parameter of the email.
 *  5) Extends the provided content map with common parameters.
 *  6) Finally, builds the {@link Mail} object.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "tms")
public class MailFactoryImpl implements MailFactory {

    private static final String ATTRIBUTE_GENERATED_AT = "generatedAt";

    private final MessageSource messageSource;
    private final SimpleDateFormat mailDateFormat;
    private final List<MailValidator> mailValidators;
    private final MailProcessorConfigurationProperties mailProcessorConfigurationProperties;

    private Locale forcedLocale;

    @Autowired
    public MailFactoryImpl(MessageSource messageSource, SimpleDateFormat mailDateFormat, List<MailValidator> mailValidators,
                           MailProcessorConfigurationProperties mailProcessorConfigurationProperties) {
        this.messageSource = messageSource;
        this.mailDateFormat = mailDateFormat;
        this.mailValidators = mailValidators;
        this.mailProcessorConfigurationProperties = mailProcessorConfigurationProperties;
    }

    @Override
    public Mail buildMail(MailRequest mailRequest) {

        MailRegistration mailRegistration = mailProcessorConfigurationProperties.getRegistrations()
                .get(mailRequest.getMailType());

        if (Objects.isNull(mailRegistration)) {
            throw new MailException(String.format("Mail type [%s] is not registered", mailRequest.getMailType()));
        }

        mailValidators.forEach(mailValidator -> mailValidator.validate(mailRequest, mailRegistration));

        return Mail.builder()
                .recipients(extractRecipients(mailRequest))
                .subject(extractSubject(mailRequest, mailRegistration))
                .replyTo(mailRequest.getReplyTo())
                .template(mailRegistration.getTemplateName())
                .contentMap(createContentMap(mailRequest))
                .build();
    }

    void setForcedLocale(Locale forcedLocale) {
        this.forcedLocale = forcedLocale;
    }

    private List<String> extractRecipients(MailRequest mailRequest) {

        return mailRequest.getRecipients().isEmpty()
                ? List.of(mailProcessorConfigurationProperties.getAdminNotificationAddress())
                : mailRequest.getRecipients();
    }

    private String extractSubject(MailRequest mailRequest, MailRegistration mailRegistration) {

        String subjectKey = Optional.ofNullable(mailRequest.getOverrideSubjectKey())
                .orElse(mailRegistration.getDefaultSubjectKey());

        return translateSubject(subjectKey);
    }

    private String translateSubject(String subjectMessageKey) {
        return messageSource.getMessage(subjectMessageKey, null, subjectMessageKey, forcedLocale);
    }

    private Map<String, Object> createContentMap(MailRequest mailRequest) {

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put(ATTRIBUTE_GENERATED_AT, mailDateFormat.format(new Date()));
        contentMap.putAll(mailRequest.getContentMap());

        return contentMap;
    }
}
