/*
Copyright [2020] [https://www.stylefeng.cn]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Guns源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/stylefeng/guns-separation
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/stylefeng/guns-separation
6.若您的项目无法满足以上几点，可申请商业授权，获取Guns商业授权许可，请在官网购买授权，地址为 https://www.stylefeng.cn
 */
package cn.stylefeng.guns.modular.controller;

import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import cn.stylefeng.guns.modular.entity.SysEmpDo;
import cn.stylefeng.guns.modular.service.impl.SysEmpService2Impl2;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 一个示例接口
 *
 * @author stylefeng
 * @date 2020/4/9 18:09
 */
@RestController
@RequestMapping("/test")
public class ExampleController {

    @Resource
    SysEmpService2Impl2 sysEmpService;

    private static final Logger log = LoggerFactory.getLogger(ExampleController.class);

    @RequestMapping("/niceDay")
    public ResponseData niceDay() {

        return new SuccessResponseData("nice day");
    }
    @GetMapping("/testGet")
    public String getEmp(Long id) {
        sysEmpService.getEmp(id);
        log.info("running testGet");
        return "ok";
    }
    @GetMapping("/testAdd")
    public String addEmp(SysEmpDo dto) {
        sysEmpService.addEmp(dto);
        log.info("running testAdd");
        return "ok";
    }
    @GetMapping("/testUpdate")
    public List<SysEmpDo> updateEmp(SysEmpDo dto) {
        log.info("update running");
        sysEmpService.update(dto);
        return sysEmpService.getEmp(dto.getId());

    }
    @GetMapping("/testDelete")
    public String deleteEmp(Long id) {
        log.info("delete running");
        sysEmpService.delete(id);
        return "ok";
    }

}
