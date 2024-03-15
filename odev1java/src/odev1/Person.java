package odev1;

public class Person {
	private String name;
	private double height;
	private int age;
	
	public Person() {
		this.name = "";
		this.height = 0;
		this.age = 0;
	}
	public Person(String name,double height ,int age) {
		this.name = name;
		this.height = height;
		this.age = age;
	}
	public String toString() {
		return name + " " + height + " " + age;
		}
}
