package org.dao;

import org.entity.EditorialDepartmentMonthly;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 编辑部稿费月表数据访问接口
 * 对应数据库表: editorial_department_monthly
 * 提供编辑部稿费数据的CRUD操作
 */
@Repository
public interface EditorialDepartmentMonthlyMapper {

    /**
     * 根据记录ID查询
     * @param recordId 记录ID
     * @return 稿费记录
     */
    EditorialDepartmentMonthly selectByRecordId(Long recordId);

    /**
     * 根据统计月份查询
     * @param statisticalMonth 统计月份(YYYY-MM)
     * @return 稿费记录列表
     */
    List<EditorialDepartmentMonthly> selectByMonth(String statisticalMonth);

    /**
     * 查询用户在某个月的稿费记录
     * @param userId 用户ID
     * @param month 月份
     * @return 稿费记录列表
     */
    List<EditorialDepartmentMonthly> selectByUserAndMonth(Long userId, String month);

    /**
     * 查询所有记录
     * @return 稿费记录列表
     */
    List<EditorialDepartmentMonthly> selectAll();

    /**
     * 分页查询稿费记录
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 稿费记录列表
     */
    List<EditorialDepartmentMonthly> selectByPage(int offset, int limit);

    /**
     * 统计稿费记录数量
     * @return 记录总数
     */
    int countRecords();

    /**
     * 插入稿费记录
     * @param record 稿费记录对象
     * @return 影响的行数
     */
    int insertRecord(EditorialDepartmentMonthly record);

    /**
     * 更新稿费记录
     * @param record 稿费记录对象
     * @return 影响的行数
     */
    int updateRecord(EditorialDepartmentMonthly record);

    /**
     * 删除稿费记录
     * @param recordId 记录ID
     * @return 影响的行数
     */
    int deleteByRecordId(Long recordId);

    /**
     * 根据月份统计总稿费
     * @param month 月份
     * @return 总稿费金额
     */
    Double sumFeeByMonth(String month);

    /**
     * 根据用户ID统计总稿费
     * @param userId 用户ID
     * @return 总稿费金额
     */
    Double sumFeeByUser(Long userId);
}
