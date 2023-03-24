package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fooddelivery.Service.EmployeeService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/employee")//提前设置前缀，那么http://localhost:8080/employee/login就只要处理login就可以了
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    //如何接受json数据,注意表格的名称需要和实体类的属性名一样
    //request 是用来存入session 保持登陆的
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
//    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        //1. 将密码进行加密
//        String password = employee.getPassword();
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        //2. 根据用户名查询数据库，无用户名返回false
//        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername,employee.getUsername());
//        Employee employee1 = employeeService.getOne(queryWrapper);
//
//        if(employee1 == null){
//            return R.error("登陆失败");
//        }
//        //3. 将密码比对
//        if(employee1.getPassword() != password){
//            return R.error("密码不正确");
//        }
//        //4. 员工有禁用状态，查看员工是否禁用
//        if(employee1.getStatus() == 0){
//            return R.error("员工被禁用");
//        }
//        //5. 登陆成功，将员工存入session
//        request.getSession().setAttribute("employee",employee1.getId());
//        return R.success(employee1);
//    }

}
