package tobi.user;

import org.springframework.ejb.access.EjbAccessException;

import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ExceptionEx {


    public void add(User user) throws DuplicateUserIdException, SQLException {
        try {
            // JDBC를 이용해 user정보를 DB에 추가하는코드
            // 그런기능을가진 다른 SQLEception을 던지는 메소드를 호출하는코드

        } catch (SQLException e) {

            // ErrorCode가 MySQL의 "Duplicate Entry(1062)" 이면 예외 전환
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
                throw DuplicateUserIdException();
            else
                throw e; // 그 외에 경우는 SQLException 그대로


        }
        catch (SQLException e) {
            throw DuplicateUserIdException(e);  // 예외의 근본원인도 같이 넣어주는 방법
        }

        catch (SQLException e){
            throw DuplicateUserIdException().initCause(e); // iniCause() 메소드로 근본원인이 되는 예외를 넣어주는것  , 예외처리를 강제하는 checked exception(체크예외)을 unchecked exception(언체크예외)인 런타임예외로 바꾸는 경우에 사용한다 .
        }


        try {
            OrderHome orderHome = EJBHomeFactory.getInstance(),getOrderHome();
            Order order = orderHome.findByPrimaryKey(Integer id);

        } catch (NamingException ne) {
            throw new EJBException(ne);
        } catch (SQLException se) {
            throw new EJBException(se);
        } catch (RemoteException remoteException) {
            throw new EJBEception(re);
        }


    }
}
