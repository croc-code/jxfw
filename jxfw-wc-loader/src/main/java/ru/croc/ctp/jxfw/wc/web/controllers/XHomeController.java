package ru.croc.ctp.jxfw.wc.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;
import ru.croc.ctp.jxfw.wc.web.mvc.controllers.PageControllerBase;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Базовый контроллер для корневого URL. 
 * Наследник класса {@link PageControllerBase}.
 *
 * @since 1.0
 * @see PageControllerBase
 */
@Controller
public class XHomeController extends PageControllerBase {

    protected String getMainScriptName() {
        return "main.js";
    }

    /**
     * Хендлер для корневого URL.
     *
     * @param request - объект запроса.
     * @param model   - модель
     * @return HTML страница
     */
    @RequestMapping(value = {"/", "/display/**"}, produces = "text/html;charset=UTF8")
    public String index(HttpServletRequest request, Model model) {
        final MainPageModel pageModel = new MainPageModel();
        initializePageModel(pageModel, request);

        setupModel(model, pageModel);


        return "index";
    }

}
