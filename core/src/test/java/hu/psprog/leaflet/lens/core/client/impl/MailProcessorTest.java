package hu.psprog.leaflet.lens.core.client.impl;

import hu.psprog.leaflet.lens.core.client.renderer.MailRenderer;
import hu.psprog.leaflet.lens.core.config.MailProcessorConfigurationProperties;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.ReflectionUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link MailProcessor}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailProcessorTest {

    private static final String EXACT_RECIPIENT = "test@dev.lflt";
    private static final String RENDERED_MAIL_CONTENT = "Rendered mail";
    private static final String SENDER_ADDRESS = "sender-address@local.dev";
    private static final String SENDER_NAME = "Test Sender";
    private static final String FIELD_SENDER = "sender";
    private static final InternetAddress FROM_ADDRESS = prepareSender();
    private static final String REPLY_TO_ADDRESS = "test@dev.local";
    private static final String SUBJECT = "Test";
    private static final String CONTENT_TYPE_HTML = "text/html";

    @Mock
    private MailRenderer mailRenderer;

    @Mock
    private MailProcessorConfigurationProperties mailProcessorConfigurationProperties;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MailProcessor mailProcessor;

    private Mail mail;

    @Test
    public void shouldSelectMailRendererAndPrepareSender() throws NoSuchFieldException {

        // given
        given(mailProcessorConfigurationProperties.getSenderAddress()).willReturn(SENDER_ADDRESS);
        given(mailProcessorConfigurationProperties.getSenderName()).willReturn(SENDER_NAME);

        // when
        mailProcessor.initialize();

        // then
        assertThat(getSenderField(), equalTo(FROM_ADDRESS));
    }

    @Test
    public void shouldProcessMailWithGivenRecipient() throws NoSuchFieldException, MessagingException {

        // given
        prepareMail(true);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        given(mailRenderer.renderMail(mail)).willReturn(RENDERED_MAIL_CONTENT);
        prepareSenderField();

        // when
        mailProcessor.process(mail);

        // then
        verify(mailRenderer).renderMail(mail);
        verify(mailProcessorConfigurationProperties, never()).getAdminNotificationAddress();
        verify(mimeMessage).setRecipients(Message.RecipientType.TO, new Address[]{getAddressToCheck(EXACT_RECIPIENT)});
        verify(mimeMessage).setFrom(FROM_ADDRESS);
        verify(javaMailSender).send(mimeMessage);
        verify(mimeMessage).setSubject(SUBJECT);
        verify(mimeMessage).setContent(RENDERED_MAIL_CONTENT, CONTENT_TYPE_HTML);
        verifyNoMoreInteractions(mimeMessage);
    }

    @Test
    public void shouldProcessMailWithReplyToAddress() throws NoSuchFieldException, MessagingException {

        // given
        prepareMail(true, true);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        given(mailRenderer.renderMail(mail)).willReturn(RENDERED_MAIL_CONTENT);
        prepareSenderField();

        // when
        mailProcessor.process(mail);

        // then
        verify(mailRenderer).renderMail(mail);
        verify(mailProcessorConfigurationProperties, never()).getAdminNotificationAddress();
        verify(mimeMessage).setRecipients(Message.RecipientType.TO, new Address[]{getAddressToCheck(EXACT_RECIPIENT)});
        verify(mimeMessage).setFrom(FROM_ADDRESS);
        verify(javaMailSender).send(mimeMessage);
        verify(mimeMessage).setReplyTo(new Address[] {getAddressToCheck(REPLY_TO_ADDRESS)});
        verify(mimeMessage).setSubject(SUBJECT);
        verify(mimeMessage).setContent(RENDERED_MAIL_CONTENT, CONTENT_TYPE_HTML);
        verifyNoMoreInteractions(mimeMessage);
    }

    @Test
    public void shouldProcessMailThrowExceptionOnMissingRecipient() throws NoSuchFieldException {

        // given
        prepareMail(false);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        prepareSenderField();

        // when
        assertThrows(MailValidationException.class, () -> mailProcessor.process(mail));

        // then
        verify(mailRenderer, never()).renderMail(any(Mail.class));
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    private static InternetAddress prepareSender() {

        InternetAddress address = null;
        try {
            address = new InternetAddress(SENDER_ADDRESS, SENDER_NAME);
        } catch (UnsupportedEncodingException e) {
            fail("Failed to prepare sender address.");
        }

        return address;
    }

    private void prepareMail(boolean exactRecipient) {
        prepareMail(exactRecipient, false);
    }

    private void prepareMail(boolean exactRecipient, boolean withReplyTo) {
        mail = Mail.builder()
                .recipients(exactRecipient
                        ? List.of(EXACT_RECIPIENT)
                        : Collections.emptyList())
                .subject(SUBJECT)
                .replyTo(withReplyTo
                        ? REPLY_TO_ADDRESS
                        : null)
                .build();
    }

    private Address getAddressToCheck(String recipient) throws AddressException {
        return InternetAddress.parse(recipient)[0];
    }

    private InternetAddress getSenderField() throws NoSuchFieldException {
        return (InternetAddress) ReflectionUtils.getField(accessSenderField(), mailProcessor);
    }

    private void prepareSenderField() throws NoSuchFieldException {
        ReflectionUtils.setField(accessSenderField(), mailProcessor, FROM_ADDRESS);
    }

    private Field accessSenderField() throws NoSuchFieldException {

        Field field = MailProcessor.class.getDeclaredField(FIELD_SENDER);
        field.setAccessible(true);

        return field;
    }
}
