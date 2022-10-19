package hu.psprog.leaflet.lens.core.service;

import hu.psprog.leaflet.lens.core.domain.MailRequest;

/**
 * Interface for sending mails.
 *
 * @author Peter Smith
 */
public interface MailService {

    /**
     * Sends a mail based on the given {@link MailRequest} object.
     *
     * @param mailRequest {@link MailRequest} object containing request data for the mail to be sent
     */
    void sendMail(MailRequest mailRequest);
}
