package hu.psprog.leaflet.lens.core.validator;

import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;

/**
 * Interface for complex mail validation steps.
 *
 * @author Peter Smith
 */
public interface MailValidator {

    /**
     * Validates the given {@link MailRequest} object against the implemented directives.
     * {@link MailRegistration} object can be used to gather specific requirements for the {@link MailRequest}.
     *
     * @param mailRequest {@link MailRequest} object containing the request data
     * @param mailRegistration {@link MailRegistration} object containing configuration directives for the request
     * @throws MailValidationException on invalid {@link MailRequest}
     */
    void validate(MailRequest mailRequest, MailRegistration mailRegistration);
}
