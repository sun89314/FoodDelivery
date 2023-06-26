package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.example.mybatisplustest.common.R;
//import com.example.mybatisplustest.entity.Employee;
//import com.example.mybatisplustest.service.EmployeeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.EmployeeService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    public EmployeeService employeeService;

    /**
     * Employee Login, according to username and password
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        //1、Encrypt the password submitted by the page
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        //2、query the database according to the username submitted by the page
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        //3、If no query is returned, the login fails
        //4、Password comparison, if inconsistent, return login failure result
        if(emp == null || !emp.getPassword().equals(password)){
            return R.error("Login failed");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        //5、Check the employee status. If it is disabled, return the employee is disabled
        if(emp.getStatus() == 0){
            return R.error("The employee is disabled");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * Logout
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        //clear the id of the currently logged in employee saved in the Session
        request.getSession().removeAttribute("employee");
        return R.success("Logout successfully");
    }

    /**
     * Get all employees
     * @param request
     * @param emp
     * @return
     */
    @PostMapping()
    public R<String> saveUser(HttpServletRequest request,@RequestBody Employee emp){
        log.info("New Employee：{}",emp.toString());
        //Set initial password, time and other content
        String password = "111111";
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        emp.setPassword(password);
        employeeService.save(emp);
        return R.success("New employee successfully");
    }

    /**
     * using page object to get all employees
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> getPage(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //Create Page object
        Page pageinfo = new Page(page,pageSize);
        //query all the employees, and sort by update time to get the latest employee
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //use the page object to get the employees
        employeeService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * Edit employee information according to id
     * @param employee
     * @return
     */
    @PutMapping ()
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        employeeService.update();
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("Update employee successfully");
    }

    @GetMapping("/{id}")
    public R<Employee> getUserById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if(emp != null){
            return R.success(emp);
        }
        return R.error("The employee does not exist");
    }
}
