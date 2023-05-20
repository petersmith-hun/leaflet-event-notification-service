package hu.psprog.leaflet.lens.web.rest.controller;

import hu.psprog.leaflet.lens.api.domain.MailRequestWrapper;
import hu.psprog.leaflet.lens.api.domain.SystemStartup;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.service.MailService;
import hu.psprog.leaflet.lens.web.factory.MailRequestFactory;
import hu.psprog.leaflet.lens.web.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MailController}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private MailService mailService;

    @Mock
    private MailRequestFactory mailRequestFactory;

    @InjectMocks
    private MailController mailController;

    @Test
    public void shouldSendMailReturnAcceptedStatusOnSuccess() {

        // given
        var requestWrapper = MailRequestWrapper.<SystemStartup>builder().build();
        MailType mailType = MailType.SYSTEM_STARTUP;
        MailRequest mailRequest = MailRequest.builder().mailType(mailType).build();

        given(mailRequestFactory.create(requestWrapper, mailType)).willReturn(mailRequest);

        // when
        ResponseEntity<Void> result = mailController.sendMail(mailType, requestWrapper);

        // then
        assertThat(result.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
        assertThat(result.getBody(), nullValue());

        verify(mailService).sendMail(mailRequest);
    }

    @Test
    public void shouldHandleUnknownMailRegistrationReturnNotFoundStatus() {

        // given
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("invalid", MailType.class, "mailType", null, null);

        // when
        ResponseEntity<Void> result = mailController.handleUnknownMailRegistration(exception);

        // then
        assertThat(result.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(result.getBody(), nullValue());
    }

    @Test
    public void shouldHandleValidationExceptionReturnBadRequestStatus() {

        // given
        String message = "Invalid recipient";
        MailValidationException exception = new MailValidationException(message);

        // when
        ResponseEntity<ErrorResponse> result = mailController.handleValidationException(exception);

        // then
        assertThat(result.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(result.getBody(), notNullValue());
        assertThat(result.getBody().message(), equalTo(message));
    }

    @Test
    public void shouldHandleAnyOtherExceptionReturnInternalServerErrorStatus() {

        // given
        String message = "Something went wrong";
        RuntimeException exception = new RuntimeException(message);

        // when
        ResponseEntity<Void> result = mailController.handleAnyOtherException(exception);

        // then
        assertThat(result.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(result.getBody(), nullValue());
    }
}
