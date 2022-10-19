package hu.psprog.leaflet.lens.core.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Internally used domain class for processing mail requests.
 *
 * @author Peter Smith
 */
@Data
@Builder
public class MailRequest {

    private final MailType mailType;
    private final List<String> recipients;
    private final String replyTo;
    private final String overrideSubjectKey;
    private final Map<String, Object> contentMap;

    public List<String> getRecipients() {

        return Optional.ofNullable(recipients)
                .orElseGet(Collections::emptyList);
    }
}
