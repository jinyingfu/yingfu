package com.yingfu.matching;



public class Main {
	public static void main(String[] args) {
		String menu = "한우불고기전골";
		String groumet = "전골";
		String ingredient = "한우";
		
		String matchingResult = MatchingDAO.matching(menu, groumet, ingredient);
		System.out.println("매칭결과 : " + matchingResult);
		
		
		
	}
}
