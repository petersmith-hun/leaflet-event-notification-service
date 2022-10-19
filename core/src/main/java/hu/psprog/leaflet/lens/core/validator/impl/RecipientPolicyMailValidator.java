package hu.psprog.leaflet.lens.core.validator.impl;

import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.validator.MailValidator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Predicate;

/**
 * {@link MailValidator} implementation checking if the number of specified recipients matches the expected count
 * based on the given policy in {@link MailRegistration}.
 *
 * Validity requirements for the policy types are the following:
 *  - NONE: recipient list must be empty (admin notification address will be applied automatically);
 *  - SINGLE: 1 recipient address can and must be specified;
 *  - MULTIPLE: 1 or more recipient addresses can be specified, at least 1 is mandatory.
 *
 * @author Peter Smith
 */
@Component
public class RecipientPolicyMailValidator implements MailValidator {

    private static final Map<MailRegistration.RecipientPolicy, Predicate<MailRequest>> POLICY_VALIDATION_MAP = createPolicyValidationMap();

    @Override
    public void validate(MailRequest mailRequest, MailRegistration mailRegistration) {

        Predicate<MailRequest> policyValidator = POLICY_VALIDATION_MAP.get(mailRegistration.getRecipientPolicy());
        if (!policyValidator.test(mailRequest)) {
            throw new MailValidationException(String.format("Mail does not conform the required recipient policy of %s with %d recipients",
                    mailRegistration.getRecipientPolicy(), mailRequest.getRecipients().size()));
        }
    }

    private static Map<MailRegistration.RecipientPolicy, Predicate<MailRequest>> createPolicyValidationMap() {

        return Map.of(
                MailRegistration.RecipientPolicy.NONE, mailRequest -> mailRequest.getRecipients().isEmpty(),
                MailRegistration.RecipientPolicy.SINGLE, mailRequest -> mailRequest.getRecipients().size() == 1,
                MailRegistration.RecipientPolicy.MULTIPLE, mailRequest -> mailRequest.getRecipients().size() > 0
        );
    }
}
