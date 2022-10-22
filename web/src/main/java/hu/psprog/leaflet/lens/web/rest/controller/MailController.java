package hu.psprog.leaflet.lens.web.rest.controller;

import hu.psprog.leaflet.lens.api.domain.MailContent;
import hu.psprog.leaflet.lens.api.domain.MailRequestWrapper;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.domain.MailType;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.service.MailService;
import hu.psprog.leaflet.lens.web.factory.MailRequestFactory;
import hu.psprog.leaflet.lens.web.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * REST controller implementation for mail operations.
 *
 * @author Peter Smith
 */
@RestController
@Slf4j
public class MailController {

    private final MailService mailService;
    private final MailRequestFactory mailRequestFactory;

    @Autowired
    public MailController(MailService mailService, MailRequestFactory mailRequestFactory) {
        this.mailService = mailService;
        this.mailRequestFactory = mailRequestFactory;
    }

    /**
     * POST /mail/{mailType}
     *
     * Requests sending a mail. The type of mail to be sent must be registered under {@code mail.registrations}.
     *
     * @param mailType type of the mail to be sent as {@link MailType}
     * @param mailRequestWrapper contents of the mail to be sent wrapped as {@link MailRequestWrapper} with a content of type {@link MailContent}
     * @return HTTP 202 Accepted on success
     */
    @PostMapping("/mail/{mailType}")
    public ResponseEntity<Void> sendMail(@PathVariable MailType mailType,
                                         @RequestBody MailRequestWrapper<? extends MailContent> mailRequestWrapper) {

        MailRequest mailRequest = mailRequestFactory.create(mailRequestWrapper, mailType);
        mailService.sendMail(mailRequest);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    /**
     * Handles the {@link MethodArgumentTypeMismatchException} that might be thrown upon parsing the {@link MailType} path variable.
     * Returns HTTP 404 Not Found in case of a failure of such.
     *
     * @param exception the original exception object
     * @return HTTP 404 Not Found
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<Void> handleUnknownMailRegistration(MethodArgumentTypeMismatchException exception) {

        log.error("Invalid mail type specified - possibly an unknown registration", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    /**
     * Handles mail validation failures by returning the validation error message along with HTTP 400 Bad Request status.
     *
     * @param exception the original exception object
     * @return HTTP 400 Bad Request with the validation error message as {@link ErrorResponse}
     */
    @ExceptionHandler(MailValidationException.class)
    ResponseEntity<ErrorResponse> handleValidationException(MailValidationException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    /**
     * Fallback exception handler for any other kind of exceptions, returning HTTP 500 Internal Server Error status.
     *
     * @param exception the original exception object
     * @return HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<Void> handleAnyOtherException(Exception exception) {

        log.error("Unexpected exception occurred", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
