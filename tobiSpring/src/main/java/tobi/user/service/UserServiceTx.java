package tobi.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobi.user.domain.User;

public class UserServiceTx implements UserService{

    UserService userService; // 타겟 오브젝트
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void add(User user) { //메소드구현 + 위임
        userService.add(user);

    }

    @Override
    public void upgradeLevels() throws Exception { // 메소드 구현

        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //부가기능수행
        try {

            userService.upgradeLevels(); // 위임

            this.transactionManager.commit(status);  // 부가기능수행
        } catch (RuntimeException e) {//
            this.transactionManager.rollback(status);//
            throw e;//
        }
    }

}
