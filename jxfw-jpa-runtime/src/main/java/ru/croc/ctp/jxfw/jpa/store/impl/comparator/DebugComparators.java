package ru.croc.ctp.jxfw.jpa.store.impl.comparator;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.util.Collection;
import java.util.Comparator;

/**
 * Реализация проверки работы компаратора доменных объектов
 *  
 * @since 1.5
 * @author AKogun
 */
public final class DebugComparators {
    
    /**
     * Prevent construction.
     */
    private DebugComparators() {
    }

    /**
     * Verify that a comparator is transitive.
     *
     * @param <T>        the type being compared
     * @param <K>        the type of elements
     * @param comparator the comparator to test
     * @param elements   the elements to test against
     * @throws AssertionError if the comparator is not transitive
     */
    public static <T extends DomainObject<?>, K extends T> void verifyTransitivity(Comparator<T> comparator,
            Collection<K> elements) {
        for (T first : elements) {
            for (T second : elements) {
                int result1 = comparator.compare(first, second);
                int result2 = comparator.compare(second, first);
                if (result1 == 0 && result2 == 0) {
                    continue;
                } else if (result1 != -result2) {
                    // Uncomment the following line to step through the failed case
                    // comparator.compare(first, second);
                    throw new AssertionError("compare(" + first + ", " + second + ") == " + result1
                            + " but swapping the parameters returns " + result2);
                }
            }
        }
        for (T first : elements) {
            for (T second : elements) {
                int firstGreaterThanSecond = comparator.compare(first, second);
                if (firstGreaterThanSecond <= 0) {
                    continue;
                }
                for (T third : elements) {
                    int secondGreaterThanThird = comparator.compare(second, third);
                    if (secondGreaterThanThird <= 0) {
                        continue;
                    }
                    int firstGreaterThanThird = comparator.compare(first, third);
                    if (firstGreaterThanThird <= 0) {
                        // Uncomment the following line to step through the failed case
                        // comparator.compare(first, third);
                        throw new AssertionError("compare(" + first + ", " + second + ") > 0, " + "compare(" + second
                                + ", " + third + ") > 0, but compare(" + first + ", " + third + ") == "
                                + firstGreaterThanThird);
                    }
                }
            }
        }
    }
}