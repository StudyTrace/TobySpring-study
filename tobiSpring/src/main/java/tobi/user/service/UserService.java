package tobi.user.service;

import tobi.user.domain.User;

public interface UserService {

    void add(User user);

    void upgradeLevels() throws Exception;
}
