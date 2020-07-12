package cn.stylefeng.guns.sys.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.pojo.login.SysLoginUser;
import cn.stylefeng.guns.sys.core.cache.ResourceCache;
import cn.stylefeng.guns.sys.core.cache.UserCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存的配置，默认使用基于内存的缓存，如果分布式部署请更换为redis
 *
 * @author xuyuxiang
 * @date 2020/7/9 11:43
 */
@Configuration
public class CacheConfig {

    /**
     * url资源的缓存，默认不过期
     *
     * @author stylefeng
     * @date 2020/7/9 11:44
     */
    @Bean
    public ResourceCache resourceCache() {
        return new ResourceCache();
    }

    /**
     * 登录用户的缓存，默认过期时间，根据系统sys_config中的常量决定
     *
     * @author stylefeng
     * @date 2020/7/9 11:44
     */
    @Bean
    public UserCache userCache() {
        TimedCache<String, SysLoginUser> timedCache =
                CacheUtil.newTimedCache(ConstantContextHolder.getSessionTokenExpireSec() * 1000);

        //定时清理缓存，间隔1秒
        timedCache.schedulePrune(1000);

        return new UserCache(timedCache);
    }

}
