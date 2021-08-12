package ru.croc.ctp.jxfw.cli.watch;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ru.croc.ctp.jxfw.cli.watch.EventsAccumulator;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EventsAccumulatorTest {

    @Test
    public void callConsumerReturns() throws InterruptedException {
        //given:
        @SuppressWarnings("unchecked")
        Consumer<Collection<File>> onSignal = spy(Consumer.class);
        EventsAccumulator eventsAccumulator = new EventsAccumulator(onSignal);
        HashSet<File> files = new HashSet<>();

        //when:
        eventsAccumulator.add(files);
        Thread.sleep(1100);

        //then:
        verify(onSignal, never()).accept(any());
    }

    @Test
    public void callConsumer() throws InterruptedException {
        //given:
        @SuppressWarnings("unchecked")
        Consumer<Collection<File>> onSignal = spy(Consumer.class);
        EventsAccumulator eventsAccumulator = new EventsAccumulator(onSignal);
        HashSet<File> files = new HashSet<>();
        files.add(new File("just"));

        //when:
        eventsAccumulator.add(files);
        Thread.sleep(1100);

        //then:
        verify(onSignal, atLeastOnce()).accept(any());
    }

    @Test
    public void consumerInvokedWithAggregatedFilesForSequenceEvents() throws InterruptedException {
        //given:
        @SuppressWarnings("unchecked")
        Consumer<Collection<File>> onSignal = spy(Consumer.class);
        EventsAccumulator eventsAccumulator = new EventsAccumulator(onSignal);
        File first = new File("1");
        File second = new File("2");

        //when:
        eventsAccumulator.add(Arrays.asList(first));

        //and when:
        eventsAccumulator.add(Arrays.asList(second));
        Thread.sleep(1100);

        //then:
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Collection<File>> argumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(onSignal, atMost(1)).accept(argumentCaptor.capture());
        Collection<File> collection = argumentCaptor.getValue();
        assertTrue(collection.contains(first));
        assertTrue(collection.contains(second));

    }

    @Test
    public void consumerInvokedTwiceSinceDelayBetweenEvents() throws InterruptedException {
        //given:
        @SuppressWarnings("unchecked")
        Consumer<Collection<File>> onSignal = spy(Consumer.class);
        EventsAccumulator eventsAccumulator = new EventsAccumulator(onSignal);
        File first = new File("1");
        File second = new File("2");

        //when:
        eventsAccumulator.add(Arrays.asList(first));
        Thread.sleep(1100);

        //and when:
        eventsAccumulator.add(Arrays.asList(second));
        Thread.sleep(1100);

        //then:
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Collection<File>> argumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(onSignal, atLeast(2)).accept(argumentCaptor.capture());
        Collection<File> collection = argumentCaptor.getValue();
        //т.к. этот уже процессился отдельно в превом вызове
        assertTrue(!collection.contains(first));
        assertTrue(collection.contains(second));
    }
}
