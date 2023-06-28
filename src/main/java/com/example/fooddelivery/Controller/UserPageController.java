package com.example.fooddelivery.Controller;

import com.example.fooddelivery.Service.UserService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * UserPageController
 *
 * @author letingsun
 * @since 6/27/23
 */
@RestController
@RequestMapping("/userPage")
@Slf4j
public class UserPageController {
    @Autowired
    UserService userService;
//    @GetMapping("/getUser")
//    public R<User> getUser(HttpSession session) {
//        Long userId = (Long) session.getAttribute("user");
//        User user = userService.getById(userId);
//        return R.success(user);
//    }
}
