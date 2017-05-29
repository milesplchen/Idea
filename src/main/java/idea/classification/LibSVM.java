/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.classification;

import java.io.*;
import java.util.StringTokenizer;

import libsvm.*;

/**
 * LibSVM 相關操作.
 *
 * @author Miles Chen
 */
public class LibSVM {
	/** SVM 參數. */
	protected svm_parameter param;
	/** SVM 資料格式. */
	protected svm_problem prob;
	/** Model 檔名稱. */
	protected String model_name;

	/** 各 feature 最大值. */
	protected double[] max;
	/** 各 feature 最小值. */
	protected double[] min;

	/** Constructor. */
	public LibSVM() {
		setDefaultParam();
	}

	/** 設定 default SVM 參數. */
	public void setDefaultParam() {
		param = new svm_parameter();

		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;	// 特徵數夠多時使用
		param.degree = 3;							// 1 - 11
//		param.gamma = 1.0 / prob.x[0].length;		// 特徵數的倒數 (0.1 0.2 0.4 0.6 0.8 1.6 3.2 6.4 12.8 的倒數)
		param.coef0 = 0;							// 0.2 0.4 0.6 0.8 1
		param.C = 1;								// 0.0001 - 10000 (越大則對錯誤的懲罰越大，會 overfit)
		param.nu = 0.5;
		param.p = 0.1;
		param.cache_size = 100;
		param.eps = 1e-3;
		param.shrinking = 1;
		param.probability = 0;						// 若要估計分到每個類的概率則設為 1
//		param.nr_weight = 0;						// weight*C, for C-SVC (default 1)
//		param.weight_label = new int[0];
//		param.weight = new double[0];
	}

	/** 設定 default SVR 參數. */
	public void setDefaultParamSVR() {
		param.svm_type = svm_parameter.EPSILON_SVR;
//		param.kernel_type = svm_parameter.RBF;		// 將樣本映射到高維空間
		param.p = 0.1;
	}

	/**
	 * 自訂參數.
	 *
	 * @param c cost 參數
	 * @param g gamma 參數
	 */
	public void setParam(double c, double g) {
		param.C = c;
		param.gamma = g;
		param.p = 0.1;
	}

	/**
	 * train model.
	 *
	 * @param model_name model 檔名稱
	 * @throws IOException 檔案寫入錯誤
	 */
	public void train(String model_name) throws IOException {
//		System.out.println("Training......");

		this.model_name = model_name;
		System.out.println(svm.svm_check_parameter(prob, param));	// 若參數沒問題，則 return null，否則 return error 描述
		svm_model model = svm.svm_train(prob, param);
		svm.svm_save_model(this.model_name, model);					// 將訓練好的 model 存檔
	}

	/**
	 * 用 command line 的方式 train model.
	 *
	 * @param args 參數
	 * @throws IOException 檔案寫入錯誤
	 */
/*	public void train(String[] args) throws IOException {
		this.model_name = svm_train.main(args);
	}*/

	/**
	 * n-fold cross validation.
	 *
	 * @param fold n-fold
	 * @return 預測的結果
	 */
	public double[] crossValidation(int fold) {
		System.out.println(svm.svm_check_parameter(prob, param));	// 若參數沒問題，則 return null，否則 return error 描述

		double[] target = new double[prob.l];
		svm.svm_cross_validation(prob, param, fold, target);

		return target;
	}

	/**
	 * test 訓練的結果.
	 *
	 * @param model_name model 檔名稱
	 * @return 預測的結果
	 * @throws IOException 檔案讀取錯誤
	 */
	public double[] test(String model_name) throws IOException {
//		System.out.println("Testing......");
		this.model_name = model_name;

		svm_model model = svm.svm_load_model(this.model_name);
		double[] pred = new double[prob.l];		// 預測的結果

		for (int i = 0; i < prob.l; i++)
			pred[i] = svm.svm_predict(model, prob.x[i]);

		return pred;
	}

	/**
	 * 計算預測結果的準確度.
	 * 包含: accuracy, precision, sensitivity, specificity, F-measure.
	 *
	 * @param pred 預測結果
	 * @return accuracy
	 */
	public double performance(double[] pred) {
		int tp = 0, tn = 0, fp = 0, fn = 0;

		for (int i = 0; i < prob.l; i++) {
			if (pred[i] == prob.y[i] && pred[i] != 0)
				tp++;
			else if (pred[i] == prob.y[i] && pred[i] == 0)
				tn++;
			else if (pred[i] != prob.y[i] && pred[i] != 0)
				fp++;
			else if (pred[i] != prob.y[i] && pred[i] == 0)
				fn++;
		}

		return performance(tp, tn, fp, fn);
	}

	/**
	 * 計算各項準確度.
	 * 包含: accuracy, precision, sensitivity, specificity, F-measure.
	 *
	 * @param tp true positive 數
	 * @param tn true negative 數
	 * @param fp false positive 數
	 * @param fn false negative 數
	 * @return accuracy
	 */
	public static double performance(int tp, int tn, int fp, int fn) {
		if (tp + fn == 0 || tn + fp == 0) return 0;

		double accuracy = (double)(tp + tn) / (tp + tn + fp + fn) * 100;
		double precision = (double)tp / (tp + fp) * 100;
		double sensitivity = (double)tp / (tp + fn) * 100;		// recall
		double specificity = (double)tn / (tn + fp) * 100;
		double b = 1;
		double fmeasure = (1 + b*b) * precision * sensitivity / (b*b * precision + sensitivity);

		System.out.printf("Accuracy = %1.3f%% (%d/%d)\n", accuracy, tp + tn, tp + tn + fp + fn);
		System.out.printf("Precision = %1.3f%% (%d/%d)\n", precision, tp, tp + fp);
		System.out.printf("Sensitivity = %1.3f%% (%d/%d)\n", sensitivity, tp, tp + fn);
		System.out.printf("Specificity = %1.3f%% (%d/%d)\n", specificity, tn, tn + fp);
		System.out.printf("F-measure = %1.3f%%\n", fmeasure);

		return accuracy;
	}

	/**
	 * 計算 mean square error.
	 *
	 * @param pred 預測結果
	 * @return mean square error
	 */
	public double mse(double[] pred) {
		double err = 0;

		for (int i = 0; i < prob.l; i++)
			err += Math.pow(pred[i] - prob.y[i], 2);

		return err / prob.l;
	}

	/** 以預設值計算不同參數的準確率. */
	public void grid() {
		grid(-5, 15, 2, -15, 3, 2, 5);
	}

	/**
	 * 計算不同參數的準確率.
	 *
	 * @param c_begin cost 參數起始值 (2^c_begin)
	 * @param c_end   cost 參數最大值 (2^c_end)
	 * @param c_step  cost 參數每次遞增值 (c_begin += c_step)
	 * @param g_begin gamma 參數起始值 (2^g_begin)
	 * @param g_end   gamma 參數最大值 (2^g_end)
	 * @param g_step  gamma 參數每次遞增值 (g_begin += g_step)
	 * @param fold    n-fold cross validation
	 */
	public void grid(int c_begin, int c_end, int c_step, int g_begin, int g_end, int g_step, int fold) {
		double c_best = 0;
		double g_best = 0;
		double acc_best = -1;

		for (int c1 = c_begin; c1 < c_end; c1 += c_step) {
			double c = Math.pow(2, c1);

			for (int g1 = g_begin; g1 < g_end; g1 += g_step) {
				double g = Math.pow(2, g1);

				setParam(c, g);
				double acc = performance(crossValidation(fold));
				System.out.println(c + " " + g + " "+ acc);

				if (acc > acc_best) {
					acc_best = acc;
					c_best = c;
					g_best = g;
				}
			}
		}

		System.out.printf("%g %g %g\n", c_best, g_best, acc_best);
	}

	/**
	 * 將 svm_problem 裡的資料存成 SVM 格式檔.
	 *
	 * @param filename 存檔名稱
	 * @throws IOException 存檔錯誤
	 */
	public void saveSVMFormat(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

		for (int i = 0; i < prob.l; i++) {
			bw.write(String.valueOf((int)prob.y[i]));
			for (int j = 0; j < prob.x[i].length; j++) {
				bw.write(' ');
				bw.write(String.valueOf(prob.x[i][j].index));
				bw.write(':');
				bw.write(String.valueOf((int)prob.x[i][j].value));
			}
			bw.newLine();
		}

		bw.close();

//		System.out.println("Data saved as SVM format.");
	}

	/** 計算各 feature 的極大值、極小值. */
	public void extrema() {
		max = new double[prob.x[0].length];
		min = new double[prob.x[0].length];

		for (int i = 0; i < min.length; i++) {
			min[i] = 1000000000;
			max[i] = -1000000000;
		}

		for (int i = 0; i < prob.l; i++)
			for (int j = 0; j < prob.x[i].length; j++) {
				if (prob.x[i][j].value > max[j])
					max[j] = prob.x[i][j].value;
				if (prob.x[i][j].value < min[j])
					min[j] = prob.x[i][j].value;
			}
	}

	/** 將 feature 正規化. */
	public void normalize() {
		for (int i = 0; i < prob.l; i++)
			for (int j = 0; j < prob.x[i].length; j++)
				prob.x[i][j].value = (prob.x[i][j].value - min[j]) / (max[j] - min[j]);
	}

	/**
	 * 將極值存成檔案.
	 *
	 * @param filename 檔案名稱
	 * @throws IOException 檔案寫入錯誤
	 */
	public void saveExtrema(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

		bw.write(max.length + "\n");
		for (int i = 0; i < max.length; i++)
			bw.write(max[i] + "\t" + min[i] + "\n");

		bw.close();
	}

	/**
	 * 從檔案載入極值.
	 *
	 * @param filename 檔案名稱
	 * @throws IOException 檔案讀取錯誤
	 */
	public void loadExtrema(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;

		if ((line = br.readLine()) != null) {
			int len = Integer.parseInt(line);
			max = new double[len];
			min = new double[len];
		}

		for (int i = 0; (line = br.readLine()) != null; i++) {
			StringTokenizer st = new StringTokenizer(line);

			if (st.countTokens() >= 2) {
				max[i] = Double.parseDouble(st.nextToken());
				min[i] = Double.parseDouble(st.nextToken());
			}
		}

		br.close();
	}
}
