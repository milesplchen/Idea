/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import java.security.*;
import java.util.regex.*;

/**
 * 各種字串處理函式.
 *
 * @author Miles Chen
 */
public class StringUtil {
	/** MD5 編碼. */
	public final static String MD5 = "MD5";

	/** 正規表示式. */
	protected Pattern pattern;

	/**
	 * 移除字串中的 URL.
	 *
	 * @param str 欲處理的字串
	 * @return 移除 URL 後的字串
	 */
	public static String removeUrl(String str) {
		return str.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "");
	}

	/**
	 * 尋找兩字串的最長共同序列.
	 *
	 * @param str1 要比對的字串
	 * @param str2 要比對的字串
	 * @return str1 和 str2 的最長共同序列
	 */
	public static String longestCommonSubsequence(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++)
			dp[i][0] = 0;
		for (int i = 0; i <= len2; i++)
			dp[0][i] = 0;

		for (int i = 1; i <= len1; i++)				// 尋找 lcs
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1] + 1;		// 字元相同則計數+1
				else
					dp[i][j] = Math.max(dp[i][j - 1], dp[i - 1][j]);		// 字元不同，則結果和上一個一樣
			}

		char[] lcs = new char[dp[len1][len2]];		// 存放找出的 lcs
		int idx = lcs.length - 1;

		while (idx >= 0) {							// 倒推回去尋找 lcs
			if (dp[len1][len2] == dp[len1][len2 - 1])
				len2--;
			else if (dp[len1][len2] == dp[len1 - 1][len2])
				len1--;
			else {
				lcs[idx--] = str1.charAt(len1 - 1);
				len1--;
				len2--;
			}
		}

		return new String(lcs);
	}

	/**
	 * 尋找兩字串的最長連續共同序列.
	 *
	 * @param str1 要比對的字串
	 * @param str2 要比對的字串
	 * @return str1 和 str2 的最長連續共同序列
	 */
	public static String longestContiguousCommonSubsequence(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		int[][] dp = new int[len1][len2];

		for (int i = 0; i < len1; i++)					// 比較兩字串各字元
			for (int j = 0; j < len2; j++)
				if (str1.charAt(i) == str2.charAt(j))	// 若字元相同為1，否則為0
					dp[i][j] = 1;

		int longest = 0;								// 最長序列的長度
		int longest_idx = 0;							// 最長序列的起始位置

		for (int j = len2 - 1; j >= 0; j--) {			// 搜尋上半對角線
			int length = 0;		// 目前共同序列長度
			int idx = 0;		// 目前共同序列起始位置

			for (int i = 0; i < len1 && j + i < len2; i++) {
				if (dp[i][j + i] == 1) {
					if (length == 0)
						idx = i;
					length++;
					if (length > longest) {
						longest = length;
						longest_idx = idx;
					}
				}
				else
					length = 0;
			}
		}

		for (int i = 1; i < len1; i++) {				// 搜尋下半對角線
			int length = 0;		// 目前共同序列長度
			int idx = 0;		// 目前共同序列起始位置

			for (int j = 0; j < len2 && i + j < len1; j++)
				if (dp[i + j][j] == 1) {
					if (length == 0)
						idx = i + j;
					length++;
					if (length > longest) {
						longest = length;
						longest_idx = idx;
					}
				}
				else
					length = 0;
		}

		return str1.substring(longest_idx, longest_idx + longest);
	}

	/**
	 * 計算兩字串相似度.
	 * 以 longest common substring 來計算.
	 *
	 * @param str1 要比對的字串
	 * @param str2 要比對的字串
	 * @return 兩字串相似度 (0 ~ 1)
	 */
	public static double similarity(String str1, String str2) {
		String lcs = longestCommonSubsequence(str1, str2);

		return (double) lcs.length() / Math.max(str1.length(), str2.length());
	}

	/**
	 * 產生隨機字串.
	 *
	 * @param len 欲產生字串的長度
	 * @return 隨機字串
	 */
	public static String randomString(int len) {
		StringBuilder sn = new StringBuilder();
		char ascii;
		int mod;		// 選擇要產生數字或大小寫英文

		for (int i = 0; i < len; i++) {
			mod = (int)(Math.random() * 3) % 3;

			if (mod == 0)
				ascii = (char)(Math.random() * 10 + 48);	// 數字
			else if (mod == 1)
				ascii = (char)(Math.random() * 26 + 65);	// 大寫英文
			else
				ascii = (char)(Math.random() * 26 + 97);	// 小寫英文

			sn.append(ascii);
		}

		return sn.toString();
	}

	/**
	 * 將字串加密.
	 *
	 * @param str  要加密的字串
	 * @param algo 加密演算法名稱，如: "MD5"
	 * @return 加密後的字串
	 * @throws NoSuchAlgorithmException 加密演算法名稱錯誤
	 */
	public static String encrypt(String str, String algo) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algo);		// 設定加密演算法
		String result = toHex(md.digest(str.getBytes()));		// 將字串加密
		return result;
	}

	/**
	 * 將字串轉成 16 進制.
	 *
	 * @param num 要轉換的字串
	 * @return 轉成 16 進制後的結果
	 */
	public static String toHex(byte[] num) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < num.length; i++) {
			String hex = Integer.toHexString(0xFF & num[i]);
			if (hex.length() == 1)
				result.append('0');
			result.append(hex);
		}

		return result.toString();
	}

	/**
	 * 設定正規表示式.
	 *
	 * @param regex 正規表示式
	 */
	public void compilePattern(String regex) {
		pattern = Pattern.compile(regex);
	}

	/**
	 * 尋找字串中符合正規表示式的個數.
	 *
	 * @param str 欲比對的字串
	 * @return 字串中符合正規表示式的個數
	 */
	public int countMatch(String str) {
		if (str == null)
			return 0;

		Matcher matcher = pattern.matcher(str);
		int count = 0;

		while (matcher.find())
			count++;

		return count;
	}
}
