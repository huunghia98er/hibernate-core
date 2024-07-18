package org.hibernate_core.controller;

import org.hibernate_core.HibernateCoreApplication;
import org.hibernate_core.di.annotation.Autowired;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.repo.L1CacheRepository;

import java.util.Scanner;

@Component
public class MainController {

	@Autowired
	private L1CacheRepository l1CacheRepository;

	public Scanner scanner = new Scanner(System.in);

	public void controller() {
		Display.mainDisplay();

		Integer userChoose = null;

		userChoose = scanner.nextInt();

		switch (userChoose) {
			case 0 -> {
				soutInputId();
				String name = scanner.next();
				l1CacheRepository.demoNotWorking(name);
			}

			case 1 -> {
				soutInputId();
				Long id = scanner.nextLong();
				l1CacheRepository.demoWorking(id);
			}

			case 2 -> {
				soutInputId();
				Long studentId = scanner.nextLong();
				l1CacheRepository.noFlushCacheWithOutTransaction(studentId);
			}

			case 3 -> {
				soutInputId();
				Long studentId = scanner.nextLong();
				l1CacheRepository.flushCacheWithOutTransaction(studentId);
			}

			case 4 -> {
				soutInputId();
				Long studentId = scanner.nextLong();

				l1CacheRepository.flushCacheWithTransactional(studentId);
			}

			case 5 -> {
				soutInputId();
				Long studentId = scanner.nextLong();

				l1CacheRepository.transactionEndButHibernateDoNotFlushCache(studentId);

				System.out.println("Transaction commit but hibernate do not flush cache");
				System.out.println("DEBUG point");
			}

			default -> HibernateCoreApplication.RUN = false;
		}
	}

	private static void soutInputId() {
		System.out.println("Input student id: ");
	}

}
