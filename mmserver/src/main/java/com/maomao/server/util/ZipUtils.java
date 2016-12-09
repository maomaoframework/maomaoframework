package com.maomao.server.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {
	private static final String PATH = String.valueOf(File.separatorChar);
	public static final int BUFFER = 2048;
	private static final String BASE_DIR = "";
	public static final String EXT = ".zip";

	private ZipUtils() {
	}

	public static void compress(File srcFile) throws Exception {
		String name = srcFile.getName();
		String basePath = srcFile.getParent();
		String destPath = basePath + name + EXT;
		compress(srcFile, destPath);
	}

	public static void compress(File srcFile, File destFile) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile));
		compress(srcFile, zos, BASE_DIR);
		zos.flush();
		zos.close();
	}

	public static void compress(File srcFile, String destPath) throws Exception {
		compress(srcFile, new File(destPath));
	}

	private static void compress(File srcFile, ZipOutputStream zos, String basePath) throws Exception {
		if (srcFile.isDirectory()) {
			compressDir(srcFile, zos, basePath);
		} else {
			compressFile(srcFile, zos, basePath);
		}
	}

	public static void compress(String srcPath) throws Exception {
		File srcFile = new File(srcPath);

		compress(srcFile);
	}

	public static void compress(String srcPath, String destPath) throws Exception {
		File srcFile = new File(srcPath);

		compress(srcFile, destPath);
	}

	private static void compressDir(File dir, ZipOutputStream zos, String basePath) throws Exception {
		File[] files = dir.listFiles();
		if (files.length < 1) {
			ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
			zos.putNextEntry(entry);
			zos.closeEntry();
		}

		for (File file : files) {
			compress(file, zos, basePath + dir.getName() + PATH);
		}
	}

	private static void compressFile(File file, ZipOutputStream zos, String dir) throws Exception {
		ZipEntry entry = new ZipEntry(dir + file.getName());
		zos.putNextEntry(entry);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = bis.read(data, 0, BUFFER)) != -1) {
			zos.write(data, 0, count);
		}
		bis.close();
		zos.closeEntry();
	}

	public static void unzip(String zipFile, String outputFolder) throws IOException {
		unZipFile(new File(zipFile), outputFolder);
	}

	public static void unZipFile(File file, String outputFolder) throws IOException {
		ZipFile zip = null;
		try {
			zip = new ZipFile(file);
			ZipEntry entry;
			String name;
			String filename;
			File outFile;
			File pfile;
			byte[] buf = new byte[1024];
			int len;
			InputStream is = null;
			OutputStream os = null;
			String outputFilePath;
			Enumeration<ZipEntry> en = zip.getEntries();
			while (en.hasMoreElements()) {
				entry = en.nextElement();
				name = entry.getName();
				if (!entry.isDirectory()) {
					name = entry.getName();
					filename = name;
					outputFilePath = outputFolder + File.separatorChar + filename;
					outFile = new File(outputFilePath);

					if (outFile.exists())
						break;

					pfile = outFile.getParentFile();
					if (!pfile.exists()) {
						pfile.mkdirs();
					}

					try {
						is = zip.getInputStream(entry);
						os = new FileOutputStream(outFile);
						while ((len = is.read(buf)) != -1) {
							os.write(buf, 0, len);
						}
					} finally {
						if (is != null) {
							is.close();
							is = null;
						}
						if (os != null) {
							os.close();
							os = null;
						}
					}
				}
			}
		} finally {
			if (zip != null)
				zip.close();
		}

	}
}
