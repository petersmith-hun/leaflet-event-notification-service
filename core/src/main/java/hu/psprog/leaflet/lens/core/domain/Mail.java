package hu.psprog.leaflet.lens.core.domain;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * Mail object.
 *
 * @author Peter Smith
 */
@Data
@Builder
public class Mail {

    @NotEmpty
    private final List<String> recipients;

    @NotEmpty
    private final String subject;

    @NotEmpty
    private final String template;

    private final String replyTo;
    private final Map<String, Object> contentMap;
}
