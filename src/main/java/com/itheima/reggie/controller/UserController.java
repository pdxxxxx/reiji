package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @create 2023-03-13 21:13
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        if(!StringUtils.isEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);

            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
//            session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }
        return R.error("手机验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInsession= redisTemplate.opsForValue().get(phone);
//        Object codeInsession = session.getAttribute(phone);

        if (codeInsession != null && codeInsession.equals(code)){
            redisTemplate.delete(phone);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);

            if(user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);

        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("成功退出");
    }
}
