package tobi.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class) // @Runwith 대체
@ContextConfiguration(classes = {DaoFactory.class})
class UserDaoJdbcTest {

    @Autowired
    private ApplicationContext context;


    /**
     * ApplicationContext 가 메소드를 실행할때마다 생성되는부분을 리팩토링하였다.
     */

    @Autowired
    private UserDao dao;


    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void setUp() {
        this.dao = context.getBean("userDao", UserDaoJdbc.class);
        System.out.println("before 실행");
        this.user1 = new User("LeeYoungJin", "이영진", "1234", Level.BASIC, 1, 0);
        this.user2 = new User("KimYoungJin", "김영진", "5678", Level.SILVER, 55, 10);
        this.user3 = new User("ParkYoungJin", "박영진", "9012", Level.GOLD, 100, 40);
        System.out.println(this.context);
        System.out.println(this);
        System.out.println("아이디뽑기" + this.user1.getId() + this.user2.getId() + this.user3.getId());
    }


    @Test
    void addAndGet() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        checkSumUser(userget1, user1);


        User userget2 = dao.get(user2.getId());
        checkSumUser(userget2, user2);

    }


    @Test
    void count() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));


    }


    @Test
    void getUserFailure()  {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unknown_id"); // 예외발생해야함
        });
    }

    @Test
    void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        assertThrows(DuplicateKeyException.class, () -> {
            dao.add(user1); // 예외발생해야함
        });

    }


    @Test
    void getAll()  {
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSumUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSumUser(user2, users2.get(0));
        checkSumUser(user1, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSumUser(user2, users3.get(0));
        checkSumUser(user1, users3.get(1));
        checkSumUser(user3, users3.get(2));

    }

    private void checkSumUser(User user1, User user2) {
        assertThat(user1.getId(),is(user2.getId()));
        assertThat(user1.getName(),is(user2.getName()));
        assertThat(user1.getPassword(),is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }


    @Test
    void update() {
        dao.deleteAll();

        dao.add(user1); // 수정할 유저
        dao.add(user2); // 수정하지 않을 유저

        user1.setName("정자수");
        user1.setPassword("1234");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSumUser(user1, user1update);
        User user2same = dao.get(user2.getId());
        checkSumUser(user2, user2same);

    }







}
