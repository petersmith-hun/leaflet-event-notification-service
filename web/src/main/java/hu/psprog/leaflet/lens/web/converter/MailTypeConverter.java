package hu.psprog.leaflet.lens.web.converter;

import hu.psprog.leaflet.lens.core.domain.MailType;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts the given {@link String} parameter to {@link MailType}.
 * Implementation is case-insensitive.
 *
 * @author Peter Smith
 */
public class MailTypeConverter implements Converter<String, MailType> {

    @Override
    public MailType convert(String source) {
        return MailType.valueOf(source.toUpperCase());
    }
}
