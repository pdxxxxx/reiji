package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @author
 * @create 2023-03-06 19:02
 */
public interface DishService extends IService<Dish> {
    public void saveWithDishFlavor(DishDto dishDto);

   public DishDto getByIdWithFlavors(Long id);

   public void updateWithDishFlavor(DishDto dishDto);
}
