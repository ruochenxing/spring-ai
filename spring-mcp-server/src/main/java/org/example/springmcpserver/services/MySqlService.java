package org.example.springmcpserver.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

@Service
public class MySqlService {

    // 实现 执行select SQL 语句
    @Tool(description = "执行select SQL 语句，并返回结果")
    public String executeSql(String sql) throws SQLException, JsonProcessingException {
        System.out.println("receive sql: " + sql);
        // 如果不是 select 语句，则抛出异常
        if (!sql.trim().toLowerCase().startsWith("select") && !sql.trim().toLowerCase().startsWith("show")) {
            throw new SQLException("只支持 select 语句, 不支持: " + sql);
        }
        String url = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "123456";
        // 1. 获取数据库连接
        Connection conn = getConnection(url, username, password);
        // 2. 执行 SQL 语句
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        // 3. 将结果集转换为 List<Map<String, Object>>
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
            }
            result.add(row);
        }
        // 4. 关闭数据库连接
        closeConnection(conn);
        // 5. 返回 json 字符串，使用 Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        System.out.println("result: " + json);
        return json;
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
