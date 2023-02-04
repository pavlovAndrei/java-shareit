package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    private final int offset;

    public CustomPageRequest(Integer offset, Integer size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    public static CustomPageRequest of(int offset, int size) {
        return new CustomPageRequest(offset, size, Sort.unsorted());
    }

//    @Override
//    public long getOffset() {
//        return offset;
//    }
}
