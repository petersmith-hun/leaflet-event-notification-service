package hu.psprog.leaflet.lens.core.validator.impl;

import hu.psprog.leaflet.lens.core.config.MailRegistration;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link RecipientPolicyMailValidator}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class RecipientPolicyMailValidatorTest {

    @Mock
    private MailRequest mailRequest;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private List<String> recipientList;

    @Mock
    private MailRegistration mailRegistration;

    @InjectMocks
    private RecipientPolicyMailValidator recipientPolicyMailValidator;

    @ParameterizedTest
    @MethodSource("validScenarios")
    public void shouldValidatePassSilentlyForValidCases(MailRegistration.RecipientPolicy recipientPolicy, int numberOfRecipients) {

        // given
        prepareMocks(recipientPolicy, numberOfRecipients);

        // when
        recipientPolicyMailValidator.validate(mailRequest, mailRegistration);

        // then
        // pass silently
    }

    @ParameterizedTest
    @MethodSource("invalidScenarios")
    public void shouldValidateThrowValidationExceptionForInvalidCases(MailRegistration.RecipientPolicy recipientPolicy, int numberOfRecipients) {

        // given
        prepareMocks(recipientPolicy, numberOfRecipients);

        // when
        assertThrows(MailValidationException.class, () -> recipientPolicyMailValidator.validate(mailRequest, mailRegistration));

        // then
        // exception expected
    }

    private void prepareMocks(MailRegistration.RecipientPolicy recipientPolicy, int numberOfRecipients) {

        given(mailRegistration.getRecipientPolicy()).willReturn(recipientPolicy);
        given(mailRequest.getRecipients()).willReturn(recipientList);
        given(recipientList.size()).willReturn(numberOfRecipients);
        given(recipientList.isEmpty()).willReturn(numberOfRecipients == 0);
    }

    private static Stream<Arguments> validScenarios() {

        return Stream.of(
                Arguments.of(MailRegistration.RecipientPolicy.NONE, 0),
                Arguments.of(MailRegistration.RecipientPolicy.SINGLE, 1),
                Arguments.of(MailRegistration.RecipientPolicy.MULTIPLE, 1),
                Arguments.of(MailRegistration.RecipientPolicy.MULTIPLE, 3)
        );
    }

    private static Stream<Arguments> invalidScenarios() {

        return Stream.of(
                Arguments.of(MailRegistration.RecipientPolicy.NONE, 1),
                Arguments.of(MailRegistration.RecipientPolicy.SINGLE, 0),
                Arguments.of(MailRegistration.RecipientPolicy.SINGLE, 2),
                Arguments.of(MailRegistration.RecipientPolicy.MULTIPLE, 0)
        );
    }
}
