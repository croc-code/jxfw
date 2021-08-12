package ru.croc.ctp.jxfw.core.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.croc.ctp.jxfw.core.exception.dto.DomainViolationTo;
import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XInvalidDataExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XOptimisticConcurrencyExceptionTo;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolation;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Collections;
import java.util.Locale;

public class ExceptionLocalizationTest {

    static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    static XfwMessageTemplateResolver resolver = new XfwMessageTemplateResolver();

    static {
        messageSource.setBasename("xfwMessages");
        resolver.setMessageSource(messageSource);
    }

    static Locale ruLocale = new Locale.Builder().setLanguage("ru").build();
    static Locale enLocale = new Locale.Builder().setLanguage("en").build();

    @Test
    public void XExceptionTest() {
        // create
        XException ex = new XException.Builder(null,"message")
                .cause(new RuntimeException("cause"))
                .containsUserDescription(true)
                .helpLink("helpLink")
                .sourceLogEntryUniqueId("sourceLogEntryUniqueId")
                .addArgument("arg1")
                .addArgument(22)
                .build();

        // конвертируем в ТО и локализуем. в сообщении нет параметров, сообщение не из бандла
        XExceptionTo exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "message");
        assertEquals(exTo.getHelpLink(), "helpLink");
        assertEquals(exTo.isContainsUserDescription(), true);
        assertEquals(exTo.getSourceLogEntryUniqueId(), "sourceLogEntryUniqueId");

        // create
        ex = new XException.Builder(null,"message {0} {1} {2}")
                .addArgument("arg1")
                .addArgument(22)
                .build();

        // конвертируем в ТО и локализуем. в сообщении есть параметры, сообщение не из бандла
        exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "message arg1 22 {2}");

        // конвертируем в ТО и локализуем. в сообщении есть параметры, сообщение не из бандла. нет messageSource
      //  exTo = new XExceptionTo(ex, null, ruLocale);
      //  assertEquals(exTo.getDefaultMessage(), "message arg1 22 {2}");

        // create
        ex = new XException.Builder("test.message", "")
                .addArgument("arg1")
                .addArgument(22)
                .build();

        // конвертируем в ТО и локализуем. в сообщении есть параметры, сообщение из бандла
        exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "тест arg1 22 {2}");

        // конвертируем в ТО и локализуем. в сообщении есть параметры, сообщение из бандла
        exTo = new XExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "this is test message arg1 22 {2}");


        // null в аргументах
        ex = new XException.Builder(null,"message {0} {1} {2}")
                .addArgument(null)
                .addArgument(22)
                .build();
        exTo = new XExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "message null 22 {2}");

    }


    @Test
    public void XInvalidDataExceptionTest() {
        // построение сообщение по методике из версии <=1.6
        XInvalidDataException ex = new XInvalidDataException("typeName" , "id", "customMessage");
        XInvalidDataExceptionTo exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Некорректные входные данные для объекта [typeName] с идентификатором id: customMessage.");

        // задаем Identity несколько раз. в исключении должен остаться только последний.
        ex = new XInvalidDataException.Builder<>()
                .identity("typeName4" , "id4")
                .identity("typeName2" , "id2")
                .identity("typeName3" , "id3")
                .identity("typeName" , "id")
                .build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Некорректные входные данные для объекта [typeName] с идентификатором id.");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Incorrect input for object [typeName] with identifier id.");
        assertEquals(exTo.getIdentities().size(), 2);
        assertEquals(exTo.getIdentities().get("type"), "typeName");
        assertEquals(exTo.getIdentities().get("id"), "id");

        ex = new XInvalidDataException.Builder<>(null,"custom message {0} {1} {2} {3}")
                .identity("typeName4" , "id4")
                .addArgument("my1")
                .identity("typeName2" , "id2")
                .identity("typeName3" , "id3")
                .identity("typeName" , "id")
                .addArgument("my2")
                .build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "custom message [typeName] id my1 my2");


        ex = new XInvalidDataException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.ts.message", "")
                .identity("typeName" , "id")
                .addArgument(1)
                .addArgument(2)
                .build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Некорректная версия объекта [typeName] с идентификатором id (clientTs = 1 serverTs=2)");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Incorrect version in object [typeName] with identifier id (clientTs = 1 serverTs=2)");


        ex =  new XInvalidDataException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.validation.message","")
                .addArgument("custom message")
                .build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Ошибка валидации: custom message");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Validation failed: custom message");


        ex = new XInvalidDataException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.readonly.message","").identity("typeName" , "id").build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Ошибка проверки свойства только чтение для нового объекта [typeName]. Запрещено сохранять такие объекты из веб-клиента.");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Failed to validate readOnly check for new domain object [typeName]. You cant save readOnly entity or property from WebClient.");

        ex = new XInvalidDataException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.newremove.message","").build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals("Некорректное состояние доменного объекта. Нельзя одновременно устанавливать флаги isRemoved и isNew.", exTo.getMessage());
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals("Illegal domain object state. Cannot be set isRemoved and isNew together.", exTo.getMessage());

        ex = new XInvalidDataException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.readonly.delete.message","").identity("typeName" , "id").build();
        exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Невозможно удалить доменный объект [typeName] id, доступный только для чтения.");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "You attempted to delete read only domain object [typeName] id.");
    }


    @Test
    public void XObjectNotFoundExceptionTest() {

        XObjectNotFoundException ex = new XObjectNotFoundException("typeName" , "id");
        XInvalidDataExceptionTo exTo = new XInvalidDataExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Объект [typeName] с идентификатором id не найден.");
        exTo = new XInvalidDataExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Object [typeName] with identifier id not found.");
    }


    @Test
    public void XSecurityExceptionTest() {

        XSecurityException ex = new XSecurityException("userName");
        XExceptionTo exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Ошибка безопасности для пользователя: userName");
        exTo = new XExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Security error for user: userName.");


        ex = new XSecurityException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException.privileges.message","")
                .addArgument("userName").build();
        exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "userName не имеет достаточных прав");
        exTo = new XExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "userName has insufficient privileges");


        ex = new XSecurityException.Builder<>("ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException.quota.message","")
                .addArgument("userName")
                .addArgument(1)
                .addArgument(2)
                .build();
        exTo = new XExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "userName, превышена квота на дисковое пространство, общее 1, максимально допустимое 2");
        exTo = new XExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "userName, disk quota exceeded, total 1, max allowed 2");
    }


    @Test
    public void XOptimisticConcurrencyExceptionTest() {

        XOptimisticConcurrencyException ex = new XOptimisticConcurrencyException("ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException.message","", Collections.emptySet(),
                Collections.emptyList(), Collections.emptyList());
        XOptimisticConcurrencyExceptionTo exTo = new XOptimisticConcurrencyExceptionTo(ex, resolver, ruLocale);
        assertEquals(exTo.getMessage(), "Обнаружены конфликты в сохраняемых данных");
        exTo = new XOptimisticConcurrencyExceptionTo(ex, resolver, enLocale);
        assertEquals(exTo.getMessage(), "Conflicts detected in the data being stored");
    }

    @Test
    public void DomainViolationTest() {
        DomainViolation domainViolation = new DomainViolation.Builder().message("test.message","")
                .addMessageArgument("arg1")
                .addMessageArgument(22)
                .addMessageArgument(null)
                .build();
        DomainViolationTo dto = new DomainViolationTo( domainViolation, resolver ,ruLocale);
        assertEquals(dto.getMessage(), "тест arg1 22 null");

        domainViolation = new DomainViolation.Builder()
                .description("test.message","")
                .addDescriptionArgument("arg1")
                .addDescriptionArgument(22)
                .addDescriptionArgument(null)
                .build();
        dto = new DomainViolationTo( domainViolation, resolver ,ruLocale);
        assertEquals(dto.getDescription(), "тест arg1 22 null");   }

}
