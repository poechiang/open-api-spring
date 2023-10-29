package me.jeffrey.open.controller;

import me.jeffrey.open.common.Response;
import me.jeffrey.open.dto.GoodDTO;
import me.jeffrey.open.services.IGoodService;
import me.jeffrey.open.services.impl.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodController {
    
    @Autowired
    private IGoodService goodService;
    @GetMapping("")
    public Response<List<GoodDTO>> selectGoods() {
        return Response.Ok(goodService.selectGoods());
    }
}
