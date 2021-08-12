package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataRow;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.DetailDataClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyValueEvaluatorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.FormattersClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDataClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDataFragmentClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.MasterDetailLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ObjectFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MasterDetailLayoutTest {

    @Test
    public void wrapParamColumnName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MasterDetailLayout layout = new MasterDetailLayout();

        Method method = MasterDetailLayout.class.getDeclaredMethod("wrapParamColumnName", DetailDataClass.ParamColumnClass.class);
        method.setAccessible(true);

        DetailDataClass.ParamColumnClass paramColumnClass = new DetailDataClass.ParamColumnClass();
        paramColumnClass.setName("vasya");
        Object result = method.invoke(layout, paramColumnClass);

        assertTrue("{#vasya}".equals(result));
    }

    @Test
    public void initFakeParam() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MasterDetailLayout layout = new MasterDetailLayout();

        Method method = MasterDetailLayout.class.getDeclaredMethod("initFakeParam", String.class, Object.class);
        method.setAccessible(true);

        ReportParams.ReportParam result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", new Integer(5));
        assertEquals(VarTypesClass.I_4.value(), result.getProfile().getVt().value());

        result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", 5D);
        assertEquals(VarTypesClass.R_8.value(), result.getProfile().getVt().value());

        result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", new BigDecimal(3));
        assertEquals(VarTypesClass.FIXED_14_4.value(), result.getProfile().getVt().value());

        result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", Boolean.FALSE);
        assertEquals(VarTypesClass.BOOLEAN.value(), result.getProfile().getVt().value());

        result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", UUID.randomUUID());
        assertEquals(VarTypesClass.UUID.value(), result.getProfile().getVt().value());

        result = (ReportParams.ReportParam) method.invoke(layout, "emptyName", Date.from(Instant.now()));
        assertEquals(VarTypesClass.DATE_TIME_TZ.value(), result.getProfile().getVt().value());
    }

    @Test
    public void writeMasterDataIReportFormat() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MasterDetailLayout layout = new MasterDetailLayout();

        Method method = MasterDetailLayout.class.getDeclaredMethod(
                "writeMasterData",
                IDataRow.class,
                ReportLayoutData.class,
                MasterDetailLayoutClass.class,
                MasterDataClass.class,
                int.class
        );
        method.setAccessible(true);

        ReportLayoutData reportLayoutData = mock(ReportLayoutData.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(reportLayoutData.getRepGen()).thenReturn(new XslFoProfileWriter(outputStream, null));
        MasterDataClass masterDataClass = new MasterDataClass();
        ArrayList<MasterDataFragmentClass> masterDataFragment = new ArrayList<>();
        MasterDataFragmentClass fragmentClass = new MasterDataFragmentClass();
        fragmentClass.setValue("fRaGmEnT");
        masterDataFragment.add(fragmentClass);
        FormattersClass formattersClass = new FormattersClass();
        ArrayList<JAXBElement<? extends AbstractFormatterClass>> jaxbElements = new ArrayList<>();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EmptyValueEvaluatorClass> jaxbElement = objectFactory.createEmptyValueEvaluator(new EmptyValueEvaluatorClass());
        jaxbElements.add(jaxbElement);
        formattersClass.setAbstractFormatter(jaxbElements);
        fragmentClass.setFormatters(formattersClass);
        masterDataClass.setMasterDataFragment(masterDataFragment);
        method.invoke(
                layout,
                mock(IDataRow.class),
                reportLayoutData,
                null,
                masterDataClass,
                3
        );

        String result = outputStream.toString();
        System.err.println(result);

        assertTrue(result.contains("fRaGmEnT"));
    }
}
