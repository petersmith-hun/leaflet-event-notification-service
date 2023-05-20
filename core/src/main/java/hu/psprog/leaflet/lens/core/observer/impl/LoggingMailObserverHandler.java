package hu.psprog.leaflet.lens.core.observer.impl;

import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import hu.psprog.leaflet.lens.core.observer.ObserverHandler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Logging-only observer for {@link MailDeliveryInfo}-returning observables.
 *
 * @author Peter Smith
 */
@Component
@Slf4j
public class LoggingMailObserverHandler implements ObserverHandler<MailDeliveryInfo> {

    private static final String MAIL_DELIVERY_INFO_MESSAGE_PATTERN = "Mail delivery status: [%s] '%s' -> %s (%d constraint violations)";

    private final Map<String, Disposable> disposableMap = new HashMap<>();

    @Override
    public void attachObserver(Observable<MailDeliveryInfo> observable) {

        String subscriptionID = UUID.randomUUID().toString();
        Disposable subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(mailDeliveryInfoConsumer(subscriptionID));

        disposableMap.put(subscriptionID, subscription);
    }

    private Consumer<MailDeliveryInfo> mailDeliveryInfoConsumer(String subscriptionID) {

        return mailDeliveryInfo -> {

            log.info(createLogMessage(mailDeliveryInfo));

            if (disposableMap.containsKey(subscriptionID)) {
                disposableMap.remove(subscriptionID).dispose();
            }
        };
    }

    private String createLogMessage(MailDeliveryInfo mailDeliveryInfo) {

        return String.format(MAIL_DELIVERY_INFO_MESSAGE_PATTERN,
                mailDeliveryInfo.getMailDeliveryStatus(),
                mailDeliveryInfo.getMail().getSubject(),
                mailDeliveryInfo.getMail().getRecipients(),
                extractNumberOfConstraintViolations(mailDeliveryInfo.getConstraintViolations()));
    }

    private int extractNumberOfConstraintViolations(Map<String, String> constraintViolations) {

        return Optional.ofNullable(constraintViolations)
                .map(Map::size)
                .orElse(0);
    }
}
