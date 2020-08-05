package cn.stylefeng.guns.modular.entity;

import cn.stylefeng.guns.core.pojo.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@Getter
@Setter
public class SysEmpDo extends BaseEntity {
    private Long id;
    private String jobNum;
    private Long orgId;
    private String orgName;

}
