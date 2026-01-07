package org.test;
// NewDatabaseConnectionTest.java
import java.sql.*;

public class NewDatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pann_financial_system?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ PANN财务系统数据库连接成功！");

            // 验证表
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            System.out.println("📊 数据库中的表：");
            while (rs.next()) {
                System.out.println(" - " + rs.getString(1));
            }

        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败: " + e.getMessage());
        }
    }
}
