package cn.stylefeng.guns.sys.modular.menu.node;

import cn.stylefeng.guns.core.pojo.node.TreeNode;
import lombok.Data;

import java.util.List;

/**
 * 菜单树节点
 *
 * @author xuyuxiang
 * @date 2020/4/5 12:03
 */
@Data
public class MenuTreeNode implements TreeNode {

    /**
     * 主键
     */
    private Long id;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 名称
     */
    private String title;

    /**
     * 值
     */
    private String value;

    /**
     * 排序，越小优先级越高
     */
    private Integer weight;

    /**
     * 子节点
     */
    private List children;

    /**
     * 父id别名
     */
    @Override
    public Long getPid() {
        return this.parentId;
    }

    /**
     * 子节点
     */
    @Override
    public void setChildren(List children) {
        this.children = children;
    }
}
