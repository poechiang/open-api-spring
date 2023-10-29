package me.jeffrey.open.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodDTO {
    private long id;
    
    
    private String name;
    private long price;
    private String img;
    private String type;
    private String brand;
    private String color;
    private String size;
    private String description;
    private String category;
    private String brandId;
}
