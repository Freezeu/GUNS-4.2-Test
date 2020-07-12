package cn.stylefeng.guns.modular.controller;

import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一个示例接口
 *
 * @author stylefeng
 * @date 2020/4/9 18:09
 */
@RestController
@RequestMapping("/test")
public class ExampleController {

    @RequestMapping("/niceDay")
    public ResponseData niceDay() {
        return new SuccessResponseData("nice day");
    }

}
