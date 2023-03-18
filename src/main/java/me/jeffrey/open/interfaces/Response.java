package me.jeffrey.open.interfaces;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Response<T> {
    private int status;
    private T payload;
}
