package tobi.user.dao;

import tobi.user.domain.User;
import tobi.user.sqlservice.SqlService;

import java.util.List;

public interface UserDao {

    void add(User user) throws SqlService.SqlRetrievalFailureException;

    User get(String id) throws SqlService.SqlRetrievalFailureException;

    List<User> getAll() throws SqlService.SqlRetrievalFailureException;

    void deleteAll() throws SqlService.SqlRetrievalFailureException;

    int getCount() throws SqlService.SqlRetrievalFailureException;


    void update(User user1) throws SqlService.SqlRetrievalFailureException;

}
