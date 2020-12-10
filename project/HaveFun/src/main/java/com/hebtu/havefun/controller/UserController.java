package com.hebtu.havefun.controller;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * @param phoneNum 电话号码
     * @return 返回"true"或者"false"
     * @description 判断是否注册
     */
    @RequestMapping("/judgeRegistered")
    public String judgeRegistered(String phoneNum) {
        if (phoneNum == null) {
            System.out.println("judgeRegistered Error");
            return "ErrorParameter";
        }
        if (userService.judgeRegistered(phoneNum)) {
            return "false";
        } else {
            return "true";
        }
    }

    /**
     * @param phoneNum 手机号码
     * @param password 密码
     * @return 返回"true"或者"false"
     * @description 注册
     */
    @RequestMapping("/register")
    public String register(String phoneNum, String password) {
        if (phoneNum == null || password == null) {
            System.out.println("register Error");
            return "ErrorParameter";
        }
        if (userService.register(phoneNum, password)) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * @param phoneNum 手机号
     * @param password 密码
     * @return 返回的是user的对象Json串
     * @description 登录
     */
    @RequestMapping("/login")
    public String login(String phoneNum, String password) {
        if (phoneNum == null || password == null) {
            System.out.println("login Error");
            return "ErrorParameter";
        }
        String user = userService.login(phoneNum, password);
        return "".equals(user) ? "" : user;
    }

    /**
     * @param phoneNum 电话号码
     * @param password 密码
     * @return 返回"true"或"false"
     * @description 修改密码
     */
    @RequestMapping("/modifyPassword")
    public String modifyPassword(String phoneNum, String password) {
        if (phoneNum == null || password == null) {
            System.out.println("modifyPassword Error");
            return "ErrorParameter";
        }
        if (userService.modifyPassword(phoneNum, password)) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * @param activityId 活动id
     * @param id         用户id,注意不是getUserId,是getId
     * @return 返回报名成功"true",如果超出活动人数上限，返回"enough",已经报名了"exists"
     * @description 报名活动
     */
    @RequestMapping("/enrollActivity")
    public String enrollActivity(Integer activityId, Integer id) {
        if (activityId == null || id == null) {
            System.out.println("enrollActivity Error");
            return "ErrorParameter";
        }
        return userService.enrollActivity(activityId, id);
    }

    /**
     * @param activityId 活动id
     * @param id         用户id,注意不是getUserId,是getId
     * @return 返回取消报名成功"true"或者失败"false"
     * @description 取消报名活动
     */
    @RequestMapping("/cancelEnrollActivity")
    public String cancelEnrollActivity(Integer activityId, Integer id) {
        if (activityId == null || id == null) {
            System.out.println("enrollActivity Error");
            return "ErrorParameter";
        }
        return "success".equals(userService.cancelEnrollActivity(activityId, id)) ? "true" : "false";
    }

    /**
     * @param activityId 活动的id
     * @param id         用户id,注意不是getUserId,是getId
     * @param collect    是否收藏，发送给我的是"false"或者"true"
     * @return 返回是否操作成功
     * @description 收藏或者取消收藏
     */
    @RequestMapping("/changeCollectActivity")
    public String changeCollectActivity(Integer activityId, Integer id, String collect) {
        if (activityId == null || id == null || collect == null) {
            System.out.println("changeCollectActivity Error");
            return "ErrorParameter";
        }
        boolean tag = Boolean.parseBoolean(collect);
        boolean flag = userService.changeCollectActivity(activityId, id, tag);
        return flag ? "true" : "false";
    }

    /**
     * @param id                用户id,注意不是getUserId,是getIds
     * @param personalSignature 新的个性签名
     * @return 返回"true"或者"false"
     * @description 修改个性签名
     */
    @RequestMapping("/modifyPersonalSignature")
    public String modifyPersonalSignature(Integer id, String personalSignature) {
        if (id == null || personalSignature == null) {
            System.out.println("modifyPersonalSignature Error");
            return "ErrorParameter";
        }
        return userService.modifyPersonalSignature(id, personalSignature) ? "true" : "false";
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回是否关注 "true"或者"false"
     * @description 判断是否关注
     */
    @RequestMapping("/judgeFollow")
    public String judgeFollow(Integer followId, Integer followedId) {
        if (followId == null || followedId == null) {
            System.out.println("judgeFollow Error");
            return "ErrorParameter";
        }
        return userService.judgeFollow(followId, followedId) ? "true" : "false";
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回关注成功还是失败true或者false
     * @description 关注用户
     */
    @RequestMapping("/followUser")
    public String followUser(Integer followId, Integer followedId) {
        if (followId == null || followedId == null) {
            System.out.println("followUser Error");
            return "ErrorParameter";
        }
        return userService.followUser(followId, followedId) ? "true" : "false";
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回取消关注成功还是失败true或者false
     * @description 取消关注用户
     */
    @RequestMapping("/unFollowUser")
    public String unFollowUser(Integer followId, Integer followedId) {
        if (followId == null || followedId == null) {
            System.out.println("unFollowUser Error");
            return "ErrorParameter";
        }
        return userService.unFollowUser(followId, followedId) ? "true" : "false";
    }

    /**
     * @param id 当前用户的id
     * @return 返回粉丝数量
     * @description 获取用户被多少人关注，即粉丝数量
     */
    @RequestMapping("/getFollowedCount")
    public String getFollowedCount(Integer id) {
        if (id == null) {
            System.out.println("getFollowedCount Error");
            return "ErrorParameter";
        }
        Long count = userService.getFollowedCount(id);
        return count + "";
    }

    /**
     * @param id 当前用户的id
     * @return 返回关注人数
     * @description 获取用户关注了多少人
     */
    @RequestMapping("/getFollowCount")
    public String getFollowCount(Integer id) {
        if (id == null) {
            System.out.println("getFollowCount Error");
            return "ErrorParameter";
        }
        Long count = userService.getFollowCount(id);
        return count + "";
    }

    /**
     * @param id 用户的id
     * @return 返回user对象的JSON串
     * @description 根据id获取用户的信息，用于点击某个用户头像进入个人主页
     */
    @RequestMapping("/getUser")
    public String getUser(Integer id) {
        if (id == null) {
            System.out.println("getUser Error");
            return "ErrorParameter";
        }
        User user = userService.getUser(id);
        return user != null ? JSON.toJSONString(user) : "false";
    }

    /**
     * @param id 当前用户的id
     * @return 返回一个用户列表的Json串，如果为空则返回empty
     * @description 获取当前用户的关注列表
     */
    @RequestMapping("/getFollowUsers")
    public String getFollowUsers(Integer id) {
        if (id == null) {
            System.out.println("getFollowUsers Error");
            return "ErrorParameter";
        }
        List<User> userList = userService.getFollowUsers(id);
        return userList.size() != 0 ? JSON.toJSONString(userList) : "empty";
    }

    /**
     * @param id 当前用户的id
     * @return 返回一个用户列表的Json串，如果为空则返回empty
     * @description 获取当前用户的粉丝列表
     */
    @RequestMapping("/getFollowedUsers")
    public String getFollowedUsers(Integer id) {
        if (id == null) {
            System.out.println("getFollowedUsers Error");
            return "ErrorParameter";
        }
        List<User> userList = userService.getFollowedUsers(id);
        return userList.size() != 0 ? JSON.toJSONString(userList) : "empty";
    }

    /**
     * @param headPortrait 使用OkHttp3传输图片，类型为MultipartFile，名称为headPortrait
     * @param id           用户id
     * @return 返回修改成功还是失败
     * @description 更改用户头像
     */
    @RequestMapping("/modifyUserHeadPortrait")
    public String modifyUserHeadPortrait(MultipartFile headPortrait, Integer id) {
        if (headPortrait == null || id == null) {
            System.out.println("modifyUserHeadPortrait Error");
            return "ErrorParameter";
        }
        return userService.modifyUserHeadPortrait(headPortrait, id) ? "true" : "false";
    }

    /**
     * @param userName 用户新昵称，请求参数名称为userName
     * @param id       用户id
     * @return 返回修改成功还是失败
     * @description 更改用户昵称
     */
    @RequestMapping("/modifyUserName")
    public String modifyUserName(String userName, Integer id) {
        if (userName == null || id == null) {
            System.out.println("modifyUserName Error");
            return "ErrorParameter";
        }
        return userService.modifyUserName(userName, id) ? "true" : "false";
    }

    /**
     * @param sex 用户性别字段，1或者0
     * @param id  用户id
     * @return 返回修改成功还是失败
     * @description 修改用户性别
     */
    @RequestMapping("/modifyUserSex")
    public String modifyUserSex(Integer sex, Integer id) {
        if (sex == null || id == null) {
            System.out.println("modifyUserName Error");
            return "ErrorParameter";
        }
        return userService.modifyUserSex(sex, id) ? "true" : "false";
    }

    /**
     * @param id             用户id
     * @param residentIdCard 身份证号
     * @param realName       真实姓名
     * @return 返回是否认证成功，"true"或者"false"
     * @description 根据用户id设置用户身份证号
     */
    @RequestMapping("/idCardAuthentication")
    public String idCardAuthentication(Integer id, String residentIdCard, String realName) {
        if (id == null || residentIdCard == null || realName == null) {
            System.out.println("idCardAuthentication Error");
            return "ErrorParameter";
        }
        return "true".equals(userService.idCardAuthentication(id, residentIdCard, realName)) ? "true" : "false";
    }
}