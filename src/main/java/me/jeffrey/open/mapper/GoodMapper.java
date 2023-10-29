package me.jeffrey.open.mapper;

import me.jeffrey.open.dto.GoodDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodMapper {
    public List<GoodDTO> selectGoods();
}
