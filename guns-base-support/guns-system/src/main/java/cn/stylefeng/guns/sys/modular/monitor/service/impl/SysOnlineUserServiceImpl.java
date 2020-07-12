package cn.stylefeng.guns.sys.modular.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import cn.stylefeng.guns.core.exception.DemoException;
import cn.stylefeng.guns.core.pojo.login.SysLoginUser;
import cn.stylefeng.guns.sys.core.cache.UserCache;
import cn.stylefeng.guns.sys.core.log.LogManager;
import cn.stylefeng.guns.sys.modular.monitor.param.SysOnlineUserParam;
import cn.stylefeng.guns.sys.modular.monitor.result.SysOnlineUserResult;
import cn.stylefeng.guns.sys.modular.monitor.service.SysOnlineUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 系统组织机构service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/4/7 17:06
 */
@Service
public class SysOnlineUserServiceImpl implements SysOnlineUserService {

    @Resource
    private UserCache userCache;

    /**
     * 系统在线用户列表
     *
     * @author xuyuxiang
     * @date 2020/4/7 17:10
     */
    @Override
    public List<SysOnlineUserResult> list(SysOnlineUserParam sysOnlineUserParam) {
        List<SysOnlineUserResult> resultList = CollectionUtil.newArrayList();

        // 获取缓存中的所有用户
        Map<String, SysLoginUser> allKeyValues = userCache.getAllKeyValues();
        for (Map.Entry<String, SysLoginUser> sysLoginUserEntry : allKeyValues.entrySet()) {
            SysOnlineUserResult sysOnlineUserResult = new SysOnlineUserResult();
            sysOnlineUserResult.setSessionId(sysLoginUserEntry.getKey());
            BeanUtil.copyProperties(sysLoginUserEntry.getValue(), sysOnlineUserResult);
            resultList.add(sysOnlineUserResult);
        }
        return resultList;
    }

    /**
     * 系统在线用户强退
     *
     * @author xuyuxiang
     * @date 2020/4/7 20:21
     */
    @Override
    public void forceExist(SysOnlineUserParam sysOnlineUserParam) {
        Boolean demoEnvFlag = ConstantContextHolder.getDemoEnvFlag();
        if (demoEnvFlag) {
            throw new DemoException();
        }

        //获取缓存的key
        String redisLoginUserKey = sysOnlineUserParam.getSessionId();

        //获取缓存的用户
        SysLoginUser sysLoginUser = userCache.get(redisLoginUserKey);

        //如果缓存的用户存在，清除会话，否则表示该会话信息已失效，不执行任何操作
        if (ObjectUtil.isNotNull(sysLoginUser)) {

            //清除登录会话
            userCache.remove(redisLoginUserKey);

            //获取登录用户的账户信息
            String account = LoginContextHolder.me().getSysLoginUserAccount();

            //创建退出登录日志
            LogManager.me().executeExitLog(account);
        }
    }
}
