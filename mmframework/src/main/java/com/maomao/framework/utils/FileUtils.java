/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maomao.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileUtils {
	/**
	 * 保存字符串到文件中
	 * 
	 * @param s
	 * @param filename
	 * @return
	 */
	public static boolean saveString2File(String s, String filename) {
		return saveString2File(s, new File(filename));
	}

	public static boolean saveString2File(String s, File file) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			writer.write(s);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	public static void copyFile(File srcFile, File detFolder) {
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try {
			String fileName = srcFile.getName();
			String targetFileName = detFolder.getAbsolutePath() + File.separatorChar + fileName;
			bin = new BufferedInputStream(new FileInputStream(srcFile));
			bout = new BufferedOutputStream(new FileOutputStream(new File(targetFileName)));
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = bin.read(buffer, 0, 1024)) != -1) {
				bout.write(buffer, 0, count);
			}
			bout.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bin != null)
					bin.close();
				if (bout != null)
					bout.close();
			} catch (Exception e) {

			}
		}
	}

	public static void copyFiles2(String srcFolderPath, String targetFolderPath) {
		File srcFolder = new File(srcFolderPath);
		File targetFolder = new File(targetFolderPath);
		iteratorCopyFile(srcFolder, targetFolder);
	}

	private static void iteratorCopyFile(File folder, File targetFolder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				String folderName = file.getName();
				String targetFolderName = targetFolder.getAbsolutePath() + File.separatorChar + folderName;
				File f = new File(targetFolderName);
				if (!f.exists())
					f.mkdirs();
				iteratorCopyFile(file, f);
			} else {
				// 将源文件拷贝到目标目录中
				copyFile(file, targetFolder);
			}
		}
	}

	/**
	 * 在目录中查找配置文件
	 * 
	 * @param file
	 * @return
	 */
	public static File findFile(File folder, final String pattern) {
		if (folder.isFile()) {
			if (folder.getName().equals(pattern))
				return folder;
			else
				return null;
		} else if (folder.isDirectory()) {
			File[] subFiles = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory())
						return true;
					if (pathname.getName().equals(pattern))
						return true;
					return false;
				}
			});

			if (subFiles != null && subFiles.length > 0) {
				File found = null;
				for (File f : subFiles) {
					if (f.isDirectory()) {
						found = findFile(f, pattern);
					} else if (f.getName().equals(pattern)) {
						found = f;
					}

					if (found != null) {
						break;
					}
				}
				return found;
			}
		}

		return null;
	}

	/**
	 * 删除一个Folder及Folder中的所有文件
	 * 
	 * @param file
	 */
	public static void removeFile(File path) {
		if (!path.exists())
			return;

		if (path.isFile()) {
			path.delete();
			return;
		}

		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			removeFile(files[i]);
		}

		path.delete();
	}
}
