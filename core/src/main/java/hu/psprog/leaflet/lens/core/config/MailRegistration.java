package hu.psprog.leaflet.lens.core.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Domain class holding information about a mail configuration.
 *
 * @author Peter Smith
 */
@Data
@Setter(AccessLevel.PACKAGE)
public class MailRegistration {

    /**
     * Default i18n key to generate the mail's subject.
     */
    private String defaultSubjectKey;

    /**
     * Mail template name (without prefix and suffix).
     */
    private String templateName;

    /**
     * Recipient policy of mail, determining how many recipients can be specified.
     */
    private RecipientPolicy recipientPolicy = RecipientPolicy.SINGLE;

    /**
     * Flag determining whether a reply-to address can be specified for this mail type.
     */
    private boolean replyToAllowed = false;

    /**
     * Available recipient policies.
     */
    public enum RecipientPolicy {

        /**
         * No recipient can be defined explicitly.
         */
        NONE,

        /**
         * One recipient can and must be specified.
         */
        SINGLE,

        /**
         * One or more recipients can be specified, at least one is required.
         */
        MULTIPLE
    }
}
