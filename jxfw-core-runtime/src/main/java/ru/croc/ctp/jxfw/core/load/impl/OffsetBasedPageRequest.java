package ru.croc.ctp.jxfw.core.load.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * Created by OKrutova on 17.03.2017.
 */
public class OffsetBasedPageRequest implements Pageable, Serializable {

    private static final long serialVersionUID = -25822477129613575L;

    private int limit;
    private int offset;
    private final Sort sort;

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be {@literal null}.
     */
    public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }


    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof OffsetBasedPageRequest)) {
            return false;
        }

        OffsetBasedPageRequest other = (OffsetBasedPageRequest) object;
        boolean sortEqual = this.sort == null ? other.sort == null : this.sort.equals(other.sort);
        return sortEqual && this.limit == other.limit && this.offset == other.offset;

    }

    @Override
    public int hashCode() {
        byte result = 1;
        int result1 = 31 * result + limit;
        result1 = 31 * result1 + offset;
        result1 = 31 * result1 + (null == sort ? 0 : sort.hashCode());
        return result1;
    }

    @Override
    public String toString() {
        return String.format("OffsetBasedPageRequest [limit: %d, offset %d, sort: %s]",
                limit, offset, sort == null ? null : sort.toString());
    }
}
