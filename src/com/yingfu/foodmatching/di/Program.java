package com.yingfu.foodmatching.di;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yingfu.foodmatching.entity.FoodMatching;
import com.yingfu.foodmatching.ui.Menu;

public class Program {
	public static void main(String[] args) {
		
		ApplicationContext context =
				new ClassPathXmlApplicationContext("com/yingfu/foodmatching/di/setting.xml");
		
		Menu m = context.getBean(Menu.class);
		FoodMatching fm = context.getBean(FoodMatching.class);
		System.out.println(fm.matching());
		
	}
}
