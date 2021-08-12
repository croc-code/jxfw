package ru.croc.ctp.jxfw.core.exceptions;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XBusinessLogicExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionToSerializer;
import ru.croc.ctp.jxfw.core.exception.dto.XInvalidDataExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XOptimisticConcurrencyExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XReferenceIntegrityViolationExceptionTo;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolation;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolationItem;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolationReason;
import ru.croc.ctp.jxfw.core.exception.exceptions.XBusinessLogicException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;
import ru.croc.ctp.jxfw.core.services.TestObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


/**
 * Тесты, работа с исключениями.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class ExceptionsTest {

    static ObjectMapper mapper = new ObjectMapper();
    static XfwMessageTemplateResolver resolver = new XfwMessageTemplateResolver();

    static {
       /* mapper.registerModule(new SimpleModule()
                .addSerializer(XExceptionTo.class, new XExceptionToSerializer<XExceptionTo>() {
                    @Override
                    protected void appendExceptionToSpecificInfo(JsonGenerator jgen, XExceptionTo vo) throws IOException {

                    }
                })
                .addSerializer(XBusinessLogicExceptionTo.class,
                        new XBusinessLogicExceptionToSerializer()));*/


        mapper.registerModule(new SimpleModule()
                .addSerializer(ExceptionTo.class, new ExceptionToSerializer()));
    }


    /**
     * JXFW-767 XBusinessLogicException возможность задавать violation типизировано
     *
     * @throws IOException
     */
    @Test
    public void testXBusinessLogicExceptionFieldsNeededForWcArePresent() throws IOException {

        String idOrig = "some id";
        String typeOrig = "SomeTestObjectType";
        String someProp = "someProp";
        String descriptionOrig = "some desc";
        DomainViolationItem violationItem = new DomainViolationItem(
                new DomainObjectIdentity(idOrig, typeOrig), someProp);
        String messageOrig = "message for DomainViolation";
        DomainViolation violation = new DomainViolation.Builder().message(null, messageOrig)
                .reason(DomainViolationReason.INTEGRITY)
                .addViolationItem(violationItem)
                .ruleId("some rule Id")
                .description(null, descriptionOrig)
                .ignorable(true)
                .build();
        XBusinessLogicException exception = new XBusinessLogicException("message",
                new HashSet<>(Arrays.asList(violation)));


        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, new XBusinessLogicExceptionTo(exception, new XfwMessageTemplateResolver(), Locale.getDefault()));

        String json = writer.toString();

        System.out.println(json);

        String msg = JsonPath.read(json, "$.violations[0].message");
        Assert.assertEquals(messageOrig, msg);

        String identityId = JsonPath.read(json, "$.violations[0].items[0].identity.id");
        String typeName = JsonPath.read(json, "$.violations[0].items[0].identity.type");

        Assert.assertEquals(idOrig, identityId);
        Assert.assertEquals(typeOrig, typeName);

        String propName = JsonPath.read(json, "$.violations[0].items[0].propertyName");
        Assert.assertEquals(someProp, propName);

        boolean severity = JsonPath.read(json, "$.violations[0].ignorable");
        Assert.assertTrue(severity);

        String description = JsonPath.read(json, "$.violations[0].description");
        Assert.assertEquals(descriptionOrig, description);

    }

    /**
     * JXFW-871 улучшения XBusinessLogicException.
     */
    @Test
    public void avoidDuplicatesForDomainViolationItem() {
        DomainViolationItem item1 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id1")),
                "prop1"
        );
        DomainViolationItem item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id2")),
                "prop2"
        );


        DomainViolation violation = new DomainViolation.Builder()
                .addViolationItem(item1)
                .addViolationItem(item2)
                .build();

        //добавим 2 одинаковых объекта
        Assert.assertEquals(2, violation.getItems().size());

        item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id1")),
                "prop1"
        );

        //теперь добавим 2 одинаковых объекта
        violation = new DomainViolation.Builder()
                .addViolationItem(item1)
                .addViolationItem(item2)
                .build();
        Assert.assertEquals(1, violation.getItems().size());

        //отличаются проперти
        item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id1")),
                "ChaNged"
        );

        violation = new DomainViolation.Builder()
                .addViolationItem(item1)
                .addViolationItem(item2)
                .build();
        Assert.assertEquals(2, violation.getItems().size());

        //отличаются идентити
        item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("ChangeD")),
                "prop1"
        );
        violation = new DomainViolation.Builder()
                .addViolationItem(item1)
                .addViolationItem(item2)
                .build();
        Assert.assertEquals(2, violation.getItems().size());
    }

    /**
     * JXFW-871 улучшения XBusinessLogicException.
     */
    @Test
    public void avoidDuplicatesForDomainViolation() {
        //добавим одинаковые объекты
        DomainViolation violation1 = new DomainViolation();
        DomainViolation violation2 = new DomainViolation();

        XBusinessLogicException exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(1, exception.getViolations().size());


        //добавим разные объекты
        violation1 = new DomainViolation();
        violation2 = new DomainViolation.Builder().reason(DomainViolationReason.INTEGRITY).build();

        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();

        Assert.assertEquals(2, exception.getViolations().size());

        //добавим одинаковые объекты
        violation1 = new DomainViolation.Builder().reason(DomainViolationReason.INTEGRITY).build();
        violation2 = new DomainViolation.Builder().reason(DomainViolationReason.INTEGRITY).build();

        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(1, exception.getViolations().size());

        DomainViolationItem item1 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id1")),
                "prop1"
        );
        DomainViolationItem item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id2")),
                "prop2"
        );


        //добавим одинаковые объекты

        violation1 = new DomainViolation.Builder()
                .addViolationItem(item1).addViolationItem(item2).build();
        violation2 = new DomainViolation.Builder()
                .addViolationItem(item1).addViolationItem(item2).build();

        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(1, exception.getViolations().size());

        //поменяем одному сообщение
        String msg = "message";
        violation1 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2).build();
        violation2 = new DomainViolation.Builder()
                .addViolationItem(item1).addViolationItem(item2).build();

        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(2, exception.getViolations().size());

        //поменяем для одинаковых объектов, одному из ruleId
        violation1 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .build();
        violation2 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .ruleId(msg)
                .build();
        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();

        Assert.assertEquals(2, exception.getViolations().size());

        //поменяем для одинаковых объектов, одному из Description
        violation1 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .ruleId(msg)
                .build();
        violation2 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .ruleId(msg)
                .description(null, msg)
                .build();
        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(2, exception.getViolations().size());

        //добавим одинаковые объекты
        violation1 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .ruleId(msg)
                .description(null, msg)
                .build();
        violation2 = new DomainViolation.Builder().message(null, msg)
                .addViolationItem(item1).addViolationItem(item2)
                .ruleId(msg)
                .description(null, msg)
                .build();
        exception = new XBusinessLogicException.Builder<>(null, "message")
                .addViolation(violation1)
                .addViolation(violation2)
                .build();
        Assert.assertEquals(1, exception.getViolations().size());
    }

    /**
     * JXFW-871 улучшения XBusinessLogicException.
     */
    @Test
    public void compareViolationsShouldBeDifferent() {
        DomainViolation violation1 = new DomainViolation();
        DomainViolation violation2 = new DomainViolation();

        DomainViolationItem item1 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id1")),
                "prop1"
        );
        DomainViolationItem item2 = new DomainViolationItem(
                new DomainObjectIdentity(new TestObject("id2")),
                "prop2"
        );

        violation1 = new DomainViolation.Builder()
                .addViolationItem(item1).build();

        Assert.assertNotEquals(violation1, violation2);

        violation2 = new DomainViolation.Builder()
                .addViolationItem(item2).build();

        Assert.assertNotEquals(violation1, violation2);

        violation1 = new DomainViolation.Builder()
                .addViolationItem(item1).addViolationItem(item2).build();
        violation2 = new DomainViolation.Builder()
                .addViolationItem(item2).addViolationItem(item1).build();

        Assert.assertEquals(violation1, violation2);
    }


    @Test
    public void testXEception() throws Exception {
        XException ex = new XException.Builder<>("bundleCode", "defaultMessage")
                .cause(new RuntimeException("runtimeException"))
                .containsUserDescription(true)
                .helpLink("helpLink")
                .sourceLogEntryUniqueId("sourceLogEntryUniqueId")
                .build();
        String json = mapper.writeValueAsString(new XExceptionTo(ex, resolver, Locale.getDefault()));

        System.out.println(json);

        Assert.assertEquals(JsonPath.read(json, "$.$isException"), true);
        Assert.assertEquals(JsonPath.read(json, "$.$className"), "XException");
        Assert.assertEquals(JsonPath.read(json, "$.source"), "Croc.JXFW.Server");
        Assert.assertEquals(JsonPath.read(json, "$.message"), "defaultMessage");
        Assert.assertEquals(JsonPath.read(json, "$.containsUserDescription"), true);
        Assert.assertEquals(JsonPath.read(json, "$.helpLink"), "helpLink");
        Assert.assertEquals(JsonPath.read(json, "$.sourceLogEntryUniqueId"), "sourceLogEntryUniqueId");
        Assert.assertEquals(JsonPath.read(json, "$.innerException"), "runtimeException");
        Assert.assertNotNull(JsonPath.read(json, "$.sourceMachineName"));
        Assert.assertNotNull(JsonPath.read(json, "$.stackTrace"));


    }

    @Test
    public void testXOptimisticConcurrencyException() throws Exception {
        List<DomainTo> obsoleteObjects = new ArrayList<>();
        List<DomainTo> deletedObjects = new ArrayList<>();
        DomainTo dto = new DomainTo();
        dto.setType("type1");
        dto.setId("id1");
        dto.addProperty("prop1", "val1");
        obsoleteObjects.add(dto);
        dto = new DomainTo();
        dto.setType("type2");
        dto.setId("id2");
        dto.addProperty("prop2", "val2");
        obsoleteObjects.add(dto);

        dto = new DomainTo();
        dto.setType("type3");
        dto.setId("id3");
        dto.addProperty("prop3", "val3");
        deletedObjects.add(dto);

        dto = new DomainTo();
        dto.setType("type4");
        dto.setId("id4");
        dto.addProperty("prop4", "val4");
        deletedObjects.add(dto);


        XOptimisticConcurrencyException ex = new XOptimisticConcurrencyException("bundleCode", "defaultMessage",
                Collections.emptySet(), obsoleteObjects, deletedObjects);
        String json = mapper.writeValueAsString(new XOptimisticConcurrencyExceptionTo(ex, resolver, Locale.getDefault()));

        System.out.println(json);

        Assert.assertEquals(JsonPath.read(json, "$.$className"), "XOptimisticConcurrencyException");
        Assert.assertEquals(JsonPath.read(json, "$.$isException"), true);
        Assert.assertEquals(JsonPath.read(json, "$.source"), "Croc.JXFW.Server");
        Assert.assertEquals(JsonPath.read(json, "$.message"), "defaultMessage");
        Assert.assertEquals(JsonPath.read(json, "$.containsUserDescription"), false);
        Assert.assertNull(JsonPath.read(json, "$.helpLink"));
        Assert.assertNull(JsonPath.read(json, "$.sourceLogEntryUniqueId"));
        Assert.assertNull(JsonPath.read(json, "$.innerException"));
        Assert.assertNotNull(JsonPath.read(json, "$.sourceMachineName"));
        Assert.assertNotNull(JsonPath.read(json, "$.stackTrace"));
        Assert.assertEquals(JsonPath.read(json, "$.obsoleteObjects[0].type"), "type1");
        Assert.assertEquals(JsonPath.read(json, "$.obsoleteObjects[0].id"), "id1");
        Assert.assertEquals(JsonPath.read(json, "$.obsoleteObjects[1].type"), "type2");
        Assert.assertEquals(JsonPath.read(json, "$.obsoleteObjects[1].id"), "id2");
        Assert.assertEquals(JsonPath.read(json, "$.deletedObjects[0].type"), "type3");
        Assert.assertEquals(JsonPath.read(json, "$.deletedObjects[0].id"), "id3");
        Assert.assertEquals(JsonPath.read(json, "$.deletedObjects[1].type"), "type4");
        Assert.assertEquals(JsonPath.read(json, "$.deletedObjects[1].id"), "id4");


    }

    @Test
    public void testXInvalidDataException() throws Exception {
        XInvalidDataException ex = new XInvalidDataException.Builder("bundleCode", "defaultMessage")
        .identity("typeName", "id").build();
        String json = mapper.writeValueAsString(new XInvalidDataExceptionTo(ex, resolver, Locale.getDefault()));

        System.out.println(json);

        Assert.assertEquals(JsonPath.read(json, "$.$className"), "XInvalidDataException");
        Assert.assertEquals(JsonPath.read(json, "$.$isException"), true);
        Assert.assertEquals(JsonPath.read(json, "$.source"), "Croc.JXFW.Server");
        Assert.assertEquals(JsonPath.read(json, "$.message"), "defaultMessage");
        Assert.assertEquals(JsonPath.read(json, "$.containsUserDescription"), false);
        Assert.assertNull(JsonPath.read(json, "$.helpLink"));
        Assert.assertNull(JsonPath.read(json, "$.sourceLogEntryUniqueId"));
        Assert.assertNull(JsonPath.read(json, "$.innerException"));
        Assert.assertNotNull(JsonPath.read(json, "$.sourceMachineName"));
        Assert.assertNotNull(JsonPath.read(json, "$.stackTrace"));
        Assert.assertEquals(JsonPath.read(json, "$.identities.id"), "id");
        Assert.assertEquals(JsonPath.read(json, "$.identities.type"), "typeName");

    }

    @Test
    public void testXReferenceIntegrityViolationException() throws Exception {
        XReferenceIntegrityViolationException ex = new XReferenceIntegrityViolationException(
                "defaultMessage",
                new RuntimeException("runtimeException"),
                Collections.singleton(
                        new DomainViolation.Builder().message(null, "violationMsg")
                                .reason(DomainViolationReason.INTEGRITY)
                                .ruleId("some rule Id")
                                .description(null, "violationDescr")
                                .ignorable(true)
                                .build()
                ),
                "entityTypeName",
                "navigationPropName",
                "reason");
        String json = mapper.writeValueAsString(new XReferenceIntegrityViolationExceptionTo(ex, resolver, Locale.getDefault()));

        System.out.println(json);

        Assert.assertEquals(JsonPath.read(json, "$.$className"), "XReferenceIntegrityViolationException");
        Assert.assertEquals(JsonPath.read(json, "$.$isException"), true);
        Assert.assertEquals(JsonPath.read(json, "$.source"), "Croc.JXFW.Server");
        Assert.assertEquals(JsonPath.read(json, "$.message"), "defaultMessage");
        Assert.assertEquals(JsonPath.read(json, "$.containsUserDescription"), false);
        Assert.assertNull(JsonPath.read(json, "$.helpLink"));
        Assert.assertNull(JsonPath.read(json, "$.sourceLogEntryUniqueId"));
        Assert.assertEquals(JsonPath.read(json, "$.innerException"),"runtimeException");
        Assert.assertNotNull(JsonPath.read(json, "$.sourceMachineName"));
        Assert.assertNotNull(JsonPath.read(json, "$.stackTrace"));
        Assert.assertEquals(JsonPath.read(json, "$.reason"),"reason");
        Assert.assertEquals(JsonPath.read(json, "$.entityTypeName"),"entityTypeName");
        Assert.assertEquals(JsonPath.read(json, "$.navigationPropName"),"navigationPropName");

    }





}
