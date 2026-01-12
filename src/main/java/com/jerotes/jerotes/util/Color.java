package com.jerotes.jerotes.util;

public class Color {
	//颜色转换
	public static int[] colorStringToRGBInt(String hexStr){
		if(hexStr != null && !"".equals(hexStr)) {
			if (hexStr.length() == 7) {
				int[] rgb = new int[3];
				rgb[0] = Integer.valueOf(hexStr.substring(1, 3), 16);
				rgb[1] = Integer.valueOf(hexStr.substring(3, 5), 16);
				rgb[2] = Integer.valueOf(hexStr.substring(5, 7), 16);
				return rgb;
			}
			if (hexStr.length() == 6) {
				int[] rgb = new int[3];
				rgb[0] = Integer.valueOf(hexStr.substring(0, 2), 16);
				rgb[1] = Integer.valueOf(hexStr.substring(2, 4), 16);
				rgb[2] = Integer.valueOf(hexStr.substring(4, 6), 16);
				return rgb;
			}
		}
		return null;
	}
	public static int[] colorIntToRGBInt(int n){
		int[] rgb = new int[3];
		rgb[0] = (n >> 16 & 0xFF);
		rgb[1] = (n >> 8 & 0xFF);
		rgb[2] = (n & 0xFF);
		return rgb;
	}
	public static int colorRGBToInt(int Red, int Green, int Blue){
		int R = Math.round(Red);
		int G = Math.round(Green);
		int B = Math.round(Blue);
		R = (R << 16) & 0x00FF0000;
		G = (G << 8) & 0x0000FF00;
		B = B & 0x000000FF;
		return 0xFF000000 | R | G | B;
	}
	public static int colorStringToInt(String hexStr){
		int[] rgb = colorStringToRGBInt(hexStr);
		int R = Math.round(rgb[0]);
		int G = Math.round(rgb[1]);
		int B = Math.round(rgb[2]);
		R = (R << 16) & 0x00FF0000;
		G = (G << 8) & 0x0000FF00;
		B = B & 0x000000FF;
		return 0xFF000000 | R | G | B;
	}
	public static int rgbToPackedInt(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}
}