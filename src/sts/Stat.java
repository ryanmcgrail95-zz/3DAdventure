package sts;

import func.Math2D;

public class Stat {
	private static int coinNumber;
	private static int hp, maxHP, fp, maxFP, bp;
	
	public static void initialize() {
		hp = maxHP = 10;
		fp = maxHP = 10;
		bp = 3;
	}
	
	
	// Stats
	public static int attack() {
		return 1;
	}
	public static int defense() {
		return 0;
	}
	
	// Coins
		public static int setCoinNum(int amt) {
			coinNumber = (int) Math2D.contain(0, amt, 999);
			return coinNumber;
		}
		public static int addCoin() {
			return addCoin(1);
		}
		public static int addCoin(int number) {
			return setCoinNum(coinNumber+number);
		}
		public static int getCoinNum() {
			return coinNumber;
		}

		
	// Get Stat Info
		public static int getHP() {
			return hp;
		}
		public static int getMaxHP() {
			return maxHP;
		}
		
		public static int getFP() {
			return fp;
		}
		public static int getMaxFP() {
			return maxFP;
		}
		
		public static int getBP() {
			return bp;
		}
}
