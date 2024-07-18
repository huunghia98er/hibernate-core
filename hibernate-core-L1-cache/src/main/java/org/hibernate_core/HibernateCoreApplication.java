package org.hibernate_core;

import org.hibernate_core.controller.MainController;
import org.hibernate_core.di.annotation.Autowired;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.di.loader.ContextLoaderClone;
import org.hibernate_core.di.loader.Runner;

@Component
public class HibernateCoreApplication implements Runner {

	public static boolean RUN = true;

	@Autowired
	private MainController mainController;

	public static void main(String[] args) {
		ContextLoaderClone.getInstance().load("org.hibernate_core");
	}

	@Override
	public void run() {
		while (RUN) {
			mainController.controller();
		}
	}
}
