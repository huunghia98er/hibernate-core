package org.hibernate_core.config;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate_core.entity.Student;

@Getter
public class HibernateSessionFactoryConfig {
	private SessionFactory sessionFactory;

	private HibernateSessionFactoryConfig() {
		this.init();
	}

	public static HibernateSessionFactoryConfig getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private synchronized void init() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration()
					.configure("hibernate.cfg.xml")
					.addAnnotatedClass(Student.class)
					.buildSessionFactory();
		}
	}

	private static final class InstanceHolder {
		private static final HibernateSessionFactoryConfig INSTANCE = new HibernateSessionFactoryConfig();
	}
}
