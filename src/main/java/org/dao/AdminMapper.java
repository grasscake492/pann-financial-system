package org.dao;

import org.entity.Admin;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理员表数据访问接口
 * 对应数据库表: admins
 * 提供管理员数据的CRUD操作
 */
@Repository
public interface AdminMapper {

    /**
     * 根据管理员ID查询
     * @param adminId 管理员ID
     * @return 管理员对象
     */
    Admin selectByAdminId(Long adminId);

    /**
     * 根据用户ID查询管理员
     * @param userId 用户ID
     * @return 管理员对象
     */
    Admin selectByUserId(Long userId);

    /**
     * 根据学号查询管理员
     * @param studentNumber 学号
     * @return 管理员对象
     */
    Admin selectByStudentNumber(String studentNumber);

    /**
     * 根据真实姓名查询管理员
     * @param realName 真实姓名
     * @return 管理员用户
     */
    List<Admin> selectByRealName(String realName);

    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    List<Admin> selectAllAdmins();

    /**
     * 根据部门ID查询管理员
     * @param departmentId 部门ID
     * @return 管理员列表
     */
    List<Admin> selectByDepartmentId(Long departmentId);

    /**
     * 根据部门名称查询管理员
     * @param departmentName 部门名称
     * @return 管理员列表
     */
    List<Admin> selectByDepartmentName(Long departmentName);

    /**
     * 查询最高级管理员
     * @return 管理员列表
     */
    List<Admin> selectSuperAdmins();

    /**
     * 统计管理员数量
     * @return 管理员总数
     */
    int countAdmins();

    /**
     * 插入管理员
     * @param admin 管理员对象
     * @return 影响的行数
     */
    int insertAdmin(Admin admin);

    /**
     * 更新管理员
     * @param admin 管理员对象
     * @return 影响的行数
     */
    int updateAdmin(Admin admin);

    /**
     * 更新管理员角色
     * @param adminId 管理员ID
     * @param departmentId 部门ID
     * @param isSuperAdmin 是否最高管理员
     * @return 影响的行数
     */
    int updateAdminRole(Long adminId, Long departmentId, Boolean isSuperAdmin);

    /**
     * 根据管理员ID删除管理员
     * @param adminId 管理员ID
     * @return 影响的行数
     */
    int deleteByAdminId(Long adminId);

}
