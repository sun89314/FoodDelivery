package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fooddelivery.Service.UserService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    private R<User> login(@RequestBody Map user, HttpSession session) {
        System.out.println(user.get("phone"));

        String phoneNumber = user.get("phone").toString();
        String code = user.get("code").toString();
        if(code != null && code.equals(code)){
            // 登陆
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phoneNumber);
            User one = userService.getOne(wrapper);
            //判断是否是新用户
            if(one == null){
                one = new User();
                one.setPhone(phoneNumber);
                userService.save(one);
            }
//保存用户信息到session
            session.setAttribute("user",one.getId());
            return R.success(one);
        }
        return R.error("fail");
    }
}
