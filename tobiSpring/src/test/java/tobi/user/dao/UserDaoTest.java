package tobi.user.dao;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import tobi.user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDaoTest {

    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    /**
     * 테스트를 수행하는데 필요한 정보나 오브젝트인 픽스처로 반복되는부분을 추출하였다. 
     */

    @BeforeEach
     void setUp() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
       this.dao = context.getBean("userDao", UserDao.class);
        System.out.println("before 실행");
        this.user1 = new User("LeeYoungJin", "이영진", "1234");
        this.user2 = new User("KimYoungJin", "김영진", "5678");
        this.user3 = new User("ParkYoungJin", "박영진", "9012");
    }


     @Test
        void addAndGet() throws SQLException {
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
         dao.deleteALl();
         assertThat(dao.getCount(), is(0));

         dao.add(user1);
         assertThat(dao.getCount(), is(1));

         dao.add(user2);
         assertThat(dao.getCount(), is(2));

         dao.add(user3);
         assertThat(dao.getCount(), is(3));


     }


     @Test
    void getUserFailure() throws SQLException {
         dao.deleteALl();
         assertThat(dao.getCount(), is(0));

         assertThrows(EmptyResultDataAccessException.class, () ->{
             dao.get("unknown_id"); // 예외발생해야함
         });
     }


 }
