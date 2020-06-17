package com.yingfu.matching;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchingDAO {

	private static String re(String word) {

		Matcher matcher = null;
		Pattern pattern = null;

		pattern = Pattern.compile("(^[^0-9a-zA-Z+/])+([°¡-ÆR &]*)+");
		matcher = pattern.matcher(word);
		String g = null;
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			g = matcher.group();
			if (!g.equals("")) {
				sb.append(g);
			}
		}
		return sb.toString();
	}

	protected static HashMap<String, HashSet<String>> makeFoodDict(String category) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream("C:\\Users\\H\\Desktop\\yingfu\\DB\\rawData\\foodNutrition\\nutritionDB.csv"),
					"utf-8"));
			String line = null;
			boolean isFirst = true;
			HashSet<String> food_Goon_List = new HashSet<String>();
			HashMap<String, HashSet<String>> food_Goon_Dict1 = new HashMap<String, HashSet<String>>();
			String[] infos = null;

			if (category.equals("À½½Ä¸Þ´º")) {
				category = "D";
			} else if (category.equals("½ÄÀç·á")) {
				category = "R";
			}

			while ((line = br.readLine()) != null) {
				if (isFirst) {
					isFirst = false;
					continue;
				}
				infos = line.split(",");
				if (infos[1].contains(category)) {
					if (food_Goon_Dict1.containsKey(infos[3])) {
						food_Goon_List = food_Goon_Dict1.get(infos[3]);
					} else {
						food_Goon_List = new HashSet<String>();
					}
					if (category.equals("D")) {
						food_Goon_List.add(re(infos[4]).trim());
					} else {
						if (infos[4].split("\\.").length == 1) {
							food_Goon_List.add(infos[4].split("\\.")[0].trim());
						} else {
							food_Goon_List.add(infos[4].split("\\.")[infos[4].split("\\.").length - 2].trim());
						}
					}
					food_Goon_Dict1.put(infos[3], food_Goon_List);
				}

			}
			br.close();
			return food_Goon_Dict1;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	@SuppressWarnings("unused")
	protected static String matching(String menu, String groumet, String ingredient) {
		HashMap<String, HashSet<String>> food_Dict_Groumet = MatchingDAO.makeFoodDict("À½½Ä¸Þ´º");
		HashMap<String, HashSet<String>> food_Dict_Ingredient = MatchingDAO.makeFoodDict("½ÄÀç·á");
		String matchedMenu = null;
		ArrayList<String> menuList = new ArrayList<String>();
		ArrayList<String> groumetMenus = null;
		// À½½Ä¸Þ´º¿¡¼­ ÈÄº¸±º Ã£±â
		
		try {
			for (String string : food_Dict_Groumet.keySet()) {
				groumetMenus = new ArrayList<String>(food_Dict_Groumet.get(string));
				for (int i = 0; i < groumetMenus.size(); i++) {
					try {
						if (groumetMenus.get(i).equals(menu)) {
							matchedMenu = groumetMenus.get(i);
							return matchedMenu;
						}else if (groumetMenus.get(i).contains(groumet) || (groumetMenus.get(i).contains(groumet) && groumetMenus.get(i).contains(ingredient))
								) {
							menuList.add(groumetMenus.get(i));
						} 
					} catch (Exception e) {}
				}
			}
		} catch (Exception e) {
			return "À½½Ä¸Þ´º(groumet)¿Í ½ÄÀç·á(ingredient) ¾øÀ½";
		}
		if (menuList.size() == 0) {
			return "DB¿¡ ¸ÅÄªµÇ´Â À½½Ä¸Þ´º ¾øÀ½";
		}
		System.out.println("menulist : " + menuList);

		// ½ÄÀç·á ±º Ã£±â
		ArrayList<String> ingredientMenus = null;
		String ingredientGoon = null;
		ArrayList<String> ingredientList = new ArrayList<String>();
		if (ingredient != null) {
			a: for (String string : food_Dict_Ingredient.keySet()) {
				ingredientMenus = new ArrayList<String>(food_Dict_Ingredient.get(string));
				for (int i = 0; i < ingredientMenus.size(); i++) {
					if (ingredientMenus.get(i).equals(ingredient)) {
						ingredientGoon = string;
						break a;
					}
				}
			}
			System.out.println("ingredietnGoon : " + ingredientGoon);

			// À½½Ä¸Þ´ºÀÇ ½ÄÀç·á¿Í ÈÄº¸¸Þ´º ½ÄÀç·á Áß ±ºÀÌ °°Àº ½ÄÀç·áµé Ã£±â
			try {
				ingredientMenus = new ArrayList<String>(food_Dict_Ingredient.get(ingredientGoon));
				for (int i = 0; i < ingredientMenus.size(); i++) {
					for (int j = 0; j < menuList.size(); j++) {
						if (ingredientMenus.get(i).equals(menuList.get(j).replace(groumet, ""))) {
							ingredientList.add(ingredientMenus.get(i));
						}
					}
				}
				System.out.println("ingredientList : " + ingredientList);
			} catch (Exception e) {
				System.out.println("½ÄÀç·áÀÇ ºÐ·ù±º ÀÚ·á ¾øÀ½");
			}

		}

		// ÈÄº¸¸Þ´º ½ÄÀç·á ÈÄº¸ Áß ½ÄÀç·á¿Í °¡Àå ¿µ¾ç¼ººÐÀÌ ºñ½ÁÇÑ ½ÄÀç·á Ã£±â
		if (ingredientList.size() == 0) {
			// ½ÄÀç·á°¡ ¾ø´Â °æ¿ì : ¸Þ´º ÈÄº¸ Áß¿¡¼­ °¡Àå ´ëÇ¥ ¸Þ´º(ingredient°¡ ¾ø´Â°æ¿ì, ingredient´Â ÀÖÀ¸³ª ¸ÅÄªÀÌ ¾ÈµÈ°æ¿ì
			// ±¸ºÐÇÊ¿ä)

			// ingredient ¾ø´Â °æ¿ì
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
			// ingredient°¡ db¿¡´Â ¾ø´Â °æ¿ì: °¡Àå ºñ½ÁÇÑ ¼ººÐ Ã£¾Æ³»±â(Ãß°¡ ÇÊ¿ä)
			
			
			
		} else {
			// ½ÄÀç·á°¡ ÀÖ´Â °æ¿ì ¿µ¾ç¼ººÐ ºñ±³ ÈÄ µµÃâ
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String ingredient_knn = null;
			try {
				Class.forName("org.mariadb.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://rladmdqh0214.cafe24.com:3306/rladmdqh0214",
						"rladmdqh0214", "vkdlfl91!");
			} catch (Exception e) {
				return "mariaDB ¿¬°á ½ÇÆÐ";
			}
			String sql = "SELECT dang_food_n/servingsize_food_n AS dang_ratio, dan_food_n/servingsize_food_n AS dan_ratio, zhifang_food_n/servingsize_food_n AS zhifang_ratio "
					+ "FROM food_nutrition_db " + "WHERE foodtype_food_n LIKE ? AND name_food_n LIKE ? "
					+ "ORDER BY kcal_food_n/servingsize_food_n";
			try {
				// ingredient ¿µ¾ç±¸¼ººñ ÃßÃâ
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, "%" + ingredientGoon + "%");
				pstmt.setString(2, "%" + ingredient + "%");
				rs = pstmt.executeQuery();
				rs.next();
				double dang_ratio = (rs.getDouble("dang_ratio"));
				double dan_ratio = (rs.getDouble("dan_ratio"));
				double zhifang_ratio = (rs.getDouble("zhifang_ratio"));
				// ÃßÃâÇÑ ¿µ¾ç±¸¼ººñ¿Í °¡Àå À¯»çÇÑ ½ÄÀç·á Ã£±â
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
				return "sql ¿À·ù";
			}
			matchedMenu = ingredient_knn + groumet;
		}
		return matchedMenu;
	}

}
