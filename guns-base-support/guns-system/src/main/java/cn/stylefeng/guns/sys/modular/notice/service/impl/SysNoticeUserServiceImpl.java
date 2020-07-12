package cn.stylefeng.guns.sys.modular.notice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.sys.modular.notice.entity.SysNoticeUser;
import cn.stylefeng.guns.sys.modular.notice.mapper.SysNoticeUserMapper;
import cn.stylefeng.guns.sys.modular.notice.service.SysNoticeUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 系统通知公告用户service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/6/29 10:53
 */
@Service
public class SysNoticeUserServiceImpl extends ServiceImpl<SysNoticeUserMapper, SysNoticeUser> implements SysNoticeUserService {

    /**
     * 添加
     *
     * @author xuyuxiang
     * @date 2020/6/29 11:06
     */
    @Override
    public void add(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus) {
        noticeUserIdList.forEach(userId -> {
            SysNoticeUser sysNoticeUser = new SysNoticeUser();
            sysNoticeUser.setNoticeId(noticeId);
            sysNoticeUser.setUserId(userId);
            sysNoticeUser.setStatus(noticeUserStatus);
            this.save(sysNoticeUser);
        });
    }

    /**
     * 编辑
     *
     * @author xuyuxiang
     * @date 2020/6/29 11:40
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId);
        //先删除
        this.remove(queryWrapper);
        //再增加
        this.add(noticeId, noticeUserIdList, noticeUserStatus);
    }

    /**
     * 根据通知公告id查询通知人员信息集合
     *
     * @author xuyuxiang
     * @date 2020/6/29 11:50
     */
    @Override
    public List<SysNoticeUser> getSysNoticeUserListByNoticeId(Long noticeId) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId);
        return this.list(queryWrapper);
    }

    /**
     * 设为已读
     *
     * @author xuyuxiang
     * @date 2020/6/29 12:06
     */
    @Override
    public void read(Long noticeId, Long userId, Integer status) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId)
                .eq(SysNoticeUser::getUserId, userId);
        SysNoticeUser sysNoticeUser = this.getOne(queryWrapper);
        if(ObjectUtil.isNotNull(sysNoticeUser)) {
            sysNoticeUser.setStatus(status);
            sysNoticeUser.setReadTime(new Date());
            this.updateById(sysNoticeUser);
        }
    }
}
