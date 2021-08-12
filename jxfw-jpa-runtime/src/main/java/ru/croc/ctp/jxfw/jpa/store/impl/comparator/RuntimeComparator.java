package ru.croc.ctp.jxfw.jpa.store.impl.comparator;

import static com.google.common.base.Preconditions.checkNotNull;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import javax.persistence.OneToMany;

/**
 * @author Nosov Alexander
 * @since 1.0
 */
public class RuntimeComparator implements Comparator<DomainObject<?>> {

    @Override
    public int compare(final DomainObject<?> o1, final DomainObject<?> o2) {
        checkNotNull(o1);
        checkNotNull(o2);

        @SuppressWarnings("rawtypes")
        final Class<? extends DomainObject> class1 = o1.getClass();
        @SuppressWarnings("rawtypes")
        final Class<? extends DomainObject> class2 = o2.getClass();

        if (class1 == class2) {
            final Field[] fields = class1.getDeclaredFields();

            for (final Field field : fields) {
                final boolean isPresentToMany =
                        field.isAnnotationPresent(OneToMany.class);

                if (isPresentToMany) {
                    final Class<?> genericType = field.getDeclaringClass();

                    if (genericType == class2) {
                        try {
                            field.setAccessible(true);

                            final Object valueOf1 = field.get(o1);
                            final Object valueOf2 = field.get(o2);
                            
                            if (isNullOrEmpty(valueOf1) && isNullOrEmpty(valueOf2)) {
                                return 0;
                            }
                            final Object value = field.get(o1);
                            if (!isNullOrEmpty(value)) {
                                return -1;
                            } else {
                                return 1;
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private boolean isNullOrEmpty(Object value) {
        if (value == null) {
            return true;
        }
        
        if (value instanceof Collection<?>) {
            return ((Collection<?>)value).isEmpty();
        }
        
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>)value).isEmpty();
        }
        
        return false;
    }
}
