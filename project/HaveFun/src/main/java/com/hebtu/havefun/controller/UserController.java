package com.hebtu.havefun.controller;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.entity.Messages;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
     * @return 返回报名成功"true"或者已经报名了"exists"
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
     * @param sender   发送者id,注意不是getUserId,是getId
     * @param receiver 接收者id,注意不是getUserId,是getId
     * @param msg      消息内容
     * @return 返回这个消息对象Messages
     * @description 发送消息，这边插入数据库
     */
    @RequestMapping("/addMsg")
    public String addMsg(Integer sender, Integer receiver, String msg) {
        if (sender == null || receiver == null || msg == null) {
            System.out.println("addMsg Error");
            return "ErrorParameter";
        }
        Messages messages = userService.addMsg(sender, receiver, msg);
        return messages != null ? JSON.toJSONString(messages) : "false";
    }

    /**
     * @param sender   发送者id,注意不是getUserId,是getId
     * @param receiver 接收者id,注意不是getUserId,是getId
     * @param pageNum  当前页码
     * @param pageSize 页面大小
     * @return 返回List<Messages>集合Json
     * @description 获取消息列表，分页显示
     */
    @RequestMapping("/getMsg")
    public String getMsg(Integer sender, Integer receiver, Integer pageNum, Integer pageSize) {
        if (sender == null || receiver == null || pageNum == null || pageSize == null) {
            System.out.println("getMsg Error");
            return "ErrorParameter";
        }
        List<Messages> messagesList = userService.getMsg(sender, receiver, pageNum, pageSize);
        return messagesList != null ? JSON.toJSONString(messagesList) : "empty";
    }

    /**
     * @param sender   发送者id,注意不是getUserId,是getId
     * @param receiver 接收者id,注意不是getUserId,是getId
     * @return 返回的是一个数量的string类型
     * @description 获取两个人之间总消息数量
     */
    @RequestMapping("/getMsgNum")
    public String getMsgNum(Integer sender, Integer receiver) {
        if (sender == null || receiver == null) {
            System.out.println("getMsgNum Error");
            return "ErrorParameter";
        }
        Long count = userService.getMsgNum(sender, receiver);
        System.out.println(count);
        return count + "";
    }

    /**
     * @param otherId 对方的id，注意不是getUserId,是getId
     * @param mineId  自己的id，注意不是getUserId,是getId
     * @return 返回依然是List<Messages>集合Json
     * @description 每隔若干秒刷新消息
     */
    @RequestMapping("/freshMsg")
    public String freshMsg(Integer otherId, Integer mineId) {
        if (otherId == null || mineId == null) {
            System.out.println("freshMsg Error");
            return "ErrorParameter";
        }
        List<Messages> messagesList = userService.freshMsg(otherId, mineId);
        return messagesList.size() != 0 ? JSON.toJSONString(messagesList) : "empty";
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
        User user = userService.getUser(id);
        if (id == null) {
            System.out.println("getUser Error");
            return "ErrorParameter";
        }
        return user != null ? JSON.toJSONString(user) : "false";
    }

    /**
     * @param id 当前user的id
     * @return 返回一个List<Map < Messages, Integer>>,messages的数据表示用于展示聊天列表中
     * 最新的一条消息，Integer的数据为未读消息数量，业务逻辑为我是接收者，发送者为对方，且消息未读
     * @description 获取和用户交流过的消息列表
     */
    @RequestMapping("/getMessageList")
    public String getMessageList(Integer id) {
        if (id == null) {
            System.out.println("getMessageList Error");
            return "ErrorParameter";
        }
        List<Map<Messages, Integer>> messagesList = userService.getMessageList(id);
        return messagesList.size() != 0 ? JSON.toJSONString(messagesList) : "empty";
    }

    /**
     * @param id 当前用户的id
     * @return 返回一个用户列表的Json串，如果为空则返回empty
     * @description 获取当前用户的关注列表
     */
    @RequestMapping("/getFollowUsers")
    public String getFollowUsers(Integer id) {
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
        return userService.modifyUserSex(sex, id) ? "true" : "false";
    }
}