package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Интерфейс сервисов трансформации DTO-объектов в доменные объекты и обратно.
 *
 * @param <T>  Тип доменного объекта
 * @author Nosov Alexander
 * @since 1.3
 */
public interface DomainToMapper<T extends DomainObject<?>> {
	//TODO: все объекты в / из которых происходит преобразование, должны быть доменными <T extends DomainObject<?>> {
    
	/**
     * Преобразует переданных доменный объект в DTO.
     *
     * @param domainObject Доменный объект, который нужно преобразовать в DTO
     * @param expand Массивные ссылочные свойства, которые необходимо добавить при преобразовании в DTO
     * @return DTO доменного объекта
     */
    DomainTo toTo(T domainObject, String... expand);

    /**
     * Находит доменный сервис по типу переданного объекта и выполняет преобразование в DTO.
     *
     * @param domainObject Доменный объект, который нужно преобразовать в DTO
     * @param expand Массивные ссылочные свойства, которые необходимо добавить при преобразовании в DTO
     * @return DTO соответсвующий переданному доменному объекту
     */
    List<DomainTo> toTo(Iterable<T> domainObject, String... expand);

    /**
     * Конвертирует DTO в доменных объект
     *
     * @param vo      Объект DTO, который нужно конвертировать
     * @param context Контекст конвертации
     * @return Сконвертированный объект. Он же добавлен в context.objects.
     */
    T fromTo(@Nonnull DomainTo vo,@Nonnull  ConvertContext context);
    /**
     * Конвертирует DTO в доменных объект
     *
     * @param domainObject экземпляр доменного объекта, в который будет устанавливтаь свойства из DTO
     * @param vo      Объект DTO, который нужно конвертировать
     * @param context Контекст конвертации
     * @return Сконвертированный объект. Он же добавлен в context.objects.
     */
    T fromTo(@Nonnull T domainObject, @Nonnull DomainTo vo,@Nonnull ConvertContext context);

}
