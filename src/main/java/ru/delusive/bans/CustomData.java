package ru.delusive.bans;

import java.util.HashMap;
import java.util.Map.Entry;

/*
 * Это может вызвать некоторые вопросы, но другого более лаконичного выхода я не нашел
 * Класс позволяет хранить данные разных типов, при этом предоставлять информацию о том, какого типа по определенному индексу лежат данные
 * Сам по себе он нужен для выполнения запросов с разными типами данных (SELECT name, id, isAdmin...), где name - string, id - int, isAdmin - bool...
 * 
 */
public class CustomData {
	private HashMap<Integer, Boolean> bools = new HashMap<>();
	private HashMap<Integer, String> strs = new HashMap<>();
	private HashMap<Integer, Integer> ints = new HashMap<>();
	private HashMap<Integer, Long> longs = new HashMap<>();
	private HashMap<Integer, String> types = new HashMap<>();
	public int size = 0;
	public void add(int arg) {
		ints.put(size, arg);
		types.put(size, "int");
		size++;
	}
	public void add(boolean arg) {
		bools.put(size, arg);
		types.put(size, "bool");
		size++;
	}
	public void add(String arg) {
		strs.put(size, arg);
		types.put(size, "str");
		size++;
	}
	public void add(Long arg) {
		longs.put(size, arg);
		types.put(size, "long");
		size++;
	}
	public Entry<String, Object> get(int index) {
		HashMap <String, Object> map = new HashMap<>();
		switch(types.get(index)) {
			case "int":
				int res = ints.get(index);
				map.put("int", res);
				break; 
			case "bool":
				boolean res1 = bools.get(index);
				map.put("bool", res1);
				break;
			case "str":
				String res11 = strs.get(index);
				map.put("str", res11);
				break;
			case "long":
				Long res111 = longs.get(index);
				map.put("long", res111);
				break;
		}
		return map.entrySet().iterator().next();
	}
}
