package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;

/**
 * @author
 * @create 2023-03-06 18:07
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id) throws CustomException;
}
