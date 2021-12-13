package tobi.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tobi.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);
        User user = new User();
        user.setId("LeeYoungJin");
        user.setName("이영진");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId() + "등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + "조회 성공");


        /**
         * UserDao에있으면 안되는 관심사, 책임을 클라이언트로 떠넘겼다.
         * D사에서 사용하든, N사에서 사용하든 UserDao의 코드는 변경되지않는다.
         */
    }
}
