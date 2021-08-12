package ru.croc.ctp.jxfw.cli.provider;

import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ShellOption;

import ru.croc.ctp.jxfw.cli.provider.AnyOsFileValueProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AnyOsFileValueProviderTest {

    @Test
    public void complete() {
        AnyOsFileValueProvider provider = new AnyOsFileValueProvider(true);
        List<CompletionProposal> result = provider.complete(null,
                new CompletionContext(Arrays.asList("--file", "./"), 1, "./".length()), null);
        assertNotEquals(0, result.size());
        result = provider.complete(null,
                new CompletionContext(Arrays.asList("--file", "./src/"), 1, 6), null);
        assertEquals(2, result.size());
        result = provider.complete(null,
                new CompletionContext(Arrays.asList("--file", "/"), 1, 1), null);
        assertNotEquals(0, result.size());
        result = provider.complete(null,
                new CompletionContext(Arrays.asList("--file", "xxx"), 1, 3), null);
        assertEquals(0, result.size());
    }

    @Test
    public void testSupport() throws Exception {
        AnyOsFileValueProvider providerForAll = new AnyOsFileValueProvider(true);

        Method method = TestCommand.class.getDeclaredMethod("test", File.class, File.class, String.class);

        MethodParameter paramFileWithProvider = MethodParameter.forParameter(method.getParameters()[0]);
        MethodParameter paramWithoutProvider = MethodParameter.forParameter(method.getParameters()[1]);
        MethodParameter paramNotFile = MethodParameter.forParameter(method.getParameters()[2]);

        assertTrue(providerForAll.supports(paramFileWithProvider, null));
        assertTrue(providerForAll.supports(paramWithoutProvider, null));
        assertFalse(providerForAll.supports(paramNotFile, null));

        AnyOsFileValueProvider providerForDeclaredOnly = new AnyOsFileValueProvider(false);
        assertTrue(providerForDeclaredOnly.supports(paramFileWithProvider, null));
        assertFalse(providerForDeclaredOnly.supports(paramWithoutProvider, null));
        assertFalse(providerForDeclaredOnly.supports(paramNotFile, null));
    }

    private static class TestCommand {
        @SuppressWarnings("unused")
		private void test(@ShellOption(valueProvider = AnyOsFileValueProvider.class) File file, File otherFile, String notAFile) {
        }
    }
}