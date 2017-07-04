/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import java.util.*;

/**
 * 處理發票商品資訊格式.
 *
 * @author Miles Chen
 */
public class Product {
	/** 商品名稱. */
	public String name;
	/** 價格. */
	public int price;

	/**
	 * 新增一個商品.
	 *
	 * @param n 商品名聲
	 * @param p 商品價格
	 */
	public Product(String n, int p) {
		name = n;
		price = p;
	}

	/**
	 * 將發票上的商品名稱拆為名稱與價格.
	 *
	 * @param str 發票上的商品名稱
	 */
	public Product(String str) {
		price = getPrice(str);
		name = delPrice(str);
	}

	/**
	 * 刪除字串中商品的價格.
	 *
	 * @param str 有價格的商品名稱
	 * @return 刪除價格後的商品名稱
	 */
	public static String delPrice(String str) {
		return str.replaceAll("\\(\\$-?[\\d x]+\\)", "");
	}

	/**
	 * 取出字串中商品的價格.
	 *
	 * @param str 有價格的商品名稱
	 * @return 商品價格
	 */
	public static int getPrice(String str) {
		int price = 0;

		if (str.matches(".+\\$\\d+.+"))
			price = Integer.parseInt( str.replaceAll(".+\\$(\\d+).+", "$1") );

		return price;
	}

	/**
	 * 刪除會造成分類錯誤的字元.
	 *
	 * @param str 發票上的商品名稱
	 * @return 刪除會造成分類錯誤的字元後的商品名稱
	 */
	public static String delAmbi(String str) {
		String s = str.replaceAll("[() /-]+", "");	// 刪除符號
		s = s.replaceAll("-?[0-9.]+", "");			// 刪除數字
		s = s.replaceAll("[A-Za-z]+", "");			// 刪除英文
		return s;
	}

	/**
	 * 取得刪除會造成分類錯誤的字元後的商品名稱.
	 *
	 * @return 刪除會造成分類錯誤的字元後的商品名稱
	 */
	public String getName() {
		return delAmbi(name);
	}

	/**
	 * 刪除字串中非商品的部分.
	 *
	 * @param str 欲處理的字串
	 * @return 刪除非商品後的字串
	 */
	public static String delNongoods(String str) {
		String s = str.replaceFirst("^發票資訊：", "");
		s = s.replaceAll("，折扣[^，]*", "");
		return s;
	}

	/**
	 * 將商品列表字串拆成商品 list.
	 *
	 * @param str 欲處理的字串
	 * @return 商品 list
	 */
	public static List<Product> splitInvoice(String str) {
		List<Product> goods_list = new ArrayList<Product>();	// 商品列表

		StringTokenizer st = new StringTokenizer( delNongoods(str), "，、");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();		// 商品
			Product p = new Product(tok);
			goods_list.add(p);
		}

		return goods_list;
	}
}
