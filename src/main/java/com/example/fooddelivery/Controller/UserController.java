package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fooddelivery.Service.UserService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.utils.SMSUtils;
import com.example.fooddelivery.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Map user, HttpSession session){
        //获取手机号
        String phone = user.get("phone").toString();
        String code = user.get("code").toString();


        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码

//            session.setAttribute("code",code);
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            System.out.println("code="+code);
            //调用阿里云提供的短信服务API完成发送短信
            try {
                SMSUtils.sendMsg(phone,code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    private R<User> login(@RequestBody Map user, HttpSession session) {

        String phoneNumber = user.get("phone").toString();
        String code = user.get("code").toString();
//        String realCode = (String) session.getAttribute("code");
        String realCode = (String) redisTemplate.opsForValue().get(phoneNumber);
        if(code != null && code.equals(realCode)){
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
            System.out.println("user="+phoneNumber+" 登录成功");
            redisTemplate.delete(phoneNumber);
            return R.success(one);
        }
        return R.error("fail");
    }
}
