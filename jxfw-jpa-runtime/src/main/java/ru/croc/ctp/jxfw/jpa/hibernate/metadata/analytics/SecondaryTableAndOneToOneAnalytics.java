package ru.croc.ctp.jxfw.jpa.hibernate.metadata.analytics;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.metamodel.runtime.analitycs.XfwClassAnalytics;

import javax.persistence.Inheritance;
import javax.persistence.OneToOne;
import javax.persistence.SecondaryTable;

/**
 * Поиск в метаданных такого случая:
 * Имеем двунаправленную @OneToOne с внешним ключом в сущности, которая входит в иерархию наследования.
 * Иерархия построена со стратегией SINGLE_TABLE и использованием SecondaryTable
 * as suggested in "Java Persistence With Hibernate" section 6.5.
 * При попытке сохранить сущности в естественном порядке, т.е. сначала ту, у которой нет внешнего ключа
 * получаем ошибку org.hibernate.PropertyValueException: not-null property references a null or transient value
 * https://forum.hibernate.org/viewtopic.php?f=1&t=984204
 *
 * @since 1.6
 * @author OKrutova
 */
@Service
public class SecondaryTableAndOneToOneAnalytics implements XfwClassAnalytics {


    private final Logger logger = LoggerFactory.getLogger(XfwClassAnalytics.class);

    private final JpaContext jpaContext;

    /**
     * Конструктор.
     *
     * @param jpaContext jpaContext
     */
    public SecondaryTableAndOneToOneAnalytics(JpaContext jpaContext) {
        this.jpaContext = jpaContext;
    }

    @Override
    public void analyze(final XfwClass xfwClass) {


        for (XfwReference xfwReference : xfwClass.getEReferences()) {
            XfwAnnotation oneToOne = xfwReference.getEAnnotation(XFWConstants.getUri(OneToOne.class
                    .getSimpleName()));
            if (oneToOne != null
                    && oneToOne.getDetails().get("mappedBy") != null
                    && !oneToOne.getDetails().get("mappedBy").isEmpty()
                    && xfwReference.getEOpposite() != null
                    && xfwReference.getEOpposite().getEContainingClass().getEAnnotation(
                    XFWConstants.getUri(SecondaryTable.class.getSimpleName())) != null) {

                XfwClass secondaryClass = xfwReference.getEOpposite().getEContainingClass();
                for (XfwClass clazz : secondaryClass.getEAllSuperTypes()) {
                    XfwAnnotation inheritance = clazz.getEAnnotation(
                            XFWConstants.getUri(Inheritance.class.getSimpleName()));
                    if ("SINGLE_TABLE".equals(inheritance.getDetails().get("strategy"))) {
                        logger.warn("Entity {} property {} Имеем двунаправленную @OneToOne с внешним ключом в "
                                        + "сущности, которая входит в иерархию наследования."
                                        + " Иерархия построена со стратегией SINGLE_TABLE и использованием "
                                        + "SecondaryTable. Это потенциальная проблема при сохранении. see "
                                        + "JXFW-1191",
                                xfwClass.getName(), xfwReference.getName());

                        modify(xfwClass, xfwReference);

                    }
                }

            }

        }

    }

    private void modify(XfwClass xfwClass, XfwReference xfwReference) {
        Class entityClass = xfwClass.getInstanceClass();
        if (entityClass != null) {
            SessionFactory sessionFactory = jpaContext
                    .getEntityManagerByManagedType(entityClass).getEntityManagerFactory()
                    .unwrap(SessionFactory.class);

            ClassMetadata classMetadata = sessionFactory
                    .getClassMetadata(xfwClass.getInstanceClassName());
            if (classMetadata instanceof AbstractEntityPersister) {
                AbstractEntityPersister persister = (AbstractEntityPersister) classMetadata;
                String[] propertyNames = persister.getEntityMetamodel().getPropertyNames();
                boolean[] nullabilities = persister.getEntityMetamodel().getPropertyNullability();

                for (int i = 0; i < propertyNames.length; i++) {
                    String propertyName = propertyNames[i];
                    if (propertyName.equals(xfwReference.getName())) {
                        nullabilities[i] = true;
                        logger.info("Entity {} property {} nullability set to true",
                                xfwClass.getName(), xfwReference.getName());
                    }
                }
            }
        }
    }
}
