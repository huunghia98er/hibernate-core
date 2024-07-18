package org.hibernate_core.repo;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate_core.config.HibernateSessionFactoryConfig;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.di.annotation.PostConstruct;
import org.hibernate_core.entity.Student;

import java.util.List;

@Component
public class L2CacheRepository {
	private SessionFactory sessionFactory;

	public void demoL2Cache() {
		this.sessionFactory = HibernateSessionFactoryConfig.getInstance().getSessionFactory();
		try (Session session1 = sessionFactory.openSession();
		     Session session2 = sessionFactory.openSession();
		     Session session3 = sessionFactory.openSession()) {

			Student stu = session1.get(Student.class, 1L);
			System.out.println("Session 1 at 1st time: " + stu);

			Student student = session1.get(Student.class, 1L);
			System.out.println("Session 1 at 2nd time: " + student);

			Student student1 = session2.get(Student.class, 1L);
			System.out.println("Session 2: " + student1);

			sessionFactory.getCache().evictAll();

			Student student3 = session3.get(Student.class, 1L);
			System.out.println("Session 3: " + student3);
		}
	}

}
