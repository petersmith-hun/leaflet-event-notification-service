package hu.psprog.leaflet.lens.web.factory;

import hu.psprog.leaflet.lens.api.domain.MailContent;
import hu.psprog.leaflet.lens.api.domain.MailRequestWrapper;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import org.springframework.stereotype.Component;

/**
 * Factory implementation for creating {@link MailRequest} objects from the incoming mail requests.
 *
 * @author Peter Smith
 */
@Component
public class MailRequestFactory {

    /**
     * Converts a {@link MailRequestWrapper} object to a {@link MailRequest} and adds {@link MailType} for the generated object.
     *
     * @param mailRequestWrapper {@link MailRequestWrapper} wrapper object of the incoming mail request
     * @param mailType the registered type of the mail to be sent as {@link MailType} enum constant
     * @return converted {@link MailRequest} object
     */
    public MailRequest create(MailRequestWrapper<? extends MailContent> mailRequestWrapper, MailType mailType) {

        return MailRequest.builder()
                .mailType(mailType)
                .recipients(mailRequestWrapper.recipients())
                .replyTo(mailRequestWrapper.replyTo())
                .overrideSubjectKey(mailRequestWrapper.overrideSubjectKey())
                .contentMap(mailRequestWrapper.content().asContentMap())
                .build();
    }
}
