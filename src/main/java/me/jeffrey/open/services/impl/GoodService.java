package me.jeffrey.open.services.impl;

import me.jeffrey.open.dto.GoodDTO;
import me.jeffrey.open.mapper.GoodMapper;
import me.jeffrey.open.services.IGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodService implements IGoodService {
    
    @Autowired
    private GoodMapper goodMapper;
    @Override
    public List<GoodDTO> selectGoods() {
        return goodMapper.selectGoods();
    }
}
