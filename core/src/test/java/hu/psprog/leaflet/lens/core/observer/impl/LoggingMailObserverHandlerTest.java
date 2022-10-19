package hu.psprog.leaflet.lens.core.observer.impl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryStatus;
import io.reactivex.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LoggingMailObserverHandler}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class LoggingMailObserverHandlerTest {

    @Mock
    private Appender<ILoggingEvent> appender;

    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventArgumentCaptor;

    @InjectMocks
    private LoggingMailObserverHandler loggingMailObserverHandler;

    @BeforeEach
    public void setup() {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(appender);
    }

    @Test
    public void shouldAttachObserverWithoutConstraintViolation() {

        // given
        MailDeliveryInfo mailDeliveryInfo = prepareMailDeliveryInfo(MailDeliveryStatus.DELIVERED, false, "test@dev.local");

        // when
        call(mailDeliveryInfo);

        // then
        assertLogMessage("Mail delivery status: [DELIVERED] 'Test mail' -> [test@dev.local] (0 constraint violations)");
    }

    @Test
    public void shouldAttachObserverWithConstraintViolation() {

        // given
        MailDeliveryInfo mailDeliveryInfo = prepareMailDeliveryInfo(MailDeliveryStatus.INVALID_RECIPIENT, true, "admin@dev.local", "invalid@dev.local");

        // when
        call(mailDeliveryInfo);

        // then
        assertLogMessage("Mail delivery status: [INVALID_RECIPIENT] 'Test mail' -> [admin@dev.local, invalid@dev.local] (1 constraint violations)");
    }

    private void assertLogMessage(String expectedMessage) {

        verify(appender, atLeastOnce()).doAppend(loggingEventArgumentCaptor.capture());
        assertThat(loggingEventArgumentCaptor.getAllValues().stream()
                .map(LoggingEvent::getMessage)
                .anyMatch(expectedMessage::equals), is(true));
    }

    private void call(MailDeliveryInfo mailDeliveryInfo) {

        Observable<MailDeliveryInfo> observable = Observable.just(mailDeliveryInfo);
        loggingMailObserverHandler.attachObserver(observable);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Assertions.fail("Interrupted");
        }
    }

    private MailDeliveryInfo prepareMailDeliveryInfo(MailDeliveryStatus status, boolean withConstraintViolations, String... recipients) {

        return MailDeliveryInfo.builder()
                .mailDeliveryStatus(status)
                .constraintViolations(withConstraintViolations
                        ? prepareConstraintViolations()
                        : null)
                .mail(Mail.builder()
                        .subject("Test mail")
                        .recipients(Arrays.asList(recipients))
                        .build())
                .build();
    }

    private Map<String, String> prepareConstraintViolations() {
        return Map.of("field", "error");
    }
}
