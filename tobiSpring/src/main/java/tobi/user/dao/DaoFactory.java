package tobi.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import tobi.user.service.DummyMailSender;
import tobi.user.service.UserService;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        userService.setUserDao(userDao());
        userService.setTransactionManager(transactionManager());
        userService.setMailSender(mailSender());
        return userService;
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
