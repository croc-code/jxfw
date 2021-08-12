package ru.croc.ctp.jxfw.jpa.hibernate.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Данные о кросс-таблице в БД.
 * Предполагается, что на ней висят два FK, каждый из которых ведет к таблице доменного объекта.
 * Если FK не два, то данная структура не работоспособна.
 *
 * @author OKrutova
 * @since 1.6
 */
class XfwCrossTable {

    private static final Logger logger = LoggerFactory.getLogger(XfwCrossTable.class);

    private final List<XfwTable.XfwConstraint> constraints = new ArrayList<>();
    private final String name;


    /**
     * Конструктор.
     * @param name имя таблицы
     */
    XfwCrossTable(String name) {
        this.name = name;
    }

    /**
     * Добавить FK.
     * @param constraint FK
     */
    void addConstraint(XfwTable.XfwConstraint constraint) {
        if (!constraint.isExported()) {
            logger.info("Attempt to add imported constraint to cross-table {} {}", name, constraint.getName());
        }
        constraints.add(constraint);
    }

    /**
     * Получить один из двух FK.
     * @param ind 0 или 1
     * @return FK или null, если в данной таблицы не два FK
     */
    XfwTable.XfwConstraint getConstraint(int ind) {
        if (ind != 0 && ind != 1) {
            throw new IllegalArgumentException("ind=" + ind);
        }
        if (constraints.size() == 2) {
            return constraints.get(ind);
        } else {
            logger.info("Cross-table must contain 2 constraints {}", name, constraints.size());
            return null;
        }
    }

    String getName() {
        return name;
    }
}
