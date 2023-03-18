package me.jeffrey.open.interfaces;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ListResponse<T> extends Response<T> {
  private Paging paging;
}
