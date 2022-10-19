package hu.psprog.leaflet.lens.core.factory.impl;

import hu.psprog.leaflet.lens.core.config.MailProcessorConfigurationProperties;
import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import hu.psprog.leaflet.lens.core.exception.MailException;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.validator.MailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MailFactoryImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailFactoryImplTest {

    private static final MailType MAIL_TYPE = MailType.CONTACT_REQUEST;
    private static final String CUSTOM_RECIPIENT = "user1@dev.local";
    private static final String DEFAULT_RECIPIENT = "admin@dev.local";
    private static final String REPLY_TO = "replyto@dev.local";
    private static final String SUBJECT_KEY_OVERRIDE = "subject-key-override";
    private static final String DEFAULT_SUBJECT_KEY = "default-subject-key";
    private static final String SUBJECT_TRANSLATED = "subject_translated";
    private static final String FORMATTED_DATE = "2022-10-19";
    private static final String TEMPLATE = "contact-request-template";
    private static final Locale FORCED_LOCALE = Locale.UK;
    private static final SimpleDateFormat MAIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private MessageSource messageSource;

    @Mock
    private MailValidator mailValidator1;

    @Mock
    private MailValidator mailValidator2;

    @Mock
    private MailProcessorConfigurationProperties mailProcessorConfigurationProperties;

    @Mock
    private Map<MailType, MailRegistration> mailRegistrationMap;

    @Mock
    private MailRegistration mailRegistration;

    private MailFactoryImpl mailFactory;

    @BeforeEach
    public void setup() {
        mailFactory = new MailFactoryImpl(messageSource, MAIL_DATE_FORMAT, List.of(mailValidator1, mailValidator2), mailProcessorConfigurationProperties);
        mailFactory.setForcedLocale(FORCED_LOCALE);
    }

    @Test
    public void shouldBuildMailSuccessfullyExecuteWithOneRecipientAndReplyToAndSubjectOverride() {

        // given
        MailRequest mailRequest = prepareMailRequest(true, true, true);

        given(mailProcessorConfigurationProperties.getRegistrations()).willReturn(mailRegistrationMap);
        given(mailRegistrationMap.get(MAIL_TYPE)).willReturn(mailRegistration);
        given(mailRegistration.getTemplateName()).willReturn(TEMPLATE);
        given(messageSource.getMessage(SUBJECT_KEY_OVERRIDE, null, SUBJECT_KEY_OVERRIDE, FORCED_LOCALE)).willReturn(SUBJECT_TRANSLATED);

        // when
        Mail mail = mailFactory.buildMail(mailRequest);

        // then
        assertThat(mail.getRecipients(), equalTo(List.of(CUSTOM_RECIPIENT)));
        assertThat(mail.getSubject(), equalTo(SUBJECT_TRANSLATED));
        assertThat(mail.getReplyTo(), equalTo(REPLY_TO));
        assertThat(mail.getTemplate(), equalTo(TEMPLATE));
        assertThat(mail.getContentMap(), equalTo(Map.of(
                "field1", "value1",
                "field2", "value2",
                "generatedAt", FORMATTED_DATE
        )));

        verify(mailValidator1).validate(mailRequest, mailRegistration);
        verify(mailValidator2).validate(mailRequest, mailRegistration);
        verify(mailProcessorConfigurationProperties, never()).getAdminNotificationAddress();
    }

    @Test
    public void shouldBuildMailSuccessfullyExecuteWithoutRecipientAndNoReplyToAndDefaultSubject() {

        // given
        MailRequest mailRequest = prepareMailRequest(false, false, false);

        given(mailProcessorConfigurationProperties.getRegistrations()).willReturn(mailRegistrationMap);
        given(mailProcessorConfigurationProperties.getAdminNotificationAddress()).willReturn(DEFAULT_RECIPIENT);
        given(mailRegistrationMap.get(MAIL_TYPE)).willReturn(mailRegistration);
        given(mailRegistration.getTemplateName()).willReturn(TEMPLATE);
        given(mailRegistration.getDefaultSubjectKey()).willReturn(DEFAULT_SUBJECT_KEY);
        given(messageSource.getMessage(DEFAULT_SUBJECT_KEY, null, DEFAULT_SUBJECT_KEY, FORCED_LOCALE)).willReturn(SUBJECT_TRANSLATED);

        // when
        Mail mail = mailFactory.buildMail(mailRequest);

        // then
        assertThat(mail.getRecipients(), equalTo(List.of(DEFAULT_RECIPIENT)));
        assertThat(mail.getSubject(), equalTo(SUBJECT_TRANSLATED));
        assertThat(mail.getReplyTo(), nullValue());
        assertThat(mail.getTemplate(), equalTo(TEMPLATE));
        assertThat(mail.getContentMap(), equalTo(Map.of(
                "field1", "value1",
                "field2", "value2",
                "generatedAt", FORMATTED_DATE
        )));

        verify(mailValidator1).validate(mailRequest, mailRegistration);
        verify(mailValidator2).validate(mailRequest, mailRegistration);
        verify(mailProcessorConfigurationProperties).getAdminNotificationAddress();
    }

    @Test
    public void shouldBuildMailThrowExceptionForNonRegisteredMailType() {

        // given
        MailRequest mailRequest = prepareMailRequest(false, false, false);

        given(mailProcessorConfigurationProperties.getRegistrations()).willReturn(mailRegistrationMap);
        given(mailRegistrationMap.get(MAIL_TYPE)).willReturn(null);

        // when
        assertThrows(MailException.class, () -> mailFactory.buildMail(mailRequest));

        // then
        // exception expected
    }

    @Test
    public void shouldBuildMailThrowExceptionOnValidationFailure() {

        // given
        MailRequest mailRequest = prepareMailRequest(false, false, false);

        given(mailProcessorConfigurationProperties.getRegistrations()).willReturn(mailRegistrationMap);
        given(mailRegistrationMap.get(MAIL_TYPE)).willReturn(mailRegistration);
        doThrow(MailValidationException.class).when(mailValidator2).validate(mailRequest, mailRegistration);

        // when
        assertThrows(MailValidationException.class, () -> mailFactory.buildMail(mailRequest));

        // then
        // exception expected
    }

    private static MailRequest prepareMailRequest(boolean withRecipient, boolean withReplyTo, boolean withSubjectOverride) {

        return MailRequest.builder()
                .mailType(MAIL_TYPE)
                .recipients(withRecipient
                        ? List.of(CUSTOM_RECIPIENT)
                        : null)
                .replyTo(withReplyTo
                        ? REPLY_TO
                        : null)
                .overrideSubjectKey(withSubjectOverride
                        ? SUBJECT_KEY_OVERRIDE
                        : null)
                .contentMap(Map.of(
                        "field1", "value1",
                        "field2", "value2"
                ))
                .build();
    }
}
