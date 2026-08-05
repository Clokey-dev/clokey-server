package org.clokey;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        entityManager.unwrap(Session.class).doWork(this::extractTableNames);
    }

    /**
     * JPA 엔티티 메타모델만 사용하면 {@code @ElementCollection} 조인 테이블(예: cloth_season)처럼 별도 엔티티가 아닌 테이블은 누락되어
     * 테스트 간 데이터가 leak 된다. 실제 DB에 존재하는 모든 테이블을 기준으로 truncate 대상을 결정한다.
     */
    private void extractTableNames(Connection conn) throws SQLException {
        List<String> names = new ArrayList<>();
        try (ResultSet rs =
                conn.getMetaData().getTables(null, "PUBLIC", "%", new String[] {"TABLE"})) {
            while (rs.next()) {
                names.add(rs.getString("TABLE_NAME"));
            }
        }
        tableNames = names;
    }

    public void execute() {
        entityManager.unwrap(Session.class).doWork(this::cleanTables);
    }

    private void cleanTables(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        for (String name : tableNames) {
            statement.executeUpdate(String.format("TRUNCATE TABLE %s", name));
            if (columnExists(conn, name, name)) {
                statement.executeUpdate(
                        String.format(
                                "ALTER TABLE %s ALTER COLUMN %s_id RESTART WITH 1", name, name));
            }
        }

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private boolean columnExists(Connection conn, String tableName, String columnName)
            throws SQLException {
        try (ResultSet rs =
                conn.getMetaData()
                        .getColumns(
                                null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }
}
