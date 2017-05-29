/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.math;

/**
 * 時間序列分析相關函式.
 *
 * @author Miles Chen
 */
public class Series {
	/**
	 * 將序列正規化至 0 ~ 1.
	 *
	 * @param x 序列
	 */
	public static void normalize(double x[]) {
		double max = 0;
		for (int i = 0; i < x.length; i++)
			if (x[i] > max)
				max = x[i];

		if (max > 0)
			for (int i = 0; i < x.length; i++)
				x[i] /= max;
	}

	/**
	 * 計算序列的平均值.
	 *
	 * @param x 欲計算的序列
	 * @return 平均值
	 */
	public static double mean(double x[]) {
		double sum = 0;

		for (int i = 0; i < x.length; i++)
			sum += x[i];

		return (x.length <= 0)? 0 : sum / x.length;
	}

	/**
	 * 計算序列的母體變異數.
	 *
	 * @param x 欲計算的序列
	 * @return 母體變異數
	 */
	public static double variance(double x[]) {
		double avg = mean(x);
		double sum = 0;

		for (int i = 0; i < x.length; i++)
			sum += Math.pow(x[i] - avg, 2);

		return sum / x.length;
	}

	/**
	 * 計算序列的母體標準差.
	 *
	 * @param x 欲計算的序列
	 * @return 母體標準差
	 */
	public static double staDev(double x[]) {
		return Math.sqrt(variance(x));
	}

	/**
	 * 計算序列的 z-score.
	 *
	 * @param x 欲計算的序列
	 * @return z-score
	 */
	public static double[] zScore(double x[]) {
		double avg = mean(x);
		double sd = staDev(x);
		double z[] = new double[x.length];

		for (int i = 0; i < x.length; i++)
			z[i] = (x[i] - avg) / sd;

		return z;
	}
}
