package tobi.user.dao;

public class DaoFactory {


    public UserDao userDao()

    {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        return userDao;
    }

//    public AccountDao accountDao()
//
//    {
//        ConnectionMaker connectionMaker = new DConnectionMaker();
//        UserDao userDao = new UserDao(connectionMaker);
//        return new AccountDao(new DConnectionMaker()); // 코드중복
//    }
//
//    public MessageDao accountDao()
//
//    {
//        ConnectionMaker connectionMaker = new DConnectionMaker();
//        UserDao userDao = new UserDao(connectionMaker);
//        return new MessageDao(new DConnectionMaker()); // 코드중복
//    }

    /**
     * 관심사를 조금더 분리했다.
     * 하지만, DAO 가 많아진다면 DConnectionMaker 인스턴스를 만드는부분이 중복된다.
     */





}
