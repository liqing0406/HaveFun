package com.hebtu.havefun.controller;

import com.hebtu.havefun.entity.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/9 10:54
 * @projectName HaveFun
 * @className AppDowloadController.java
 * @description TODO
 */
@Controller
@RequestMapping("/HaveFun")
public class AppDownloadController {
    @Resource
    Constant constant;

    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws Exception {
        // 文件地址，真实环境是存放在数据库中的
        File file = new File(constant.getDownloadPath());
        // 穿件输入对象
        FileInputStream fis = new FileInputStream(file);
        // 设置相关格式
        response.setContentType("application/force-download");
        // 设置下载后的文件名以及header
        response.addHeader("Content-disposition", "attachment;fileName=" + "HaveFun.apk");
        // 创建输出对象
        OutputStream os = response.getOutputStream();
        // 常规操作
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
    }
}
