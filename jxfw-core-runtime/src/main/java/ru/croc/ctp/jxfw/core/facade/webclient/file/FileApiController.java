package ru.croc.ctp.jxfw.core.facade.webclient.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер для загрузки файлов.
 */
@RestController
@RequestMapping("**/api/_file/")
public class FileApiController extends ControllerBase {

    private class ResourceId {

        ResourceId(String id) {
            this.resourceId = id;
        }

        String resourceId;

        public String getResourceId() {
            return resourceId;
        }

    }

    private final ResourceStore resourceStore;

    private static final Logger logger = LoggerFactory
        .getLogger(FileApiController.class);

    /**
     * Конструктор.
     *
     * @param resourceStore Сервис временного хранения загруженных с клиента файлов
     */
    @Autowired
    public FileApiController(ResourceStore resourceStore) {
        Assert.notNull(resourceStore, "Constructor parameter resourceStore should not be null");
        this.resourceStore = resourceStore;
    }

    /**
     * Загружает файл на сервер во временное хранилище.
     *
     * @param multiPart Файл для загрузки
     * @return Идентификатор загруженного файла во временном хранилище
     * @throws IOException Исключение создается в слуае ошибок работы с файлом
     */
    @RequestMapping(value = {"upload", "Upload"}, method = RequestMethod.POST, consumes = "multipart/form-data",
        produces = "application/json")
    @ResponseBody
    public ResourceId upload(
        @RequestParam(value = "file") final MultipartFile multiPart)
        throws IOException {

        ResourceProperties resourceProperties = new ResourceProperties(multiPart.getContentType(),
            multiPart.getOriginalFilename(), multiPart.getSize());

        try (InputStream inputStream = multiPart.getInputStream()) {
            String resId = resourceStore.addResource(resourceProperties, inputStream);
            return new ResourceId(resId);
        }
    }

    /**
     * Загружает ранее отправленный на сервер файл обратно на клиент.
     *
     * @param request    http-запрос
     * @param response   http-ответ
     * @param resourceId Идентификатор файла во временном хранилище на сервере
     * @param fileName   Имя файла
     */
    @RequestMapping(value = "resource", method = RequestMethod.GET)
    public void getResource(final HttpServletRequest request,
                            final HttpServletResponse response,
                            @RequestParam final String resourceId,
                            @RequestParam(required = false) final String fileName) {
        logger.debug("Downloading resource " + resourceId + " with file name " + fileName);

        try (InputStream stream = resourceStore.getResourceStream(resourceId)) {
            //ВАЖНО!!! Устанавливать заголовки ответа нужно до вызова response.getOutputStream()
            buildBinPropResponse(request, response, resourceStore.getResourceProperties(resourceId));

            IOUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Удаляет ранее загруженный файл из временного хранилища на сервере.
     *
     * @param response   http-ответ
     * @param resourceId Идентификатор файла во временном хранилище на сервере
     */
    @RequestMapping(value = "resource/delete", method = RequestMethod.POST)
    public void delete(final HttpServletResponse response,
                       @RequestParam final String resourceId) {
        logger.debug("Delete resource " + resourceId);
        resourceStore.deleteResource(resourceId);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
