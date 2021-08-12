package ru.croc.ctp.jxfw.core.facade.webclient.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Реализация метода reset для FileInputStream, которая всегда возвращает поток к позиции 0.
 * Created by SPlaunov on 01.06.2016.
 */
public class ResettableFileInputStream extends FileInputStream {

    /**
     * Конструктор.
     *
     * @param file Файл, для чтения которого создается поток
     * @throws FileNotFoundException Исключение, если заданный файл не найден
     */
    public ResettableFileInputStream(File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Конструктор.
     *
     * @param name Системно-зависимое имя файла
     * @throws FileNotFoundException Исключение, если заданный файл не найден
     */
    public ResettableFileInputStream(String name) throws FileNotFoundException {
        super(name);
    }

    @Override
    public synchronized void reset() throws IOException {
        getChannel().position(0);
    }
}
