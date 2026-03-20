package edu.eci.dosw.DOSW_Library.core.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorUtil {
    private final AtomicLong sequence;

    public IdGeneratorUtil() {
        this(1L);
    }

    public IdGeneratorUtil(Long initialValue) {
        this.sequence = new AtomicLong(initialValue);
    }

    public Long nextId() {
        return sequence.getAndIncrement();
    }
}
