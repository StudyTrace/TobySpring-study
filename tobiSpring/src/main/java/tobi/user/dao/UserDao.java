package tobi.user.dao;

import tobi.user.domain.User;

import java.sql.*;


public class UserDao {

    private ConnectionMaker connectionMaker; // 인터페이스이므로 구체적인 클래스정보를 알필요가없다.

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker; // 구체적인 클래스에 의존... 문제발생 또다시 원점이다.

    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();
        ps.close();
        c.close();

    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }



}
