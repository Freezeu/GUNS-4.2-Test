package cn.stylefeng.guns.sys.modular.user.mapper;

import cn.stylefeng.guns.sys.modular.user.entity.SysUser;
import cn.stylefeng.guns.sys.modular.user.result.SysUserResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户mapper接口
 *
 * @author xuyuxiang
 * @date 2020/3/12 9:48
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取用户分页列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/4/7 21:14
     */
    Page<SysUserResult> page(@Param("page") Page page, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 用户选择器
     *
     * @param queryWrapper 查询参数
     * @return 用户列表集合
     * @author xuyuxiang
     * @date 2020/7/3 13:22
     */
    List<SysUser> selector(@Param("ew") QueryWrapper<SysUser> queryWrapper);
}
