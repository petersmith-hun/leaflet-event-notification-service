package hu.psprog.leaflet.lens.core.validator.impl;

import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.validator.MailValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * {@link MailValidator} implementation checking if the mail configuration allows specifying a reply-to address when one is actually given.
 *
 * @author Peter Smith
 */
@Component
public class ReplyToPolicyMailValidator implements MailValidator {

    @Override
    public void validate(MailRequest mailRequest, MailRegistration mailRegistration) {

        if (!mailRegistration.isReplyToAllowed() && Objects.nonNull(mailRequest.getReplyTo())) {
            throw new MailValidationException(String.format("Overriding reply-to field is not allowed for %s mails", mailRequest.getMailType()));
        }
    }
}
