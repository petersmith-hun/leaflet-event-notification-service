package hu.psprog.leaflet.lens.core.service.impl;

import hu.psprog.leaflet.lens.core.client.MailClient;
import hu.psprog.leaflet.lens.core.domain.Mail;
import hu.psprog.leaflet.lens.core.domain.MailDeliveryInfo;
import hu.psprog.leaflet.lens.core.domain.MailRequest;
import hu.psprog.leaflet.lens.core.exception.MailException;
import hu.psprog.leaflet.lens.core.exception.MailValidationException;
import hu.psprog.leaflet.lens.core.factory.MailFactory;
import hu.psprog.leaflet.lens.core.observer.ObserverHandler;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MailServiceImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private MailFactory mailFactory;

    @Mock
    private MailClient mailClient;

    @Mock
    private ObserverHandler<MailDeliveryInfo> mailDeliveryInfoObserverHandler;

    @Mock
    private MailRequest mailRequest;

    @Mock
    private Mail mail;

    @Mock
    private MailDeliveryInfo mailDeliveryInfo;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    public void shouldSendMailCreateTheMailThenSendAndAttachObserverToIt() {

        // given
        Observable<MailDeliveryInfo> mailDeliveryInfoObservable = Observable.just(mailDeliveryInfo);

        given(mailFactory.buildMail(mailRequest)).willReturn(mail);
        given(mailClient.sendMail(mail)).willReturn(mailDeliveryInfoObservable);

        // when
        mailService.sendMail(mailRequest);

        // then
        verify(mailDeliveryInfoObserverHandler).attachObserver(mailDeliveryInfoObservable);
    }

    @Test
    public void shouldSendMailReThrowMailValidationException() {

        // given
        doThrow(MailValidationException.class).when(mailFactory).buildMail(mailRequest);

        // when
        assertThrows(MailValidationException.class, () -> mailService.sendMail(mailRequest));

        // then
        // exception expected
    }

    @Test
    public void shouldSendMailThrowMailExceptionOnAnyUnhandledError() {

        // given
        given(mailFactory.buildMail(mailRequest)).willReturn(mail);
        doThrow(RuntimeException.class).when(mailClient).sendMail(mail);

        // when
        assertThrows(MailException.class, () -> mailService.sendMail(mailRequest));

        // then
        // exception expected
    }
}
