package hu.psprog.leaflet.lens.core.client.impl;

import hu.psprog.leaflet.lens.core.client.MailClient;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryStatus;
import io.reactivex.rxjava3.core.Observable;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link MailClient}.
 *
 * @author Peter Smith
 */
@Service
@Slf4j
class MailClientImpl implements MailClient {

    private final MailProcessor mailProcessor;
    private final Validator validator;

    @Autowired
    public MailClientImpl(MailProcessor mailProcessor, Validator validator) {
        this.mailProcessor = mailProcessor;
        this.validator = validator;
    }

    @Override
    public Observable<MailDeliveryInfo> sendMail(Mail mail) {

        return Observable.create(emitter -> {
            MailDeliveryInfo.MailDeliveryInfoBuilder mailDeliveryInfo = MailDeliveryInfo.builder()
                    .mail(mail);

            Set<ConstraintViolation<Mail>> validationResult = validator.validate(mail);

            if (validationResult.isEmpty()) {
                try {
                    mailProcessor.process(mail);
                    mailDeliveryInfo.mailDeliveryStatus(MailDeliveryStatus.DELIVERED);
                } catch (SendFailedException e) {
                    log.error("Invalid recipient", e);
                    mailDeliveryInfo.mailDeliveryStatus(MailDeliveryStatus.INVALID_RECIPIENT);
                } catch (MessagingException e) {
                    log.error("Failed to send message", e);
                    mailDeliveryInfo.mailDeliveryStatus(MailDeliveryStatus.COMMUNICATION_ERROR);
                } catch (Exception e) {
                    log.error("Unknown exception occurred while processing mail", e);
                    mailDeliveryInfo.mailDeliveryStatus(MailDeliveryStatus.UNKNOWN_ERROR);
                }
            } else {
                log.error("Invalid mail structure.");
                mailDeliveryInfo.constraintViolations(prepareConstraintViolations(validationResult));
                mailDeliveryInfo.mailDeliveryStatus(MailDeliveryStatus.VALIDATION_ERROR);
            }

            emitter.onNext(mailDeliveryInfo.build());
        });
    }

    private Map<String, String> prepareConstraintViolations(Set<ConstraintViolation<Mail>> validationResult) {
        return validationResult.stream()
                .collect(Collectors.toMap(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage));
    }
}
