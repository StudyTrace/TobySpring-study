package tobi.user.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobi.user.dao.UserDao;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserDao userDao;


    private PlatformTransactionManager transactionManager;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels() throws SQLException {

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource); // JDBC트랜잭션 추상오브젝트 생성

        /**
         * PlatformTransactionManager(스프링이 제공하는 트랜잭션 경계설정을 위한 추상인터페이스 )로 시작한 트랜잭션은 트랜낵션 동기화 저장소에 저장된다.
         * PlatformTransactionManager를 구현한 DatasourceTransactionManager 오브젝트는 JdbcTemplate에서 사용될수잇는 방식으로 트랜잭션을 관리해준다.
         */
//
//        TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화
//        Connection c = DataSourceUtils.getConnection(dataSource); // DB커넥션을 생성하고 트랜잭션 시작 , 이후의 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
//        c.setAutoCommit(false);

        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status); // 예외발생시 롤백
            throw e;
        }

//        }finally {
//            DataSourceUtils.releaseConnection(c, dataSource); // 안전하게 DB커넥션을 닫음
//            TransactionSynchronizationManager.unbindResource(this.dataSource);
//            TransactionSynchronizationManager.clearSynchronization();  // 동기화 작업 종료 및 정리
//        }

    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
     switch (currentLevel){
         case BASIC:
             return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
         case SILVER:
             return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
         case GOLD:
             return false;
         default: throw new IllegalArgumentException("Unknown Level: " + currentLevel); // 현재 로직에서 다룰수없는 레벨이면 예외
     }
    }

    public void add(User user) {

        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);

    }
}
