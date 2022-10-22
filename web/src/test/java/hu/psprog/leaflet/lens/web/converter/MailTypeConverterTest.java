package hu.psprog.leaflet.lens.web.converter;

import hu.psprog.leaflet.lens.core.domain.MailType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for {@link MailTypeConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class MailTypeConverterTest {

    @InjectMocks
    private MailTypeConverter mailTypeConverter;

    @ParameterizedTest
    @MethodSource("mailTypeParameterSource")
    public void shouldConvertParameterToMailType(String parameter, MailType expectedMailType) {

        // when
        MailType result = mailTypeConverter.convert(parameter);

        // then
        assertThat(result, equalTo(expectedMailType));
    }

    private static Stream<Arguments> mailTypeParameterSource() {

        return Stream.of(MailType.values())
                .flatMap(mailType -> Stream.of(
                        Arguments.of(mailType.name().toLowerCase(), mailType),
                        Arguments.of(mailType.name(), mailType)
                ));
    }
}
