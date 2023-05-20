package hu.psprog.leaflet.lens.web.factory;

import hu.psprog.leaflet.lens.api.domain.ContactRequest;
import hu.psprog.leaflet.lens.api.domain.MailRequestWrapper;
import hu.psprog.leaflet.lens.api.domain.SystemStartup;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for {@link MailRequestFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailRequestFactoryTest {

    private static final String[] RECIPIENTS = {"admin@dev.local"};
    private static final String REPLY_TO = "replyto@dev.local";
    private static final String OVERRIDE_SUBJECT_KEY = "overridden-subject-key";
    private static final String USERNAME = "User 1";
    private static final String MESSAGE = "This is a message";
    private static final String APPLICATION_NAME = "app1";
    private static final String VERSION = "1.0.0";
    private static final MailType CONTACT_REQUEST = MailType.CONTACT_REQUEST;
    private static final MailType SYSTEM_STARTUP = MailType.SYSTEM_STARTUP;

    @InjectMocks
    private MailRequestFactory mailRequestFactory;

    @Test
    public void shouldCreateMailRequestScenario1() {

        // given
        var requestWrapper = MailRequestWrapper.<ContactRequest>builder()
                .recipients(RECIPIENTS)
                .replyTo(REPLY_TO)
                .overrideSubjectKey(OVERRIDE_SUBJECT_KEY)
                .content(ContactRequest.builder()
                        .name(USERNAME)
                        .email(REPLY_TO)
                        .message(MESSAGE)
                        .build())
                .build();

        MailRequest expectedMailRequest = MailRequest.builder()
                .mailType(CONTACT_REQUEST)
                .recipients(List.of(RECIPIENTS))
                .replyTo(REPLY_TO)
                .overrideSubjectKey(OVERRIDE_SUBJECT_KEY)
                .contentMap(Map.of(
                        "name", USERNAME,
                        "email", REPLY_TO,
                        "message", MESSAGE
                ))
                .build();

        // when
        MailRequest result = mailRequestFactory.create(requestWrapper, CONTACT_REQUEST);

        // then
        assertThat(result, equalTo(expectedMailRequest));
    }

    @Test
    public void shouldCreateMailRequestScenario2() {

        // given
        var requestWrapper = MailRequestWrapper.<SystemStartup>builder()
                .content(SystemStartup.builder()
                        .applicationName(APPLICATION_NAME)
                        .version(VERSION)
                        .build())
                .build();

        MailRequest expectedMailRequest = MailRequest.builder()
                .mailType(SYSTEM_STARTUP)
                .recipients(null)
                .replyTo(null)
                .overrideSubjectKey(null)
                .contentMap(Map.of(
                        "applicationName", APPLICATION_NAME,
                        "version", VERSION
                ))
                .build();

        // when
        MailRequest result = mailRequestFactory.create(requestWrapper, SYSTEM_STARTUP);

        // then
        assertThat(result, equalTo(expectedMailRequest));
        assertThat(result.getRecipients(), equalTo(Collections.emptyList()));
    }
}
