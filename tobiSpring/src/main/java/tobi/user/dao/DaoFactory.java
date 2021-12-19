package tobi.user.dao;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import tobi.user.service.DummyMailSender;
import tobi.user.service.TransactionAdvice;
import tobi.user.service.TxProxyFactoryBean;
import tobi.user.service.UserServiceImpl;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {


    @Bean
    public TransactionAdvice transactionAdvice(){
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }

    @Bean
    public NameMatchMethodPointcut transactionPointcut() {
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("upgrade*");
        return nameMatchMethodPointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(transactionAdvice());
        defaultPointcutAdvisor.setPointcut(transactionPointcut());
        return defaultPointcutAdvisor;
    }

    @Bean
    public ProxyFactoryBean userService(){
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(userServiceImpl());
        proxyFactoryBean.setInterceptorNames("transactionAdvisor");
        return proxyFactoryBean;
    }



    @Bean
     public MessageFactoryBean message() {
        MessageFactoryBean messageFactoryBean = new MessageFactoryBean();
        messageFactoryBean.setText("Factory Bean");
        return messageFactoryBean;
    }


//    @Bean
//    public TxProxyFactoryBean userService() throws ClassNotFoundException {
//        TxProxyFactoryBean userServiceTx = new TxProxyFactoryBean();
//        userServiceTx.setTarget(userServiceImpl());
//        userServiceTx.setTransactionManager(transactionManager());
//        userServiceTx.setPattern("upgradeLevels");
//        userServiceTx.setServiceInterface(Class.forName("tobi.user.service.UserService"));
//        return userServiceTx;
//    }

    @Bean
    public UserServiceImpl userServiceImpl() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserDao(userDao());
        userServiceImpl.setMailSender(mailSender());
        return userServiceImpl;
    }

    @Bean
    public DummyMailSender mailSender() {
        DummyMailSender dummyMailSender = new DummyMailSender();
        return dummyMailSender;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }

    @Bean
    public UserDaoJdbc userDao()

    {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setDataSource(dataSource());
        return userDaoJdbc;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/tobi?serverTimezone=Asia/Seoul");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }


}
