package ru.croc.ctp.jxfw.core.domain.impl;



import org.junit.Before;
import org.junit.Test;
import ru.croc.ctp.jxfw.core.domain.SaveStateManager;
import ru.croc.ctp.jxfw.core.load.GeneralLoadContext;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.events.AfterLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.CheckSecurityEvent;
import ru.croc.ctp.jxfw.core.load.events.LoadEvent;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.core.load.impl.MultiLoadServiceImpl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SaveStateManagerImplTest {
    private SaveStateManager saveStateManager;

    @Before
    public void init() {
        saveStateManager = new SaveStateManagerImpl();
    }

    /**
     * Проверяет ценарий: отлючаем всё и включаем нужное. После следует еще ряд манимуляций.
     */
    @Test
    public void includesTest() {
        // Отключаем всё и добавляем только, то что интересует
        saveStateManager.disableAll();
        saveStateManager.enable(LoadService.class, LoadContext.class);
        saveStateManager.enable(LoadServiceImpl.class.getPackage());
        saveStateManager.reset(LoadServiceImpl.class);
        // Проверки
        assertTrue(isEnable(LoadService.class));
        assertTrue(isEnable(LoadContext.class));
        assertTrue(isEnable(MultiLoadServiceImpl.class));
        assertFalse(isEnable(AfterLoadEvent.class));
        assertFalse(saveStateManager.isEnable(LoadServiceImpl.class).isPresent());

        // Переустанавливаем значения
        saveStateManager.enable(LoadServiceImpl.class);
        saveStateManager.disable(LoadService.class.getPackage());
        // Проверки
        assertFalse(isEnable(LoadService.class));
        assertFalse(isEnable(LoadContext.class));
        assertTrue(isEnable(MultiLoadServiceImpl.class));
        assertFalse(isEnable(AfterLoadEvent.class));
        assertTrue(isEnable(LoadServiceImpl.class));

        // сбрасываем пакет
        saveStateManager.reset(LoadService.class.getPackage());
        // Проверки
        assertFalse(saveStateManager.isEnable(LoadService.class).isPresent());
        assertFalse(saveStateManager.isEnable(LoadResult.class).isPresent());
        assertFalse(isEnable(AfterLoadEvent.class));

        // перекрытие для класса поверх reset пакета
        saveStateManager.enable(LoadService.class);
        // Проверки
        assertTrue(isEnable(LoadService.class));
        assertFalse(saveStateManager.isEnable(LoadResult.class).isPresent());
        assertFalse(isEnable(AfterLoadEvent.class));
    }

    /**
     * Проверяет ценарий: включаем всё и отключаем ненужное.  После следует еще ряд манимуляций.
     */
    @Test
    public void excludesTest() {
        // Отключаем всё и добавляем только, то что интересует
        saveStateManager.enableAll();
        saveStateManager.disable(LoadService.class, LoadContext.class);
        saveStateManager.disable(LoadServiceImpl.class.getPackage());
        saveStateManager.reset(LoadServiceImpl.class);

        // Проверки
        assertFalse(isEnable(LoadService.class));
        assertFalse(isEnable(LoadContext.class));
        assertFalse(isEnable(MultiLoadServiceImpl.class));
        assertTrue(isEnable(AfterLoadEvent.class));
        assertFalse(saveStateManager.isEnable(LoadServiceImpl.class).isPresent());

        // Переустанавливаем значения
        saveStateManager.disable(LoadServiceImpl.class);
        saveStateManager.enable(LoadService.class.getPackage());
        // Проверки
        assertTrue(isEnable(LoadService.class));
        assertTrue(isEnable(LoadContext.class));
        assertFalse(isEnable(MultiLoadServiceImpl.class));
        assertTrue(isEnable(AfterLoadEvent.class));
        assertFalse(isEnable(LoadServiceImpl.class));


        // сбрасываем пакет
        saveStateManager.reset(LoadService.class.getPackage(), LoadEvent.class.getPackage());
        saveStateManager.enable(AfterLoadEvent.class, BeforeLoadEvent.class);
        // Проверки
        assertFalse(saveStateManager.isEnable(LoadService.class).isPresent());
        assertFalse(saveStateManager.isEnable(LoadResult.class).isPresent());
        assertFalse(saveStateManager.isEnable(CheckSecurityEvent.class).isPresent());
        assertTrue(isEnable(AfterLoadEvent.class));
        assertTrue(isEnable(BeforeLoadEvent.class));

        // перекрытие для класса поверх reset пакета
        saveStateManager.enable(LoadService.class);
        saveStateManager.reset(AfterLoadEvent.class);
        // Проверки
        assertTrue(isEnable(LoadService.class));
        assertFalse(saveStateManager.isEnable(LoadResult.class).isPresent());
        assertFalse(saveStateManager.isEnable(AfterLoadEvent.class).isPresent());
        assertTrue(isEnable(BeforeLoadEvent.class));
    }

    @Test
    public void replaceClassesToPackageTest() {
        // Перекрытие для классов из двух пакетов
        saveStateManager.enable(LoadContext.class, LoadService.class, AfterLoadEvent.class, BeforeLoadEvent.class);
        // Указанные классы перекрылись
        assertTrue(isEnable(LoadContext.class));
        assertTrue(isEnable(LoadService.class));
        assertTrue(isEnable(AfterLoadEvent.class));
        assertTrue(isEnable(BeforeLoadEvent.class));
        // Не указанные нет
        assertFalse(saveStateManager.isEnable(LoadEvent.class).isPresent());
        assertFalse(saveStateManager.isEnable(LoadServiceImpl.class).isPresent());

        // Установим значения на пакеты, значения установленные для классов должны затереться
        saveStateManager.disable(LoadContext.class.getPackage(), AfterLoadEvent.class.getPackage());
        // Проверяем, что значения обновились
        assertFalse(isEnable(LoadContext.class));
        assertFalse(isEnable(LoadService.class));
        assertFalse(isEnable(AfterLoadEvent.class));
        assertFalse(isEnable(BeforeLoadEvent.class));
        // стальные классы в пакетах
        assertFalse(isEnable(LoadResult.class));
        assertFalse(isEnable(LoadEvent.class));
        // Здесь ничего меняться не должно
        assertFalse(saveStateManager.isEnable(LoadServiceImpl.class).isPresent());

        // сбросим пакет
        saveStateManager.reset(LoadContext.class.getPackage());
        // Проверяем, что значения обновились
        assertFalse(saveStateManager.isEnable(LoadContext.class).isPresent());
        assertFalse(saveStateManager.isEnable(LoadService.class).isPresent());
        assertFalse(isEnable(AfterLoadEvent.class));
        assertFalse(isEnable(BeforeLoadEvent.class));
        assertFalse(saveStateManager.isEnable(LoadResult.class).isPresent());
        assertFalse(isEnable(LoadEvent.class));
        // Здесь ничего меняться не должно
        assertFalse(saveStateManager.isEnable(LoadServiceImpl.class).isPresent());
    }

    private Boolean isEnable(Class<? > clazz) {
        return saveStateManager.isEnable(clazz).get();
    }
}
