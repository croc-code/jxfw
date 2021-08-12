package ru.croc.ctp.jxfw.core.metamodel.runtime;

import static com.google.common.collect.Lists.newArrayList;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.analitycs.XfwClassAnalytics;

import java.util.List;

/**
 * Бин запускает все анализаторы метамодели, которые есть в контексте приложения,
 * по событию поднятия контекста.
 *
 * @author OKrutova
 * @since 1.6
 */
public class MetamodelAnalyticsComponent {

    private final List<XfwClassAnalytics> analytics;
    private final XfwModel xfwModel;
    private final boolean enabled;

    /**
     * Конструктор.
     *
     * @param analytics анализаторы метамодели
     * @param xfwModel  метамодель
     * @param enabled   включено
     */
    public MetamodelAnalyticsComponent(List<XfwClassAnalytics> analytics, XfwModel xfwModel, boolean enabled) {
        this.analytics = analytics != null ? analytics : newArrayList();
        this.xfwModel = xfwModel;
        this.enabled = enabled;
    }


    /**
     * Отрабатывает при старте контекста.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        if (enabled) {
            for (XfwClass xfwClass : xfwModel.getAll(XfwClass.class)) {
                for (XfwClassAnalytics analytic : analytics) {
                    analytic.analyze(xfwClass);
                }
            }

        }
    }
}
