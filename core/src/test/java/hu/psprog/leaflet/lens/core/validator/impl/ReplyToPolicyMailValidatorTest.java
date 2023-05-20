package hu.psprog.leaflet.lens.core.validator.impl;

import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link ReplyToPolicyMailValidator}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class ReplyToPolicyMailValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private MailRequest mailRequest;

    @Mock
    private MailRegistration mailRegistration;

    @InjectMocks
    private ReplyToPolicyMailValidator replyToPolicyMailValidator;

    @Test
    public void shouldValidatePassSilentlyIfReplyToIsAllowedAndSpecified() {

        // given
        prepareMocks(true, true);

        // when
        replyToPolicyMailValidator.validate(mailRequest, mailRegistration);

        // then
        // pass silently
    }

    @Test
    public void shouldValidatePassSilentlyIfReplyToIsAllowedAndNotSpecified() {

        // given
        prepareMocks(true, false);

        // when
        replyToPolicyMailValidator.validate(mailRequest, mailRegistration);

        // then
        // pass silently
    }

    @Test
    public void shouldValidatePassSilentlyIfReplyToIsNotAllowedAndNotSpecified() {

        // given
        prepareMocks(false, false);

        // when
        replyToPolicyMailValidator.validate(mailRequest, mailRegistration);

        // then
        // pass silently
    }

    @Test
    public void shouldValidateThrowValidationExceptionIfReplyToIsNotAllowedSpecified() {

        // given
        prepareMocks(false, true);

        // when
        assertThrows(MailValidationException.class, () -> replyToPolicyMailValidator.validate(mailRequest, mailRegistration));

        // then
        // exception expected
    }

    private void prepareMocks(boolean allowReplyTo, boolean withReplyTo) {

        given(mailRegistration.isReplyToAllowed()).willReturn(allowReplyTo);
        given(mailRequest.getReplyTo()).willReturn(withReplyTo
                ? "replyto@dev.local"
                : null);
    }
}
