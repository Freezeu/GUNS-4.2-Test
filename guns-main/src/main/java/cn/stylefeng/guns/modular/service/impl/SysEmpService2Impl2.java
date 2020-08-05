package cn.stylefeng.guns.modular.service.impl;

import cn.stylefeng.guns.modular.entity.SysEmpDo;
import cn.stylefeng.guns.modular.mapper.EmpMapper;
import cn.stylefeng.guns.modular.service.SysEmpService2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysEmpService2Impl2 implements SysEmpService2 {

    @Resource
    EmpMapper empMapper;
    @Override
    public List<SysEmpDo> getEmp(Long id) {
        List<SysEmpDo> emp = empMapper.getEmp(id);
        emp.stream().forEach((x)->{
            System.out.println(x.toString());
        });
        return emp;
    }

    @Override
    public void addEmp(SysEmpDo sysEmpDo) {
        empMapper.addEmp(sysEmpDo);
    }

    @Override
    public void update(SysEmpDo sysEmpDo) {
        empMapper.updateEmp(sysEmpDo);
    }

    @Override
    public void delete(Long id) {
        empMapper.deleteEmp(id);
    }
}
