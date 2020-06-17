package com.yingfu.foodmatching.ui;

public class Menu {
	private String name;
	private String groumet;
	private String ingredient;
	
	public Menu() {
		// TODO Auto-generated constructor stub
	}

	public Menu(String name, String groumet, String ingredient) {
		this.name = name;
		this.groumet = groumet;
		this.ingredient = ingredient;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroumet() {
		return groumet;
	}

	public void setGroumet(String groumet) {
		this.groumet = groumet;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	@Override
	public String toString() {
		return "Menu [name=" + name + ", groumet=" + groumet + ", ingredient=" + ingredient + "]";
	}
	
	
}
