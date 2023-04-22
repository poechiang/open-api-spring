package me.jeffrey.open.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Paging {
    private long page;
    private int pageSize;
    private long total;
}
