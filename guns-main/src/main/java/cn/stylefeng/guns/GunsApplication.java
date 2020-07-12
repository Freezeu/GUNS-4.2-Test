package cn.stylefeng.guns;

import cn.hutool.log.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot方式启动类
 *
 * @author stylefeng
 * @date 2017/5/21 12:06
 */
@SpringBootApplication
public class GunsApplication {

    private static final Log log = Log.get();

    public static void main(String[] args) {
        SpringApplication.run(GunsApplication.class, args);
        log.info(">>> " + GunsApplication.class.getSimpleName() + " is success!");
    }

}