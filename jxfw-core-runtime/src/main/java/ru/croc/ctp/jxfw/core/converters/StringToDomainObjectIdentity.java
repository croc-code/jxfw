package ru.croc.ctp.jxfw.core.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;

import java.util.StringTokenizer;


@Component
public class StringToDomainObjectIdentity implements Converter<String, DomainObjectIdentity> {

    @Override
    public DomainObjectIdentity convert(String domainIdentity) {

        StringTokenizer st = new StringTokenizer(domainIdentity, "(");
        String domainType = st.nextToken();
        String domainId = st.nextToken().substring(0, 36);


        return new DomainObjectIdentity<>(domainId, domainType);

    }
}
