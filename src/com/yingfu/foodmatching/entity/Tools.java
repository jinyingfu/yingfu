package com.yingfu.foodmatching.entity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	protected static String re(String word) {

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
}
