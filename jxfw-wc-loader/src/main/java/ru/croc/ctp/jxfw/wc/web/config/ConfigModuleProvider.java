package ru.croc.ctp.jxfw.wc.web.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Компонент для прохода конфигурации по конвейеру.
 * 
 * @author Nosov Alexander
 * @since 1.2
 */
@Service
@Scope(SCOPE_SINGLETON)
public class ConfigModuleProvider extends ApplicationObjectSupport {

    
    private List<ConfigModuleModifier> configModules;

    private XConfig config;

    @Autowired
    public void setConfigModules(List<ConfigModuleModifier> configModules) {
        this.configModules = configModules;
    }

    /**
     * @return загрузить конфигурационный файл.
     */
    public XConfig loadConfig() {
        if (config != null) {
            return config;
        }
        XConfig config = null;
        for (ConfigModuleModifier configModule : configModules) {
            if (config == null) {
                if (configModule instanceof ConfigModule) {
                    config = ((ConfigModule) configModule).createClientConfig();
                    continue;
                }
            }
            configModule.createClientConfig(config);
        }
        return this.config = config;
    }
}
