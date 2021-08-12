package ru.croc.ctp.jxfw.jpa.hibernate.metadata;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Данные о таблице в БД, соответсвующем ей доменном объекте,
 * констрейнтах на этой таблице.
 *
 * @author OKrutova
 * @since 1.6
 */
class XfwTable {

    /**
     * Метаданные класса.
     */
    private XfwClass xfwClass;

    private final String tableName;

    private final String dbTableName;

    private final Map<String, XfwStructuralFeature> columnsToFields;

    private final Map<String, String> joinTableToColumn;


    private final Map<String, XfwConstraint> constraints = new HashMap<>();


    /**
     * Конструктор.
     *
     * @param xfwClass          метаданные доменного объекта
     * @param tableName         имя таблицы в метаданных Hibernate
     * @param dbTableName       имя таблицы в БД
     * @param columnsToFields   соответствие столбцов в БД и метаданных полей ДО
     * @param joinTableToColumn соответствие имен кросс-таблиц и столбцов БД
     */
    XfwTable(XfwClass xfwClass, String tableName, String dbTableName, Map<String, XfwStructuralFeature> columnsToFields,
             Map<String, String> joinTableToColumn) {
        this.xfwClass = xfwClass;
        this.columnsToFields = columnsToFields;
        this.tableName = tableName;
        this.dbTableName = dbTableName;
        this.joinTableToColumn = joinTableToColumn;
    }

    XfwClass getXfwClass() {
        return xfwClass;
    }


    public void setXfwClass(XfwClass xfwClass) {
        this.xfwClass = xfwClass;
    }


    public Map<String, String> getJoinTableToColumn() {
        return joinTableToColumn;
    }

    /**
     * Имя таблицы в представлении метаданных Hibernate.
     *
     * @return Имя таблицы в представлении метаданных Hibernate.
     */
    String getTableName() {
        return tableName;
    }


    /**
     * Имя таблицы в представлении БД. Требуется, чтобы запросить из БД информацию об индексах.
     * см. XfwMetadataHolder.buildIndexConstraintData, JXFW-1522
     *
     * @return Имя таблицы в представлении БL
     */
    String getDbTableName() {
        return dbTableName;
    }

    Map<String, XfwStructuralFeature> getColumnsToFields() {
        return columnsToFields;
    }

    Map<String, XfwConstraint> getConstraints() {
        return constraints;
    }


    /**
     * Добавляет в таблицу FK из кросс-таблицы.
     *
     * @param constraint     FK
     * @param crossTableName имя кросс-таблицы
     * @return FK
     */
    XfwConstraint importCrossTableConstraint(XfwConstraint constraint, String crossTableName) {
        XfwConstraint xfwConstraint = new XfwConstraint(false, null, constraint.getName());
        if (joinTableToColumn.get(crossTableName) != null) {
            xfwConstraint.columns.add(joinTableToColumn.get(crossTableName));
        }
        constraints.put(constraint.getName(), xfwConstraint);
        return xfwConstraint;
    }


    /**
     * Данные о констренйте БД.
     */
    class XfwConstraint {


        private final boolean exported;

        private final String oppositeTableName;

        private final String name;
        /**
         * Колонки, на которые наложен констрейнт.
         */
        private final Set<String> columns = new HashSet<>();

        /**
         * Конструктор.
         *
         * @param exported          признак экспортируемый\импортируемый констрейнт.
         * @param oppositeTableName для FK - имя таблицы на противоположной стороне связи
         * @param name              имя констренйта
         */
        XfwConstraint(boolean exported, String oppositeTableName, String name) {
            this.exported = exported;
            this.oppositeTableName = oppositeTableName;
            this.name = name;
        }


        Set<String> getColumns() {
            return columns;
        }


        boolean isExported() {
            return exported;
        }


        String getOppositeTableName() {
            return oppositeTableName;
        }

        String getName() {
            return name;
        }

        XfwTable getTable() {
            return XfwTable.this;
        }
    }

}
