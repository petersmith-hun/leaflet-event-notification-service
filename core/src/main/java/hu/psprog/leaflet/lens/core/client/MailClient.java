package hu.psprog.leaflet.lens.core.client;

import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import io.reactivex.Observable;

/**
 * Mail sender client for Leaflet backend service.
 * Validates and sends given {@link Mail} object.
 * This client provides reactive endpoint for mail sending.
 *
 * @author Peter Smith
 */
@FunctionalInterface
public interface MailClient {

    /**
     * Sends given {@link Mail} object.
     * {@link Observable} return provides reactive approach for processing.
     *
     * @param mail mail object to be sent
     * @return delivery information as {@link Observable}.
     */
    Observable<MailDeliveryInfo> sendMail(Mail mail);
}
