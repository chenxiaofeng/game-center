package com.x.publics.utils;

public class PatchUtils {

	static {
		try {
			System.loadLibrary("apkPatch");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * native方法
	 * 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * @param oldApkPath
	 * @param newApkPath
	 * @param patchPath
	 * @return
	 */
	public static native int patch(String oldApkPath, String newApkPath, String patchPath);
}
