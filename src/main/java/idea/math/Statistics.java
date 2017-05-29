/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.math;

import org.apache.commons.math3.stat.inference.*;

/**
 * 各種統計運算.
 *
 * @author Miles Chen
 */
public class Statistics {
	/** 90% z-score. */
	public static final double Z90 = 1.645;
	/** 95% z-score. */
	public static final double Z95 = 1.96;
	/** 99% z-score. */
	public static final double Z99 = 2.575;

	/**
	 * 計算 odds ratio.
	 *
	 * @param ee case 中 event 發生的次數
	 * @param en case 中 event 沒發生的次數
	 * @param ce control 中 event 發生的次數
	 * @param cn control 中 event 沒發生的次數
	 * @return odds ratio
	 */
	public static double oddsRatio(double ee, double en, double ce, double cn) {
		if (ee == 0 || en == 0 || ce == 0 || cn == 0)
			return 0;
		return (ee / en) / (ce / cn);
	}

	/**
	 * 計算 confidence interval.
	 *
	 * @param ee case 中 event 發生的次數
	 * @param en case 中 event 沒發生的次數
	 * @param ce control 中 event 發生的次數
	 * @param cn control 中 event 沒發生的次數
	 * @param z  z-score
	 * @return confidence interval 的 lower bound 和 upper bound
	 */
	public static double[] confidenceInterval(double ee, double en, double ce, double cn, double z) {
		if (ee == 0 || en == 0 || ce == 0 || cn == 0)
			return new double[]{0, 0};

		double or = oddsRatio(ee, en, ce, cn);

		double a = Math.log(or);
		double b = z * Math.sqrt(1 / ee + 1 / en + 1 / ce + 1 / cn);

		double lower = Math.exp(a - b);
		double upper = Math.exp(a + b);

		return new double[]{lower, upper};
	}

	/**
	 * 計算 p-value.
	 *
	 * @param count  array representation of 2-way table
	 * @return p-value
	 */
	public static double pValue(long[][] count) {
		ChiSquareTest x2 = new ChiSquareTest();
		return x2.chiSquareTest(count);
	}

	/**
	 * 計算 p-value.
	 *
	 * @param expe  case 的 event, non-event 個數
	 * @param cont  control 的 event, non-event 個數
	 * @return p-value
	 */
	public static double pValue(long[] expe, long[] cont) {
		long[] observed = new long[expe.length + cont.length];
		long sum = 0;
		double[] expected = new double[expe.length + cont.length];

		int idx = 0;
		for (int i = 0; i < expe.length; i++) {
			if (expe[i] == 0)
				return 1;

			observed[idx++] = expe[i];
			sum += expe[i];
		}
		for (int i = 0; i < cont.length; i++) {
			if (cont[i] == 0)
				return 1;

			observed[idx++] = cont[i];
			sum += cont[i];
		}

		double e0e1 = (double)(expe[0] + expe[1]) / sum;
		double c0c1 = (double)(cont[0] + cont[1]) / sum;
		double e0c0 = (double)(expe[0] + cont[0]) / sum;
		double e1c1 = (double)(expe[1] + cont[1]) / sum;

		expected[0] = sum * e0e1 * e0c0;
		expected[1] = sum * e0e1 * e1c1;
		expected[2] = sum * c0c1 * e0c0;
		expected[3] = sum * c0c1 * e1c1;

		ChiSquareTest x2 = new ChiSquareTest();
		return x2.chiSquareTest(expected, observed);
	}
}
