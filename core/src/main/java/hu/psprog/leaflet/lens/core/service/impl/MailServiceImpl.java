package hu.psprog.leaflet.lens.core.service.impl;

import hu.psprog.leaflet.lens.core.client.MailClient;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailException;
import hu.psprog.leaflet.lens.core.factory.MailFactory;
import hu.psprog.leaflet.lens.core.observer.ObserverHandler;
import hu.psprog.leaflet.lens.core.service.MailService;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link MailService}.
 * This implementation does the following steps:
 *  1) Builds a {@link Mail} object from a {@link MailRequest} object by calling a {@link MailFactory}.
 *  2) Sends the generated {@link Mail} via the {@link MailClient}.
 *  3) Attaches an observer to the sent {@link Mail}.
 *
 * @author Peter Smith
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private final MailFactory mailFactory;
    private final MailClient mailClient;
    private final ObserverHandler<MailDeliveryInfo> mailDeliveryInfoObserverHandler;

    @Autowired
    public MailServiceImpl(MailFactory mailFactory, MailClient mailClient,
                           ObserverHandler<MailDeliveryInfo> mailDeliveryInfoObserverHandler) {
        this.mailFactory = mailFactory;
        this.mailClient = mailClient;
        this.mailDeliveryInfoObserverHandler = mailDeliveryInfoObserverHandler;
    }

    @Override
    public void sendMail(MailRequest mailRequest) {

        try {
            Mail mail = mailFactory.buildMail(mailRequest);
            Observable<MailDeliveryInfo> mailDeliveryInfoObservable = mailClient.sendMail(mail);
            mailDeliveryInfoObserverHandler.attachObserver(mailDeliveryInfoObservable);

        } catch (Exception exception) {

            log.error("Failed to send mail", exception);
            throw new MailException(String.format("Failed to send mail of type [%s]", mailRequest.getMailType()));
        }
    }
}
