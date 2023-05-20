package hu.psprog.leaflet.lens.web.model;

/**
 * Model class for returning a simple error message upon exception.
 *
 * @author Peter Smith
 */
public record ErrorResponse(
        String message
) { }
