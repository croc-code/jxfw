package ru.croc.ctp.jxfw.cli;

import org.junit.Test;

import ru.croc.ctp.jxfw.cli.CliApplication;
import ru.croc.ctp.jxfw.cli.CliProperties;
import ru.croc.ctp.jxfw.cli.dispatcher.CmdDispatcher;
import ru.croc.ctp.jxfw.cli.provider.MvnProjectProvider;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterfaceInteractionTest {

    @Test
    public void startCmdTest() throws Exception {
        //given:
        CmdDispatcher dispatcher = getCmdDispatcher();
        CliApplication application = new CliApplication();
        application.setCmdDispatcher(dispatcher);
        application.setCliProperties(getProps());
        //ручками
        application.onApplicationEvent(null);

        //when:
        application.start();

        //then:
        verify(dispatcher, atLeastOnce()).start(any(File.class), any(File.class), any());
    }

    private CliProperties getProps() {
        CliProperties mock = mock(CliProperties.class);
        when(mock.getPom()).thenReturn("file");
        when(mock.getSettings()).thenReturn("file");
        return mock;
    }

    @Test
    public void stopCmdTest() {
        //given:
        CmdDispatcher dispatcher = spy(CmdDispatcher.class);
        CliApplication application = new CliApplication();
        application.setCmdDispatcher(dispatcher);

        //when:
        application.stop();

        //then:
        verify(dispatcher, atLeastOnce()).stop();
    }

    private CmdDispatcher getCmdDispatcher() {
        CmdDispatcher dispatcher = spy(CmdDispatcher.class);
        dispatcher.setMvnProjectProvider(mock(MvnProjectProvider.class));
        return dispatcher;
    }
}
