package edu.usc.softarch.arcade.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class FileUtil {
	private static final class ForwardVersionComparator implements
			Comparator<File> {
		public int compare(File o1, File o2) {
			String version1 = extractVersion(o1.getName());
			String[] parts1 = version1.split("\\.");

			String version2 = extractVersion(o2.getName());
			String[] parts2 = version2.split("\\.");

			int minLength = parts1.length > parts2.length ? parts2.length
					: parts1.length;
			for (int i = 0; i < minLength; i++) {
				try {
					Integer part1 = Integer.parseInt(parts1[i]);
					Integer part2 = Integer.parseInt(parts2[i]);
					int compareToVal = part1.compareTo(part2);
					if (compareToVal != 0) {
						logger.debug("compareTo " + version1 + " to "
									+ version2 + ": " + compareToVal);
						return compareToVal;
					}
				} catch (NumberFormatException e) {
					logger.debug("Invalid part using string comparison for "
									+ version1
									+ " to "
									+ version2
									+ ": "
									+ version1.compareTo(version2));
					return version1.compareTo(version2);
				}
			}
			return version1.compareTo(version2);
		}
	}
	
	private static final class ReverseVersionComparator implements
			Comparator<File> {
		public int compare(File o1, File o2) {
			String version1 = extractVersion(o1.getName());
			String[] parts1 = version1.split("\\.");

			String version2 = extractVersion(o2.getName());
			String[] parts2 = version2.split("\\.");

			int minLength = parts1.length > parts2.length ? parts2.length
					: parts1.length;
			for (int i = 0; i < minLength; i++) {
				try {
					Integer part1 = Integer.parseInt(parts1[i]);
					Integer part2 = Integer.parseInt(parts2[i]);
					int compareToVal = part1.compareTo(part2);
					if (compareToVal != 0) {
						System.out.println("compareTo " + version1 + " to "
								+ version2 + ": " + compareToVal);
						return compareToVal;
					}
				} catch (NumberFormatException e) {
					System.out
							.println("Invalid part using string comparison for "
									+ version1
									+ " to "
									+ version2
									+ ": "
									+ version2.compareTo(version1));
					return version2.compareTo(version1);
				}
			}
			return version2.compareTo(version1);
		}
}

	static Logger logger = Logger.getLogger(FileUtil.class);
	
	public static String extractFilenamePrefix(String filename) {
		return filename.substring( filename.lastIndexOf(File.separatorChar)+1,filename.lastIndexOf(".") );
	}

	public static String extractFilenameSuffix(String filename) {
		return filename.substring( filename.lastIndexOf("."),filename.length() );
	}
	
	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public static String getPackageNameFromJavaFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String packageName = findPackageName(line);
			return packageName;
		}
		return null;
	}

	public static String findPackageName(String test1) {
		Pattern pattern = Pattern.compile("\\s*package\\s+(.+)\\s*;\\s*");
		Matcher matcher = pattern.matcher(test1);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

	public static String tildeExpandPath(String path) {
		if (path.startsWith("~" + File.separator)) {
			path = System.getProperty("user.home") + path.substring(1);
		}
		return path;
	}
	
	public static List<File> sortFileListByVersion(
			List<File> inList) {
		List<File> outList = new ArrayList<File>(inList);
		
		Collections.sort(outList, new ForwardVersionComparator());

		return outList;
	}
	
	public static String extractVersion(String name) {
		Pattern p = Pattern.compile("[0-9]+\\.[0-9]+(\\.[0-9]+)*");
		Matcher m = p.matcher(name);
		if (m.find()) {
			return m.group(0);
		}
		return null;
	}
	
	public static String extractVersionFromFilename(String versionSchemeExpr,
			String filename) {
		String version="";
		Pattern p = Pattern.compile(versionSchemeExpr);
		Matcher m = p.matcher(filename);
		if (m.find()) {
			version = m.group(0);
			logger.trace(version + " is the version of " + filename);
		}
		return version;
	}
}
