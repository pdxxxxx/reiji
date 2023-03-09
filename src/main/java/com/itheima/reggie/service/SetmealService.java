package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;


/**
 * @author
 * @create 2023-03-06 19:04
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

   public void removeWithDish(List<Long> ids);

   public SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
