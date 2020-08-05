package cn.stylefeng.guns.modular.service;

import cn.stylefeng.guns.modular.entity.SysEmpDo;


import java.util.List;

public interface SysEmpService2 {
    List<SysEmpDo> getEmp(Long id);
    void addEmp (SysEmpDo sysEmpDo);
    void update (SysEmpDo sysEmpDo);
    void delete (Long id);
}
