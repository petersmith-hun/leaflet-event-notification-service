package hu.psprog.leaflet.lens.core.config;

import hu.psprog.leaflet.lens.core.domain.MailType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mail processor configuration properties.
 * Required properties:
 *  - mail.registrations: map of registered mail types and their configuration
 *  - mail.admin-notification-address: system notification address
 *  - mail.sender.address: sender (typically a no-reply) address
 *  - mail.sender.name: sender name
 *
 * Optional properties:
 *  - mail.date-pattern: format pattern for dates appearing in mails, defaults to "yyyy-MM-dd HH:mm:ss z"
 *
 * @author Peter Smith
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Component
@ConfigurationProperties(prefix = "mail")
public class MailProcessorConfigurationProperties {

    private final Map<MailType, MailRegistration> registrations = new HashMap<>();

    private String adminNotificationAddress;
    private String senderAddress;
    private String senderName;

    private String datePattern = "yyyy-MM-dd HH:mm:ss z";
}
