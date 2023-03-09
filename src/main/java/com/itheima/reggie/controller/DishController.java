package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author
 * @create 2023-03-06 22:40
 */

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithDishFlavor(dishDto);
        return R.success("添加菜品成功!");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageinfo=new Page<>(page,pageSize);

        Page<DishDto> pagedto=new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishLambdaQueryWrapper.like(!Objects.isNull(name),Dish::getName,name);
        dishService.page(pageinfo,dishLambdaQueryWrapper);

        BeanUtils.copyProperties(pageinfo,pagedto,"records");
        List<DishDto> list = pageinfo.getRecords().stream().map(item -> {
            Long categoryId = item.getCategoryId();
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        pagedto.setRecords(list);
        return R.success(pagedto);
    }

    @GetMapping("/{id}")
    public R<DishDto> getByid(@PathVariable Long id){
        DishDto dishDto=dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithDishFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,String[] ids){
        for(String id :ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("状态修改成功");
    }

    @DeleteMapping
    public R<String> delete(String[] ids){
        for (String id:ids){
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Dish>>  list(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        return R.success(list);
    }
}
