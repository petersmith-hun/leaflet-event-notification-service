package hu.psprog.leaflet.lens.core.client.renderer.impl;

import hu.psprog.leaflet.lens.core.client.renderer.MailRenderer;
import hu.psprog.leaflet.lens.core.domain.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * {@link MailRenderer} implementation capable of Thymeleaf-template based mail rendering.
 *
 * @author Peter Smith
 */
@Component
public class ThymeleafMailRenderer implements MailRenderer {

    private final ITemplateEngine templateEngine;

    @Autowired
    public ThymeleafMailRenderer(@Qualifier("emailTemplateEngine") ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String renderMail(Mail mail) {

        Context context = new Context();
        context.setVariables(mail.getContentMap());

        return templateEngine.process(mail.getTemplate(), context);
    }
}
