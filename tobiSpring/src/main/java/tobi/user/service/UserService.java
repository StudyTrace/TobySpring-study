package tobi.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobi.user.dao.UserDao;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import java.util.List;

public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;
    private MailSender mailSender;
    private PlatformTransactionManager transactionManager;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels() throws Exception {

        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            upgradeLevelsInternal();
            this.transactionManager.commit(status);
        } catch (Exception e) {
            this.transactionManager.rollback(status); // 예외발생시 롤백
            throw e;
        }


    }

    private void upgradeLevelsInternal() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이" + user.getLevel().name() + "로 업그레이드 완료");


        mailSender.send(mailMessage);

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
