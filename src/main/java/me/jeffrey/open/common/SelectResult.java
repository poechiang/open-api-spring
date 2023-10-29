package me.jeffrey.open.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public
class SelectResult<T> {
  private List<T> list = new ArrayList<>();
    private long total=0;
}
