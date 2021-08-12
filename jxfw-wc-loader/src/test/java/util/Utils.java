package util;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * Полезности в тестах.
 * @author SMufazzalov
 * @since 1.4
 */
public class Utils {

    /**
     * Получить файл в виде Resource по имени из папки ресурсов static.
     *
     * @param fileName имя файла
     * @return InputStreamResource
     */
    public static InputStreamResource getResource(String fileName) {
        InputStream resourceAsStream = Utils.class.getResourceAsStream("/static/" + fileName);
        return new InputStreamResource(resourceAsStream);
    }
}
