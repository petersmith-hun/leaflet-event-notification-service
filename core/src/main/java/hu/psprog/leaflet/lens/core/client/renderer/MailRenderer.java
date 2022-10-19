package hu.psprog.leaflet.lens.core.client.renderer;

import hu.psprog.leaflet.lens.core.domain.Mail;

/**
 * Mail renderer interface.
 *
 * @author Peter Smith
 */
@FunctionalInterface
public interface MailRenderer {

    /**
     * Renders given {@link Mail} object and returns rendered mail content as String.
     *
     * @param mail {@link Mail} object to render
     * @return rendered Mail object as String
     */
    String renderMail(Mail mail);
}
