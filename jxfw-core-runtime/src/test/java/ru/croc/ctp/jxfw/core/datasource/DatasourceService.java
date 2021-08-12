package ru.croc.ctp.jxfw.core.datasource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DatasourceService extends DataSourcesBase {

    public DataSourceResult m1(Integer a1) {
        return new GeneralDataSourceResult(Arrays.asList(a1 + a1), Collections.emptyList(), new HashMap<>());
    }

    public DataSourceResult m1(Integer a1, Integer a2) {
        return new GeneralDataSourceResult(Arrays.asList(a1 * a1), Collections.emptyList(), new HashMap<>());
    }
}
