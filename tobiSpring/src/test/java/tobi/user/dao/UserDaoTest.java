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
         User user1 = new User("LeeYoungJin", "이영진", "1234");
         User user2 = new User("KimYoungJin", "김영진", "5678");

         dao.deleteALl();
         assertThat(dao.getCount(), is(0));

         dao.add(user1);
         dao.add(user2);
         assertThat(dao.getCount(), is(2));

         User userget1 = dao.get(user1.getId());
         assertThat(user1.getName(), is(user1.getName()));
         assertThat(user1.getPassword(), is(user1.getPassword()));


         User userget2 = dao.get(user2.getId());
         assertThat(user2.getName(), is(user2.getName()));
         assertThat(user2.getPassword(), is(user2.getPassword()));


     }



     @Test
    void count() throws SQLException {
         AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
         UserDao dao = context.getBean("userDao", UserDao.class);

         User user1 = new User("LeeYoungJin", "이영진", "1234");
         User user2 = new User("KimYoungJin", "김영진", "5678");
         User user3 = new User("ParkYoungJin", "박영진", "9012");

         dao.deleteALl();
         assertThat(dao.getCount(), is(0));

         dao.add(user1);
         assertThat(dao.getCount(), is(1));

         dao.add(user2);
         assertThat(dao.getCount(), is(2));

         dao.add(user3);
         assertThat(dao.getCount(), is(3));


     }
 }
