package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportParamMismatchException;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Коллекция параметров отчета.
 * Created by vsavenkov on 21.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReportParams {

    /**
     * "Сырые" параметры отчета.
     */
    private Map<String, Object> paramsNonTyped;

    /**
     * Один параметр отчета.
     */
    public static class ReportParam {

        /**
         * Значение параметра.
         */
        private Object value;

        /**
         * Профиль параметра.
         */
        private ReportClass.ParamsClass.ParamClass profile;


        /**
         * Конструктор.
         *
         * @param paramProfile Профиль параметра
         */
        public ReportParam(ReportClass.ParamsClass.ParamClass paramProfile) {
            profile = paramProfile;
            if (profile.getDefault() != null && profile.getDefault().length() != 0) {
                parse(profile.getDefault());
            }
        }

        /**
         * Профиль параметра.
         *
         * @return профиль параметра
         */
        public ReportClass.ParamsClass.ParamClass getProfile() {
            return profile;
        }

        /**
         * Тип параметра.
         *
         * @return String - возвращает строковое описание типа параметра
         */
        public String getXmlType() {
            return profile.getVt().value();
        }

        /**
         * Имя параметра.
         *
         * @return имя параметра
         */
        public String getName() {
            return profile.getN();
        }

        /**
         * Обязательность параметра.
         *
         * @return true если параметр обязательный
         */
        public boolean isRequired() {
            return (profile.isRequired() && "object" != getXmlType());
        }

        /**
         * Параметр нельзя инициализировать извне.
         *
         * @return true если параметр нельзя инициализировать извне
         */
        public boolean isProtected() {
            return profile.isProtected() || "object" == getXmlType();
        }

        /**
         * Параметр пуст.
         *
         * @return true если параметр пуст
         */
        public boolean isNull() {
            return Converter.isNull(value);
        }

        /**
         * Значение параметра.
         * Причем без дурацкого преобразования boolean в int!
         *
         * @return значение параметра
         */
        protected Object getValue() {
            return value;
        }

        /**
         * Значение параметра.
         *
         * @return знаение параметра
         */
        public Object value() {
            if (profile.getVt() == VarTypesClass.BOOLEAN) {
                if (value == null) {
                    // Почему вместо null возвращаем 0, мне непонятно.
                    // Оставлено, чтобы не потерять совместимость
                    return 0;
                } else {
                    return (boolean) value ? 1 : 0;
                }
            }

            return value;
        }

        /**
         * Установка значения параметра.
         *
         * @param value - Значение параметра.
         */
        public void setValue(Object value) {

            // Если Null то пропустим свободно
            if (value == null/* TODO: сразу не нашёл, как это в Java перетащить  || value == DBNull.Value*/) {
                this.value = null;
                return;
            }
            // Если не null то произведем дополнительную проверку типов значений
            switch (getXmlType()) {
                case "date":
                case "dateTime.tz":
                case "time.tz":
                    if (!(value instanceof Date)) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "i4":
                    if (!(value instanceof Integer)) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "r8":
                    if (!(value instanceof Double)) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "fixed.14.4":
                    if (!(value instanceof BigDecimal)) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "boolean":
                    try {
                        value = Converter.toBoolean(value.toString());
                    } catch (NullPointerException e) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "uuid":
                    if (!(value instanceof UUID)) {
                        throwException("Неверный тип параметра", null, value.toString(), null);
                    }
                    break;
                case "object":
                    break;
                default:
                    value = value.toString();
                    break;
            }
            this.value = value;
        }

        /**
         * Установка значения параметра из строки.
         *
         * @param from - Строка
         * @return Object   - Значение
         */
        public Object parse(String from) {
            return parse(from, false);
        }

        /**
         * Установка значения параметра из строки.
         *
         * @param from       - Строка
         * @param ignoreNull - Игнорировать пустые значения
         * @return Object   - Значение
         */
        public Object parse(String from, boolean ignoreNull) {
            Object temp = value;

            if (!ignoreNull) {
                value = null;
            }
            if (from == null || from.length() == 0) {
                return value;
            }
            try {
                value = Converter.toObject(from, getXmlType());
            } catch (Exception e) {
                value = temp;
                throwException("Ошибка преобразования параметра", null, from, e);
            }

            return value;
        }

        /**
         * Генерация исключения.
         *
         * @param defaultMessage - Сообщение
         * @param param          - Параметр, на котором произошло исключение
         * @param paramValue     - Значение параметра
         * @param inner          - Вложенное исключение
         */
        public void throwException(String defaultMessage,
                                   ReportClass.ParamsClass.ParamClass param,
                                   String paramValue,
                                   Exception inner) {
            String userMessage = null;
            if (param != null) {
                userMessage = param.getMessageIfAbsent();
            }

            throw new ReportParamMismatchException((userMessage == null ? defaultMessage : userMessage),
                    getName(),
                    paramValue,
                    inner);
        }
    }

    /**
     * коллекция параметров.
     */
    private List<ReportClass.ParamsClass.ParamClass> profileParams;

    /**
     * Типизированная коллекция параметров.
     */
    private Hashtable params;

    /**
     * Процессор макросов.
     */
    private MacroProcessor processor;

    /**
     * На основе профиля отчета строит типизированную коллекцию параметров,
     * заполняет значения переданными в конструктор.
     * В случае невозможности приведения типа выдает исключение ReportParamException.
     * В случае, если передана пустая строка, или параметр отсутствует в
     * переданной коллекции, различаем 2 ситуации:
     * 1. В профиле задано значение по умолчанию для параметра - подставляется оно.
     * 2. Значение по умолчанию на задано - подставляется null.
     *
     * @param params         - Коллекция параметров отчета
     * @param paramsNonTyped - Параметры переданные в отчет в строковом виде
     */
    public ReportParams(List<ReportClass.ParamsClass.ParamClass> params, Map<String, String> paramsNonTyped) {
        this(params, paramsNonTyped, true);
    }

    /**
     * На основе профиля отчета строит типизированную коллекцию параметров,
     * заполняет значения переданными в конструктор.
     * В случае невозможности приведения типа выдает исключение ReportParamException.
     * В случае, если передана пустая строка, или параметр отсутствует в
     * переданной коллекции, различаем 2 ситуации:
     * 1. В профиле задано значение по умолчанию для параметра - подставляется оно.
     * 2. Значение по умолчанию на задано - подставляется null.
     *
     * @param params                - Коллекция параметров отчета
     * @param rawParams             - Параметры переданные в отчет в строковом виде
     * @param processRequiredParams - Признак необходимости проверить параметры на обязательность
     */
    public ReportParams(List<ReportClass.ParamsClass.ParamClass> params,
                        Map<String, String> rawParams,
                        boolean processRequiredParams) {
        String paramName;                    // Имя параметра
        ReportParams.ReportParam myParam;    // Параметр

        setParamsNonTyped(rawParams);
        processor = new MacroProcessor(null, this);
        profileParams = params;

        //Если параметры не определены просто тихо выйдем
        if (null == profileParams) {
            return;
        }
        this.params = new Hashtable();

        for (ReportClass.ParamsClass.ParamClass oneParam : profileParams) {
            // Добавим в коллекцию
            myParam = new ReportParam(oneParam);
            paramName = myParam.getName();
            if (!myParam.isProtected()) {
                if (rawParams != null) {
                    myParam.parse(rawParams.get(paramName), true);
                }

                if (processRequiredParams && myParam.isNull() && myParam.isRequired()) {
                    // Если параметр отсутствует  проверим его обязательность
                    myParam.throwException("Отсутвует обязательный параметр " + paramName,
                            myParam.getProfile(),
                            StringUtils.EMPTY, null);
                }
            }
            this.params.put(paramName, myParam);
        }
    }


    /**
     * Конструктор инициализирует объект ReportParams на основе другого объекта.
     *
     * @param params - параметры
     */
    public ReportParams(ReportParams params) {

        if (params == null) {
            throw new ArgumentNullException("oParams");
        }
        this.params = new Hashtable(params.getCount());
        Enumeration paramsEnumerator = params.getNames();
        while (paramsEnumerator.hasMoreElements()) {
            addParam(params.getParam((String) paramsEnumerator.nextElement()));
        }
    }

    /**
     * Получение параметра по имени.
     *
     * @param name - Имя параметра
     * @return ReportParam  - Параметр
     */
    public ReportParam getParam(String name) {

        ReportParam result = (params == null) ? null : (ReportParam) params.get(name);
        if (result == null) {
            throw new ArgumentException("Параметр " + name + " не определен");
        }
        return result;
    }

    /**
     * Получение значения параметра по имени.
     *
     * @param name Имя параметра
     * @return значение или null
     */
    public <T> T getParamValueOrNull(String name, Class<T> clazz) {

        ReportParam result = (params == null) ? null : (ReportParam) params.get(name);
        if (result == null) {
            return null;
        }
        return clazz.cast(result.getValue());
    }

    /**
     * Проверяет существование параметра с указанным именем.
     *
     * @param name - Имя параметра
     * @return boolean  - возвращает true если такой параметр декларирован в профиле, иначе false
     */
    public boolean isParamExists(String name) {

        return params == null ? false : params.containsKey(name);
    }

    /**
     * Количество параметров в коллекции.
     *
     * @return int  - возвращает количество параметров в коллекции
     */
    public int getCount() {

        return params == null ? 0 : params.size();
    }

    /**
     * Набор имен параметров.
     *
     * @return Enumeration  - возвращает набор имен параметров
     */
    public Enumeration getNames() {

        return params == null ? new Hashtable().keys() : params.keys();
    }

    /**
     * Устанавливает значение параметра па имени.
     *
     * @param paramName имя парамтера
     * @param value     значение
     */
    public void setParamValue(String paramName, Object value) {

        ReportParam param = getParam(paramName);
        param.value = value;
    }

    /**
     * проверяет пустой ли параметр.
     *
     * @param name имя параметра
     * @return true если параметр пустой
     */
    public boolean isEmptyParam(String name) {

        ReportParam param = getParam(name);
        return param.value == null;
    }

    /**
     * Добавляет параметр в коллекцию.
     *
     * @param newParam - добавляемый параметр
     */
    public void addParam(ReportParam newParam) {

        if (newParam == null) {
            throw new ArgumentNullException("newParam");
        }

        if (params == null) {
            params = new Hashtable();
        }

        params.put(newParam.getName(), newParam);
    }

    /**
     * Удаляет параметр из коллекции по имени.
     *
     * @param paramName - Наименование параметра, который должен быть удален из коллекции
     */
    public void removeParam(String paramName) {

        if (params != null) {
            params.remove(paramName);
        }
    }

    /**
     * Параметры хранятся в строковом представлении, если значение множественное, то конкатенируется через "&".
     *
     * @return параметры готовые для использования в {@link ru.croc.ctp.jxfw.core.facade.webclient.DomainDeserializer}
     */
    public Map<String, Object> getParamsNonTyped() {
        return paramsNonTyped;
    }

    /**
     * Сырые параметры отчета, перевести в готовые для использования
     * в {@link ru.croc.ctp.jxfw.core.facade.webclient.DomainDeserializer}.
     *
     * @param rawParams Сырые параметры отчета (строковый вид)
     */
    public void setParamsNonTyped(Map<String, String> rawParams) {
        Map<String, Object> result = new HashMap<>();
        if (rawParams != null) {
            rawParams.forEach((pn, pv) -> {
                if (StringUtils.isNotEmpty(pv) && pv.contains("&")) {
                    result.put(pn, Arrays.asList(pv.split("&")));
                } else {
                    result.put(pn, pv);
                }
            });
        }
        this.paramsNonTyped = result;
    }
}
