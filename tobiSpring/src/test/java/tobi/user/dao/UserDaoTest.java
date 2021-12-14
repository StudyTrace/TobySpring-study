package tobi.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobi.user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class) // @Runwith 대체
@ContextConfiguration(classes = {DaoFactory.class})
class UserDaoTest {

    @Autowired
    private ApplicationContext context;

    /**
     * ApplicationContext 가 메소드를 실행할때마다 생성되는부분을 리팩토링하였다.
     */

    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;



    @BeforeEach
     void setUp() {
      this.dao = context.getBean("userDao", UserDao.class);
        System.out.println("before 실행");
        this.user1 = new User("LeeYoungJin", "이영진", "1234");
        this.user2 = new User("KimYoungJin", "김영진", "5678");
        this.user3 = new User("ParkYoungJin", "박영진", "9012");
        System.out.println(this.context);
        System.out.println(this);
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
