package hu.psprog.leaflet.lens.core.factory;

import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailRequest;

/**
 * Implementation of this interface is able to properly generate a {@link Mail} from an incoming {@link MailRequest}.
 * The generated {@link Mail} instance can be used for further processing and eventually sending out the mail.
 *
 * @author Peter Smith
 */
public interface MailFactory {

    /**
     * Builds a {@link Mail} object based on the given {@link MailRequest}.
     *
     * @param mailRequest {@link MailRequest} instance containing the necessary information about the mail to be sent
     * @return generated {@link Mail} object for further processing
     */
    Mail buildMail(MailRequest mailRequest);
}
