package tobi.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {


    @Bean
    public UserDao userDao()

    {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }

    /**
     * 관심사를 조금더 분리했다.
     * 하지만, DAO 가 많아진다면 DConnectionMaker 인스턴스를 만드는부분이 중복된다.
     */





}
