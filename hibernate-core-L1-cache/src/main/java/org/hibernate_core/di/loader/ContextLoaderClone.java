package org.hibernate_core.di.loader;

import lombok.val;
import org.hibernate_core.di.annotation.Autowired;
import org.hibernate_core.di.annotation.Component;
import org.hibernate_core.di.annotation.PostConstruct;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ContextLoaderClone {
	private static final ContextLoaderClone INSTANCE = new ContextLoaderClone();
	private final Map<String, Object> nameToInstance = new HashMap<>();

	public static ContextLoaderClone getInstance() {
		return INSTANCE;
	}

	private ContextLoaderClone() {
	}

	public synchronized void load(String scanPackage) {
		val reflections = new Reflections(scanPackage);
		val classes = reflections.getTypesAnnotatedWith(Component.class);

		this.initiateInstance(classes);

		nameToInstance.forEach((name, instance) -> {
			this.injectField(instance);
			invokePostContruct(instance);
		});

		this.executeRunner();
	}

	private void initiateInstance(Set<Class<?>> clazzs) {
		clazzs.forEach(clazz -> {
			try {
				val c = Class.forName(clazz.getName());
				val instance = c.getDeclaredConstructor().newInstance();
				nameToInstance.put(clazz.getName(), instance);
			} catch (InstantiationException e) {
				System.out.println("Cannot instantiate class: " + clazz.getName());
			} catch (IllegalAccessException e) {
				System.out.println("Cannot access class: " + clazz.getName());
			} catch (InvocationTargetException e) {
				System.out.println("Cannot invoke class: " + clazz.getName());
			} catch (NoSuchMethodException e) {
				System.out.println("No such method: " + clazz.getName());
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found: " + clazz.getName());
			}
		});
	}

	private void executeRunner() {
		val runners = nameToInstance.values().stream()
				.filter(Runner.class::isInstance)
				.toList();

		if (runners.isEmpty()) {
			return;
		}

		if (runners.size() > 1) {
			throw new RuntimeException("Cannot have more than 1 runner class");
		}

		((Runner) runners.getFirst()).run();
	}

	private void injectField(Object instance) {
		Field[] fields = instance.getClass().getDeclaredFields();

		Arrays.stream(fields)
			.filter(f -> f.isAnnotationPresent(Autowired.class))
			.forEach(f -> {
				val obj = nameToInstance.get(f.getType().getName());
				f.setAccessible(true);
				try {
					f.set(instance, obj);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Cannot inject dependency " + f.getClass().getName()
							+ " to " + instance.getClass().getName());
				}
			});
	}

	private static void invokePostContruct(Object instance) {
		val postMethods = Arrays.stream(instance.getClass().getDeclaredMethods()).filter(
						method -> Arrays.stream(method.getDeclaredAnnotations())
								.anyMatch(a -> a.annotationType() == PostConstruct.class))
				.toList();
		if (postMethods.isEmpty()) {
			return;
		}
		if (postMethods.size() > 1) {
			throw new RuntimeException("Cannot have more than 1 post initiate method");
		}
		try {
			val method = postMethods.getFirst();
			method.setAccessible(true);
			method.invoke(instance);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			cause.printStackTrace();
			throw new RuntimeException(cause);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
