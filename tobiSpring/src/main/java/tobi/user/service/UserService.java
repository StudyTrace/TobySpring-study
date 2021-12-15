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


        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);
        } catch (Exception e) {
            this.transactionManager.rollback(status); // 예외발생시 롤백
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
