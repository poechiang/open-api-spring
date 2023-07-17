package me.jeffrey.open.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Paging {
    private long page;
    private int pageSize;
    private long total;
    
    public Paging(long page,int pageSize){
        this.page = page;
        this.pageSize = pageSize;
    }
}
