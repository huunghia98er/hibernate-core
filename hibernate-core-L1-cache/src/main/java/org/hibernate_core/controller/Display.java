package org.hibernate_core.controller;

public class Display {

	public static void mainDisplay() {
		System.out.println("Welcome to Hibernate Core!");
		System.out.println("L1 Cache");
		System.out.println("0. L1 Cache not working");
		System.out.println("1. L1 Cache working");
		System.out.println("2. L1 Cache with out transaction and don't call flush() method");
		System.out.println("3. L1 Cache with out transaction and call flush() method");
		System.out.println("4. L1 Cache with transaction");
		System.out.println("5. Transaction commit but hibernate do not flush cache");

		System.out.println("Please choose an option: ");
	}

}
