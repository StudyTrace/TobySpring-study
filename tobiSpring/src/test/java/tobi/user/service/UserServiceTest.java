package tobi.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import tobi.user.dao.DaoFactory;
import tobi.user.dao.UserDao;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static tobi.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static tobi.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class) // @Runwith 대체
@ContextConfiguration(classes = {DaoFactory.class})
 class UserServiceTest {

    @Autowired MailSender mailSender;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired UserServiceImpl userServiceImpl;
    @Autowired UserDao userDao;
    @Autowired UserService userService;

    List<User> users;


    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0,"이메일1"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0,"이메일2"),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1,"이메일3"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD,"이메일4"),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE,"이메일5")
        );
    }

    @Test
    @DirtiesContext // 컨텍스트의 DI설정을 변경하는 테스트라는것을 알려준다.
    void upgradeLevels() throws Exception {

        UserServiceImpl userServiceImpl = new UserServiceImpl();

        userDao.deleteAll();
        for (User user : users) {   // DB테스트 데이터 준비
            userDao.add(user);
        }

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);


        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender); //메일발송결과를 테스트할수있도록 목오브젝트를 만들어 userService의 의존오브젝트로 주입해준다.

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);


        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size(), is(2));
        assertThat(requests.get(0), is(users.get(1).getEmail()));
        assertThat(requests.get(1), is(users.get(3).getEmail()));  // 목 오브젝트를 이용한 결과 확인


        /**
         *
         */

    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); //업그레이드가 일어났는지 확인
        }else {
            assertThat(userUpdate.getLevel(), is(user.getLevel())); // 업그레이드가 일어나지않았는지 확인
        }
    }


    @Test
    void mockUpgradeLevels(){
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class); // 다이내믹한 목 오브젝트 생성
        when(mockUserDao.getAll()).thenReturn(this.users);  // 메소드의 리턴값 생성
        userServiceImpl.setUserDao(mockUserDao); // DI

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class)); // any = 파라미터를 무시하고 호출횟수만 확인할수있다.
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture()); //파라미터 정밀하게 확인하기위해 캡처도 할수있다.
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));


    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));

    }

    static class TestUserService extends UserServiceImpl {
        private String id;

        public TestUserService(String id) {
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }


    @Test
    @DirtiesContext
    void upgradeAllOrNothing() throws Exception {

        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setMailSender(mailSender);
        testUserService.setUserDao(this.userDao);

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");

        /**
         * 트랜잭션 핸들러가 필요한 정보와 오브젝트를 DI해준다.
         */

        UserService txUserService =(UserService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ UserService.class }, txHandler);

        /**
         * UserService 인터페이스 타입의 다이내믹 프록시 생성
         */
//
//        UserServiceTx txUserService = new UserServiceTx();
//        txUserService.setTransactionManager(transactionManager);
//        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected"); // TestUserService는 업그레이드작업중 예외발생해야함. 정상종료면 문제있으니 fail

        } catch (TestUserServiceException e) { // TestUserSercice가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그외의 예외라면 테스트실패

        }
        checkLevelUpgraded(users.get(1), false); // 예외가 발생하기전에 레벨변경이 있었던 사용자의 레벨이 처음상태로 바뀌었나 확인
    }

    static class MockMailSender implements MailSender {

        private List<String> requests =   new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        /**
         *  dummyMailSender처럼 아무것도 없지만, 메일주소를 저장해두고 읽을수있게끔한다는 차이점이있다.
         */

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... mailMessages) throws MailException {

        }
    }

    static class MockUserDao implements UserDao{
        private List<User> users; /// 레벨 업그레이드 후보 User오브젝트 목록
        private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public void add(User user) { throw new UnsupportedOperationException();}

        @Override
        public User get(String id) {throw new UnsupportedOperationException();}

        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void deleteAll() {throw new UnsupportedOperationException(); }

        @Override
        public int getCount() { throw new UnsupportedOperationException();}

        @Override
        public void update(User user) {
            updated.add(user);
        }
    }







}
