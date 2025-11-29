package org.dao;

import org.eneity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门表数据访问接口
 * 对应数据库表: departments
 * 提供部门数据的CRUD操作
 */
@Repository
public interface DepartmentMapper {

    /**
     * 根据部门ID查询部门
     * @param departmentId 部门ID
     * @return 部门对象
     */
    Department selectByDepartmentId(Long departmentId);

    /**
     * 查询所有部门
     * @return 部门列表
     */
    List<Department> selectAllDepartments();

    /**
     * 根据部门名称查询
     * @param departmentName 部门名称
     * @return 部门对象
     */
    Department selectByDepartmentName(String departmentName);

    /**
     * 插入部门
     * @param department 部门对象
     * @return 影响的行数
     */
    int insertDepartment(Department department);

    /**
     * 更新部门
     * @param department 部门对象
     * @return 影响的行数
     */
    int updateDepartment(Department department);

    /**
     * 删除部门
     * @param departmentId 部门ID
     * @return 影响的行数
     */
    int deleteByDepartmentId(Long departmentId);
}