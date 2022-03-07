package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author demon
 * @create 2022-02-25-13:09
 */
@Controller
@RequestMapping("user")
@Slf4j
public class UserControoler {

    @RequestMapping("findAll")
    public String findeAll(){
        log.info("进入findAll 方法");
        log.warn("");
        log.info("进入{}","findAll");
        log.info("年龄：{},","姓名:{}",age,name);
        return "index";
    }
}
