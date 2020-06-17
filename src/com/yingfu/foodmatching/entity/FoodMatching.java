package com.yingfu.foodmatching.entity;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;


import com.yingfu.foodmatching.ui.Menu;


public class FoodMatching extends Tools {
	
	private Menu menu;
	
	public FoodMatching() {
		// TODO Auto-generated constructor stub
	}
	
	public FoodMatching(Menu menu) {
		this.menu = menu;
	}
	
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@SuppressWarnings("unused")
	public String matching() {
		HashMap<String, HashSet<String>> food_Dict_Groumet = makeFoodDict("음식메뉴");
		HashMap<String, HashSet<String>> food_Dict_Ingredient = makeFoodDict("식재료");
		String matchedMenu = null;
		ArrayList<String> menuList = new ArrayList<String>();
		ArrayList<String> groumetMenus = null;
		// 음식메뉴에서 후보군 찾기
		
		try {
			for (String string : food_Dict_Groumet.keySet()) {
				groumetMenus = new ArrayList<String>(food_Dict_Groumet.get(string));
				for (int i = 0; i < groumetMenus.size(); i++) {
					try {
						if (groumetMenus.get(i).equals(menu.getName())) {
							matchedMenu = groumetMenus.get(i);
							return matchedMenu;
						}else if (groumetMenus.get(i).contains(menu.getGroumet()) || (groumetMenus.get(i).contains(menu.getGroumet()) && groumetMenus.get(i).contains(menu.getIngredient()))
								) {
							menuList.add(groumetMenus.get(i));
						} 
					} catch (Exception e) {}
				}
			}
		} catch (Exception e) {
			return "음식메뉴(groumet)와 식재료(ingredient) 없음";
		}
		if (menuList.size() == 0) {
			return "DB에 매칭되는 음식메뉴 없음";
		}
		System.out.println("menulist : " + menuList);

		// 식재료 군 찾기
		ArrayList<String> ingredientMenus = null;
		String ingredientGoon = null;
		ArrayList<String> ingredientList = new ArrayList<String>();
		if (menu.getIngredient() != null) {
			a: for (String string : food_Dict_Ingredient.keySet()) {
				ingredientMenus = new ArrayList<String>(food_Dict_Ingredient.get(string));
				for (int i = 0; i < ingredientMenus.size(); i++) {
					if (ingredientMenus.get(i).equals(menu.getIngredient())) {
						ingredientGoon = string;
						break a;
					}
				}
			}
			System.out.println("ingredietnGoon : " + ingredientGoon);

			// 음식메뉴의 식재료와 후보메뉴 식재료 중 군이 같은 식재료들 찾기
			try {
				ingredientMenus = new ArrayList<String>(food_Dict_Ingredient.get(ingredientGoon));
				for (int i = 0; i < ingredientMenus.size(); i++) {
					for (int j = 0; j < menuList.size(); j++) {
						if (ingredientMenus.get(i).equals(menuList.get(j).replace(menu.getGroumet(), ""))) {
							ingredientList.add(ingredientMenus.get(i));
						}
					}
				}
				System.out.println("ingredientList : " + ingredientList);
			} catch (Exception e) {
				System.out.println("식재료의 분류군 자료 없음");
			}

		}

		// 후보메뉴 식재료 후보 중 식재료와 가장 영양성분이 비슷한 식재료 찾기
		if (ingredientList.size() == 0) {
			// 식재료가 없는 경우 : 메뉴 후보 중에서 가장 대표 메뉴(ingredient가 없는경우, ingredient는 있으나 매칭이 안된경우
			// 구분필요)

			// ingredient 없는 경우
			Comparator<String> sortLength = new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if (o1.length() > o2.length()) {
						return 1;
					} else if (o1.length() < o2.length()) {
						return -1;
					} else {
						return 0;
					}
				}
			};
			Collections.sort(menuList, sortLength);
			matchedMenu = menuList.get(0);
			// ingredient가 db에는 없는 경우: 가장 비슷한 성분 찾아내기(추가 필요)
			
			
			
		} else {
			// 식재료가 있는 경우 영양성분 비교 후 도출
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String ingredient_knn = null;
			try {
				Class.forName("org.mariadb.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://rladmdqh0214.cafe24.com:3306/rladmdqh0214",
						"rladmdqh0214", "vkdlfl91!");
			} catch (Exception e) {
				return "mariaDB 연결 실패";
			}
			String sql = "SELECT dang_food_n/servingsize_food_n AS dang_ratio, dan_food_n/servingsize_food_n AS dan_ratio, zhifang_food_n/servingsize_food_n AS zhifang_ratio "
					+ "FROM food_nutrition_db " + "WHERE foodtype_food_n LIKE ? AND name_food_n LIKE ? "
					+ "ORDER BY kcal_food_n/servingsize_food_n";
			try {
				// ingredient 영양구성비 추출
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, "%" + ingredientGoon + "%");
				pstmt.setString(2, "%" + menu.getIngredient() + "%");
				rs = pstmt.executeQuery();
				rs.next();
				double dang_ratio = (rs.getDouble("dang_ratio"));
				double dan_ratio = (rs.getDouble("dan_ratio"));
				double zhifang_ratio = (rs.getDouble("zhifang_ratio"));
				// 추출한 영양구성비와 가장 유사한 식재료 찾기
				sql = "SELECT * " + "FROM food_nutrition_db "
						+ "ORDER BY (ABS((dan_food_n/servingsize_food_n)-?) + ABS((zhifang_food_n/servingsize_food_n)-?) + ABS((dang_food_n/servingsize_food_n)-?))";
				pstmt = con.prepareStatement(sql);
				pstmt.setDouble(1, dan_ratio);
				pstmt.setDouble(2, zhifang_ratio);
				pstmt.setDouble(3, dang_ratio);
				rs = pstmt.executeQuery();
				a: while (rs.next()) {
					for (int i = 0; i < ingredientList.size(); i++) {
						if (rs.getString("name_food_n").contains(ingredientList.get(i))) {
							ingredient_knn = ingredientList.get(i);
							break a;
						}

					}
				}
				pstmt.close();
				rs.close();
			} catch (SQLException e) {
				return "sql 오류";
			}
			matchedMenu = ingredient_knn + menu.getGroumet();
		}
		return matchedMenu;
	}
	

}
