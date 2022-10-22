package hu.psprog.leaflet.lens.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model class for returning a simple error message upon exception.
 *
 * @author Peter Smith
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private final String message;
}
