package tobi.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = this.dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();
        }catch(SQLException e){
            throw e;
        }finally {
            if(ps!=null){
                try {
                    ps.close();// 리소스를 반환, 반환하지않는다면 DB풀이 어느샌가 꽉찰수있다.
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }


        }

    }

    public void exeCuteSql(final String query) throws SQLException {
        workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement(query);
            }
        });
        //변하지 않는 콜백 클래스 정의와 오브젝트 생성

    }
}
