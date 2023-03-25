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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

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

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param emp
     * @return
     */
    @PostMapping()
    public R<String> saveUser(HttpServletRequest request,@RequestBody Employee emp){
        log.info("新增员工：{}",emp.toString());
        //设置初始密码,时间等内容
        String password = "111111";
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        emp.setPassword(password);
//        emp.setCreateTime(LocalDateTime.now());
//        emp.setUpdateTime(LocalDateTime.now());
//        emp.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        emp.setUpdateUser((Long) request.getSession().getAttribute("employee"));

//        try {
//            employeeService.save(emp);
//        } catch (Exception e) {
//            return R.error("添加用户失败");
//        }
        employeeService.save(emp);
        return R.success("新增员工成功");
    }

    /**
     * 分页处理器，使用page对象
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> getPage(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //构造分页构造器
        Page pageinfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //查询
        employeeService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 根据id来修改员工信息
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
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getUserById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if(emp != null){
            return R.success(emp);
        }
        return R.error("没有查询到对应员工信息");
    }
}
