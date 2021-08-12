package ru.croc.ctp.jxfw.cli.dispatcher;

import org.junit.Test;

import ru.croc.ctp.jxfw.cli.CliProperties;
import ru.croc.ctp.jxfw.cli.dispatcher.CmdDispatcher;
import ru.croc.ctp.jxfw.cli.provider.MvnProjectProvider;

import java.io.File;

import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CmdDispatcherTest {

    @Test
    public void interaction() throws Exception {
        CmdDispatcher cmdDispatcher = spy(new CmdDispatcher());
        MvnProjectProvider mvnProjectProvider = mock(MvnProjectProvider.class);
        cmdDispatcher.setMvnProjectProvider(mvnProjectProvider);

        File parentPom = getTestFile("src/test/resources/mvn-parent-child/pom.xml");
        File settings = getTestFile("src/test/resources/settings.xml");

        CliProperties properties = mock(CliProperties.class);
        cmdDispatcher.start(parentPom, settings, properties);

        verify(cmdDispatcher, atLeastOnce()).start(parentPom, settings, properties);

    }

}
