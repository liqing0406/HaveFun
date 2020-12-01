package com.hebtu.havefun.controller;

import com.hebtu.havefun.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/19 11:15
 * @projectName HaveFun
 * @className UserController.java
 * @description TODO
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    UserService userService;

    @RequestMapping("/judgeRegistered")
    public String judgeRegistered(String phoneNum) {
        if (userService.judgeRegistered(phoneNum)) {
            return "false";
        } else {
            return "true";
        }
    }

    @RequestMapping("/register")
    public String register(String phoneNum, String password) {
        if (userService.register(phoneNum, password)) {
            return "true";
        } else {
            return "false";
        }
    }

    @RequestMapping("/login")
    public String login(String phoneNum, String password) {
        String user = userService.login(phoneNum, password);
        return "".equals(user) ? "" : user;
    }

    @RequestMapping("/modifyPassword")
    public String modifyPassword(String phoneNum, String password) {
        if (userService.modifyPassword(phoneNum, password)) {
            return "true";
        } else {
            return "false";
        }
    }

    @RequestMapping("/enrollActivity")
    public String enrollActivity(Integer activityId, Integer id) {
        return userService.enrollActivity(activityId, id);
    }

    @RequestMapping("/modifyPersonalSignature")
    public String modifyPersonalSignature(Integer id ,String personalSignature){
        return userService.modifyPersonalSignature(id,personalSignature) ? "true" : "false";
    }
}