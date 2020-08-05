package cn.stylefeng.guns.modular.mapper;

import cn.stylefeng.guns.modular.entity.SysEmpDo;

import java.util.List;

public interface EmpMapper {
    List<SysEmpDo> getEmp(Long id);
    void addEmp(SysEmpDo dto);
    void updateEmp(SysEmpDo dto);
    void deleteEmp(Long id);
}
