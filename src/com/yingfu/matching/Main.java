package com.yingfu.matching;



public class Main {
	public static void main(String[] args) {
		String menu = "�ѿ�Ұ������";
		String groumet = "����";
		String ingredient = "�ѿ�";
		
		String matchingResult = MatchingDAO.matching(menu, groumet, ingredient);
		System.out.println("��Ī��� : " + matchingResult);
		
		
		
	}
}
