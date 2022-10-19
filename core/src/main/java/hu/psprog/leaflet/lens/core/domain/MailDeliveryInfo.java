package hu.psprog.leaflet.lens.core.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Mail delivery information.
 *
 * @author Peter Smith
 */
@Data
@Builder
public class MailDeliveryInfo {

    private final Mail mail;
    private final MailDeliveryStatus mailDeliveryStatus;
    private final Map<String, String> constraintViolations;
}
