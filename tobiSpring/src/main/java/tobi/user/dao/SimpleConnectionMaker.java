package tobi.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/tobi?serverTimezone=Asia/Seoul", "root", "root");
        return c;
    }

    /**
     * 상속을통해 구현받는 DUserDao, NUserDao 는 부모클래스가 변경이 일어나면 모두 영향을 받게된다.
     * 상속이아닌 다른 클래스로 분리하여 좀더 화끈하게 분리하는 방법을 선택하였다.
     * 그러나, UserDao코드의 수정없이 DB커넥션 생성기능을 변경할 방법이 없다라는 문제점이 생긴다.
     * 특정클래스에 종속되어있기때문이다.
     * 또한, UserDao가 SimpleConnectionMaker 라는 클래스타입의 인스턴스변수까지 정의해서 N사에서 다른 클래스를 구현하면 어쩔수없이 UserDao를 수정해야한다.
     *
     */
}
