package com.gonzalomajo.warningimages;

import android.graphics.Bitmap;

import static com.gonzalomajo.warningimages.MainActivity.getResizedBitmap;

public class ImgUtils {
	/**
	 * @param bitmap                the Bitmap to be scaled
	 * @param threshold             the maxium dimension (either width or height) of the scaled
	 *                              bitmap
	 * @param isNecessaryToKeepOrig is it necessary to keep the original bitmap? If not recycle
	 *                              the original bitmap to prevent memory leak.
	 */

	public static Bitmap getScaledDownBitmap(Bitmap bitmap, int threshold,
											 boolean isNecessaryToKeepOrig) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int newWidth = width;
		int newHeight = height;

		if (width > height && width > threshold) {
			newWidth = threshold;
			newHeight = (int) (height * (float) newWidth / width);
		}

		if (width > height && width <= threshold) {
			//the bitmap is already smaller than our required dimension, no need to resize it
			return bitmap;
		}

		if (width < height && height > threshold) {
			newHeight = threshold;
			newWidth = (int) (width * (float) newHeight / height);
		}

		if (width < height && height <= threshold) {
			//the bitmap is already smaller than our required dimension, no need to resize it
			return bitmap;
		}

		if (width == height && width > threshold) {
			newWidth = threshold;
			newHeight = newWidth;
		}

		if (width == height && width <= threshold) {
			//the bitmap is already smaller than our required dimension, no need to resize it
			return bitmap;
		}

		return getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig);
	}
}
