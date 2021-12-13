package tobi.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tobi.user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class UserDaoTest {

     @Test
        void addAndGet() throws SQLException, ClassNotFoundException {

         AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
         UserDao dao = context.getBean("userDao", UserDao.class);

         User user = new User();
         user.setId("LeeYoungJin");
         user.setName("이영진");
         user.setPassword("1234");

         dao.add(user);

         User user2 = dao.get(user.getId());

         assertThat(user2.getName(), is(user.getName()));
         assertThat(user2.getPassword(), is(user.getPassword()));


     }
 }
