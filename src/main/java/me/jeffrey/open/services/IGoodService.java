package me.jeffrey.open.services;

import me.jeffrey.open.dto.GoodDTO;

import java.util.List;

public interface IGoodService {
    
    public List<GoodDTO> selectGoods();
}
