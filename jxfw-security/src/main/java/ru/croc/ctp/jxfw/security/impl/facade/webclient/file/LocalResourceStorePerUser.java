package ru.croc.ctp.jxfw.security.impl.facade.webclient.file;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException;
import ru.croc.ctp.jxfw.core.facade.webclient.file.LocalResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;

import java.io.File;

/**
 * Сервис временного хранилища.
 * Хранит файлы в папке файловой системы.
 * Отличается от {@link LocalResourceStore} тем, что хранит данные по раздельно по каждому пользователю.
 *
 * @author Nosov Alexander
 * @since 1.3
 */
public class LocalResourceStorePerUser extends LocalResourceStore {

    private static final Logger log = LoggerFactory.getLogger(LocalResourceStorePerUser.class);

    /**
     * Конструктор.
     *
     * @param rootDirAbsolutePath Абсолютный путь файловой папки, где будет храниться контент
     * @param quotaPerUser        Объем хранилища в байтах, доступный одному пользователю
     */
    public LocalResourceStorePerUser(String rootDirAbsolutePath, Long quotaPerUser) {
        super(rootDirAbsolutePath, quotaPerUser);
    }


    @Override
    protected void runUserChecksBeforeUpload(ResourceProperties resourceProperties) {
        final Authentication authentication = getAuth();

        // NOTE: модуль секьюрити может быть отключен, либо отсутствует конфиг по типу main.config.json
        if (authentication != null) {
            if (!hasPrivilegies(authentication)) {
                throw new XSecurityException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException.privileges.message",
                        "{0} has insufficient privileges")
                        .addArgument(authentication.getName()).build();
            }
            XSecurityException quota;
            if ((quota = quotaResult(authentication, resourceProperties.getContentSize())) != null) {
                throw quota;
            }
        }
    }

    private Authentication getAuth() {
        final SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    private boolean hasPrivilegies(Authentication authentication) {
        //TODO реализация
        return true;
    }

    /**
     * Контроль квоты объема временного хранилища.
     *
     * @param authentication {@link Authentication}
     * @param contentSize    Размер файла загруженного пользователем
     * @return null значит прошли проверку, текст - не прошли
     */
    private XSecurityException quotaResult(Authentication authentication, Long contentSize) {
        File dir = getUserInboundDir();
        //использованный объем пользователем
        final long usedSpaceByUser = FileUtils.sizeOfDirectory(dir);
        //суммарный объем, который получится если посчитать уже испоьзовано + загружено
        final long totalSize = usedSpaceByUser + contentSize;
        if (totalSize > getQuotaPerUser()) { //problem
            return new XSecurityException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException.quota.message",
                    "{0}, disk quota exceeded, total {1}, max allowed {2}")
                    .addArgument(authentication.getName())
                    .addArgument(totalSize)
                    .addArgument(getQuotaPerUser())
                    .build();
        }
        return null; //ok
    }

    @Override
    protected String getStoreFolderName() {
        final Authentication authentication = getAuth();
        final String username = authentication != null ? authentication.getName() : "unauthorized";
        log.debug("Store folder for user is {}", username);
        return username;
    }

}
