package org.hibernate_core.controller;

import org.hibernate_core.HibernateCoreApplication;
import org.hibernate_core.di.annotation.Autowired;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.repo.L2CacheRepository;

import java.util.Scanner;

@Component
public class MainController {

	@Autowired
	private L2CacheRepository l2CacheRepository;

	public Scanner scanner = new Scanner(System.in);

	public void controller() {
		Display.mainDisplay();

		Integer userChoose = null;

		userChoose = scanner.nextInt();

		switch (userChoose) {

			case 1 -> {
				l2CacheRepository.demoL2Cache();
			}

			default -> HibernateCoreApplication.RUN = false;
		}
	}


}
