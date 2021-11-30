package tobi.user;

import javax.sql.DataSource;
import java.sql.*;


public class UserDao {

    private DataSource dataSource;
    private JdbcContext jdbcContext; // JdbcContext를 DI받도록 만듬

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }       // JdbcContext를 DI받도록 만듬

    public void add(final User user) throws SQLException {

        this.jdbcContext.workWithStatementStrategy( // DI받은 JdbcContext메소드를 사용하도록 만듬
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
                        ps.setString(1, user.getId());
                        ps.setString(2, user.getName());
                        ps.setString(3, user.getPassword());
                        return ps;
                    }
                }
        );
    }// AddStatement를 익명내부 클래스로전환


    public void deleteAll() throws SQLException {
       jdbcContextWithStatementStrategy(
               new StatementStrategy() {
                   @Override
                   public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                       return c.prepareStatement("delete from users");
                   }
               }
       );
    }// DeleteALlStatement를 익명내부 클래스로전환


//    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//        Connection c = null;
//        PreparedStatement ps = null;
//
//        try {
//            c = dataSource.getConnection();
//            ps= stmt.makePreparedStatement(c);
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            throw e;
//        }finally {
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) {
//
//                }
//            }
//
//            if (c != null) {
//                try {
//                    c.close();
//                } catch (SQLException e) {
//
//                }
//            }
//        }
//    }



    public int getCount() throws SQLException {

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;

        }finally {
            if (rs != null) {
                try {
                    rs.close();
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


    public User get(String id) throws SQLException, EmptyResultDataAccessException {

        Connection c = this.dataSource.getConnection();
        PreparedStatement ps = c
                .prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException();

        return user;
    }
}
