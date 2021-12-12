package tobi.tobiSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tobi.user.dao.Book;
import tobi.user.dao.UserDao;

import java.util.Arrays;

@SpringBootApplication
public class TobiSpringApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(TobiSpringApplication.class, args);

		Class<UserDao> userDaoClass = UserDao.class; // Class타입의 인스턴스를 가져오는방법 (타입을통해 가지고오는방법)

		UserDao userDao = new UserDao();
		Class<? extends UserDao> aClass = userDao.getClass(); // Class타입의 인스턴스를 가져오는방법(인스턴스를통해 가지고오는방법)

		Class<Book> bookClass = Book.class;
		Book book = new Book();


		Arrays.stream(bookClass.getFields()).forEach(System.out::println); // Book의 public한 필드값들을 전부 가져온다.
		Arrays.stream(bookClass.getDeclaredFields()).forEach(System.out::println); // Book의 모든필드값들을 전부 가져온다.
		Arrays.stream(bookClass.getDeclaredFields()).forEach(f -> {
			try {
				f.setAccessible(true);  // 리플렉션의 접근지시자 무시하는 메서드
				System.out.printf("%s %s", f, f.get(book));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}); // Book의 모든필드값들을 전부 가져온다.






	}

}
