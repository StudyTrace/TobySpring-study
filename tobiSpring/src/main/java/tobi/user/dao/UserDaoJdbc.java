package tobi.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import tobi.user.domain.Level;
import tobi.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;


public class UserDaoJdbc implements UserDao{

    private JdbcTemplate jdbcTemplate;
    private Map<String, String> sqlMap;
    private String sqlAdd;


    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public void setSqlAdd(String sqlAdd) {
        this.sqlAdd = sqlAdd;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            user.setEmail(rs.getString("email"));

            return user;
        }
    };




    public void add(final User user)  {
        this.jdbcTemplate.update(this.sqlMap.get("add"), user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
        }



    public User get(String id)  {

        return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"), new Object[]{id}, this.userMapper);
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlMap.get("getAll"), this.userMapper);

    }


    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
    }



    public int getCount()  {

        return this.jdbcTemplate.queryForObject(this.sqlMap.get("getCount"), Integer.class);

    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(this.sqlMap.get("update"), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }


}
