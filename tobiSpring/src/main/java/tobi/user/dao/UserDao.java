package tobi.user.dao;

import tobi.user.domain.User;

import java.sql.*;


public abstract class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();
        ps.close();
        c.close();

    }


    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
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

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
    // 구현코드제거, 추상메소드로 전환 서브클래스가 구현담당함 -> UserDao 의 코드는 수정할필요없이 DB연결기능을 새롭게 정의한클래스를 만들수있음

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        UserDao dao = new UserDao();
//        User user = new User();
//        user.setId("LeeYoungJin");
//        user.setName("이영진");
//        user.setPassword("1234");
//
//        dao.add(user);
//
//        System.out.println(user.getId() + "등록 성공");
//
//        User user2 = dao.get(user.getId());
//        System.out.println(user2.getName());
//        System.out.println(user2.getPassword());
//        System.out.println(user2.getId() + "조회 성공");


    }



}
