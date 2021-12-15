package tobi.user.service;

import tobi.user.dao.UserDao;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private void upgradeLevel(User user) {
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
