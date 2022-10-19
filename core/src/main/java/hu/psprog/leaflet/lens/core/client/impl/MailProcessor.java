package hu.psprog.leaflet.lens.core.client.impl;

import hu.psprog.leaflet.lens.core.client.renderer.MailRenderer;
import hu.psprog.leaflet.lens.core.config.MailProcessorConfigurationProperties;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.exception.MailException;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Processes and sends mails.
 *
 * @author Peter Smith
 */
@Component
@Slf4j
class MailProcessor {

    private final MailProcessorConfigurationProperties mailProcessorConfigurationProperties;
    private final JavaMailSender javaMailSender;
    private final MailRenderer mailRenderer;

    private Address sender;

    @Autowired
    public MailProcessor(MailProcessorConfigurationProperties mailProcessorConfigurationProperties,
                         JavaMailSender javaMailSender, MailRenderer mailRenderer) {
        this.mailProcessorConfigurationProperties = mailProcessorConfigurationProperties;
        this.javaMailSender = javaMailSender;
        this.mailRenderer = mailRenderer;
    }

    @PostConstruct
    public void initialize() {
        prepareSender();
    }

    /**
     * Processes given {@link Mail} object by transforming it into a {@link MimeMessage}.
     * Implementation uses {@link JavaMailSender} to prepare and send the mail.
     *
     * @param mail {@link Mail} object
     * @throws MessagingException if an error occurred during sending the mail
     */
    public void process(Mail mail) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(sender);
        prepareMessage(mail, message);

        javaMailSender.send(message);
    }

    private void prepareSender() {

        try {
            sender = new InternetAddress(mailProcessorConfigurationProperties.getSenderAddress(), mailProcessorConfigurationProperties.getSenderName());
        } catch (UnsupportedEncodingException e) {
            log.error("Could not prepare sender with mail configuration {}.", mailProcessorConfigurationProperties, e);
            throw new MailException("Could not prepare sender - invalid mail configuration provided.", e);
        }
    }

    private void prepareMessage(Mail mail, MimeMessage message) throws MessagingException {

        if (mail.getRecipients().isEmpty()) {
            throw new MailValidationException("At least one recipient must be present");
        }

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        mimeMessageHelper.setTo(getRecipients(mail));
        mimeMessageHelper.setSubject(mail.getSubject());
        mimeMessageHelper.setText(mailRenderer.renderMail(mail), true);

        if (Objects.nonNull(mail.getReplyTo())) {
            mimeMessageHelper.setReplyTo(mail.getReplyTo());
        }
    }

    private String[] getRecipients(Mail mail) {
        return mail.getRecipients().toArray(String[]::new);
    }
}
