package org.hibernate_core.repo;

import com.github.javafaker.Faker;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate_core.config.HibernateSessionFactoryConfig;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.di.annotation.PostConstruct;
import org.hibernate_core.entity.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class L1CacheRepository {
	private SessionFactory sessionFactory;
	private Faker faker;

	@PostConstruct
	private void initSessionFactory() {
		this.sessionFactory = HibernateSessionFactoryConfig.getInstance().getSessionFactory();
		this.faker  = new Faker();

		// mock data & insert batch
		List<Student> students = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			students.add(
					Student.builder()
							.lastName(faker.name().lastName())
							.firstName(faker.name().firstName())
							.email(faker.internet().emailAddress())
							.build()
			);
		}

//		try (Session session = sessionFactory.openSession()) {
//			session.beginTransaction();
//			session.doWork(connection -> {
//				insert(connection, students);
//			});
//			session.flush();
//			session.getTransaction().commit();
//		}
	}

	private void insert(Connection connection, List<Student> students) throws SQLException {
		String sql = "INSERT INTO students (first_name, last_name, email) VALUES (?, ?, ?)";

		try (PreparedStatement st = connection.prepareStatement(sql)) {
			for (Student stu : students) {
				st.setObject(1, stu.getFirstName());
				st.setObject(2, stu.getLastName());
				st.setObject(3, stu.getEmail());

				st.addBatch();
			}

			st.executeBatch();
		}
	}

	public void demoWorking(Long id) {
		Student student;
		try (Session session = sessionFactory.openSession()) {

			// save the student object
			student = session.get(Student.class, id);

			if (student == null) {
				System.out.println("Student not found!");
			}

			System.out.println("First time with session.get(Student.class, id), result : " + student);

			// L1 cache working
			Student student2 = session.get(Student.class, id);

			System.out.println("Second time with session.get(Student.class, id), result : " + student2);

		}
	}

	public void demoNotWorking(String name) {
		Student student;
		try (Session session = sessionFactory.openSession()) {

			student = session
					.createQuery("from Student where first_name = :name", Student.class)
					.setParameter("name", name)
					.uniqueResult();

			// L1 cache not working
			Student student2 = session
					.createQuery("from Student where first_name = :name", Student.class)
					.setParameter("name", name)
					.uniqueResult();

			System.out.println("Second time with session.get(Student.class, id), result : " + student2);

		}
	}

	public void flushCacheWithOutTransaction(Long id) {
		Student student;
		try (Session session = sessionFactory.openSession()) {

			// save the student object
			student = session.get(Student.class, id);

			if (student == null) {
				System.out.println("Student not found!");
			}

			// L1 cache working
			Student student2 = session.get(Student.class, id);

			student2.setFirstName("Updated name");

			System.out.println(student2);

			session.flush();
		}
	}

	@Deprecated
	public void noFlushCacheWithOutTransaction(Long id) {
		try (Session session = sessionFactory.openSession()) {
			// save the student object
			Student student = session.get(Student.class, id);

			if (student == null) {
				System.out.println("Student not found!");
			}

			System.out.println("Before change name to 'Update name' and clear cache " + student);

			session.clear();

			sessionFactory.getCache().evictAll();

			Student student2 = session.get(Student.class, id);

			System.out.println("After clear cache and get student again " + student2);
		}
	}

	public void flushCacheWithTransactional(Long id) {
		Student student;
		try (Session session = sessionFactory.openSession()) {

			session.beginTransaction();

			student = session.get(Student.class, id);

			if (student == null) {
				System.out.println("Student not found!");
			}

			student.setFirstName("Updated name");

			System.out.println(student);

			// session.flush();

			// Auto update with session.flush()
			session.getTransaction().commit();
		}
	}

	public void transactionEndButHibernateDoNotFlushCache(Long id) {
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();

			session.setFlushMode(FlushMode.MANUAL);

			// save the student object
			Student student = session.get(Student.class, id);

			if (student == null) {
				System.out.println("Student not found!");
				return;
			}

			student.setFirstName("Updated name 3");
			System.out.println("DEBUG point");
			System.out.println("DEBUG point");

			session.getTransaction().commit();
		}
	}

}
