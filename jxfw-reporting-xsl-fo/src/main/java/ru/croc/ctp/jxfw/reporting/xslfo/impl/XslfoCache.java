package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Обеспечивает функциональность работы с кешем сгенерированных отчетов в формате XSLFO.
 * Created by vsavenkov on 14.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class XslfoCache {

    private static final Logger logger = LoggerFactory.getLogger(XslfoCache.class);

    /**
     * объект для блокировки.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Хранилище XSLFO отчетов.
     */
    private final Map<String, ByteArrayOutputStream> reportXslfoCollection = new HashMap<>();

    /**
     * Очередь, в которую выстроены отчеты в кеше.
     */
    private final ArrayList reportXslfoQueue  = new ArrayList();

    /**
     * Максимальный размер кеша.
     */
    @Value("${ru.croc.ctp.jxfw.reporting.cache.size:52428800}") // 50Mb
    private long maxSizeOfXslfoCache;

    /**
     * Текущий размер данных в кеше.
     */
    private long currentSizeOfXslCache;


    /**
     * Удаляет из кеша XSLFO указанного отчета.
     * @param reportXslfoKey   - Ключ отчета
     */
    public void remove(String reportXslfoKey) {
        lock.lock();
        try {
            ByteArrayOutputStream outputStream = reportXslfoCollection.get(reportXslfoKey);
            // При наличии такого xsl-fo в хранилище, удаляем его
            if (outputStream != null) {
                reportXslfoCollection.remove(reportXslfoKey);
                reportXslfoQueue.remove(reportXslfoKey);
                currentSizeOfXslCache -= outputStream.size();
                outputStream.close();
            }
        } catch (IOException e) {
            logger.error("Error by reading xslfo", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверяет, содержится ли в кеше отчет с указанным ключом.
     * @param reportXslfoKey   - Ключ отчета
     * @return true если в кэше содержится отчёт с указанным ключом
     */
    public boolean contains(String reportXslfoKey) {
        synchronized (this) {
            return reportXslfoCollection.containsKey(reportXslfoKey);
        }
    }

    /**
     * Возвращает копию сохраненного в кеше XSLFO для отчета с указанным ключом.
     * @param reportXslfoKey   - Ключ отчета
     * @param withRemove       - Признак удаления из кэша
     * @return копию сохраненного в кеше XSLFO для отчета с указанным ключом
     */
    public ByteArrayOutputStream getXslfoStream(String reportXslfoKey, boolean withRemove) {
        lock.lock();
        try {
            ByteArrayOutputStream byteArrayOutputStream = reportXslfoCollection.get(reportXslfoKey);
            if (byteArrayOutputStream == null) {
                return null;
            } else if (withRemove) {
                // удаление из кэша
                reportXslfoCollection.remove(reportXslfoKey);
                reportXslfoQueue.remove(reportXslfoKey);
                currentSizeOfXslCache -= byteArrayOutputStream.size();
                return byteArrayOutputStream;
            } else {
                // делаем копию и возвращаем ее
                ByteArrayOutputStream resultStream = new ByteArrayOutputStream(byteArrayOutputStream.size());
                try {
                    byteArrayOutputStream.writeTo(resultStream);
                } catch (IOException e) {
                    logger.error("Error by writing xslfo to outputstream", e);
                }
                return resultStream;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Пытается поместить копию XSLFO сгенерированного отчета в кеш XSLFO.
     * Для этого осуществляет проверку наличия свободного места в кеше в соответствии
     * с ограничением размера кеша, заданным в конфигурации. Если места не хватает,
     * то предварительно чистит кеш.
     * @param newXslfo         - XSLFO вновь сгенеренного отчета
     * @param reportXslfoKey   - Ключ отчета для помещения в коллекцию
     */
    public void tryToAddXslfoToCache(ByteArrayOutputStream newXslfo, String reportXslfoKey) {
        if (newXslfo == null) {
            throw new ArgumentNullException("newXslfo");
        }

        // Если размер вновь сгенеренного XSLFO превышает максимальный размер кеша,
        // то и пробовать дальше нечего
        long lengthCash = newXslfo.size();
        if (lengthCash > maxSizeOfXslfoCache) {
            return;
        }

        lock.lock();
        try {
            // Удалим старую версию отчета из кэша, если она затесалась
            remove(reportXslfoKey);

            // Если необходимо, чистим место в кеше
            while (reportXslfoQueue.size() > 0 && currentSizeOfXslCache + lengthCash > maxSizeOfXslfoCache) {
                // Удаляем самый первый отчет в очереди (старейший)
                ByteArrayOutputStream byteArrayOutputStream = reportXslfoCollection.get(reportXslfoQueue.get(0));
                reportXslfoCollection.remove(reportXslfoQueue.get(0));
                reportXslfoQueue.remove(0);
                currentSizeOfXslCache -= byteArrayOutputStream.size();
                byteArrayOutputStream.close();
            }

            // делаем копию переданного XSLFO
            /*
            newXslfo.reset();
            */
            ByteArrayOutputStream reserveStream = new ByteArrayOutputStream((int) lengthCash);
            newXslfo.writeTo(reserveStream);
            /*
            newXslfo.reset();
            */

            // Положим вновь отрисованный XSLFO в хранилище
            reportXslfoCollection.put(reportXslfoKey, reserveStream);
            reportXslfoQueue.add(reportXslfoKey);
            currentSizeOfXslCache += lengthCash;
        } catch (IOException e) {
            logger.error("Error by adding xslfo to cash", e);
        } finally {
            lock.unlock();
        }
    }
}
