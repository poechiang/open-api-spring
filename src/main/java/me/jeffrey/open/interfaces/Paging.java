package me.jeffrey.open.interfaces;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Paging {
    private long pageIndex;
    private int pageSize;
    private long total;
}
