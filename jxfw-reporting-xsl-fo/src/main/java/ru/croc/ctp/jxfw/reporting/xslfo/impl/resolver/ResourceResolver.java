package ru.croc.ctp.jxfw.reporting.xslfo.impl.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.IOException;


/**
 * Ресолвер импортов xsd {@link LSResourceResolver}.
 *
 * @author SMufazzalov
 * @since 1.6
 */
public class ResourceResolver implements LSResourceResolver {

    private static final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    public LSInput resolveResource(
            String type,
            String namespaceURI,
            String publicId,
            String systemId,
            String baseURI
    ) {
        Resource resource = resolver.getResource("classpath:xsd/" + systemId);

        try {
            return new Input(publicId, systemId, resource.getInputStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
