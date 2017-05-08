package com.util.loader;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.VirtualWebappLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CatalinaWebappLoader extends VirtualWebappLoader {

	private static final String JAR_EXTENSION = ".jar";
	private static final String PATH_SEPARATOR = "//";
	private static final String JAVA_VERSION = "java.version";
	private String versionedClasspath = null;
	private static final Log log = LogFactory.getLog(CatalinaWebappLoader.class);

	@Override
	protected void startInternal() throws LifecycleException {

		final String javaVersion = System.getProperty(JAVA_VERSION);

		log.info("[java version= " + javaVersion + "]");
		try {
			if (null != javaVersion && !"".equals(javaVersion) && getVersionedClasspath() != null) {
				File versionedJarLocationDirectory = getVersionedJarDirectory(javaVersion);

				log.info("versioned path = " + versionedJarLocationDirectory.toURI().toString());
				if (versionedJarLocationDirectory.isDirectory()) {
					String[] filenames = versionedJarLocationDirectory.list();
					Arrays.sort(filenames);
					for (String fileName : filenames) {

						String aFile = fileName.toLowerCase(Locale.ENGLISH);

						if (aFile.endsWith(JAR_EXTENSION)) {
							File aJarFile = getFile(versionedJarLocationDirectory, fileName);
							if (aJarFile.isFile()) {
								addRepository(aJarFile.toURI().toString());
								log.info("loading file = " + fileName);
							}
						}

					}
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		super.startInternal();
	}

	protected File getFile(File versionedJarLocationDirectory, String fileName) {
		return new File(versionedJarLocationDirectory, fileName);
	}

	protected File getVersionedJarDirectory(final String javaVersion) {
		return new File(getVersionedClasspath() + PATH_SEPARATOR + javaVersion);
	}

	public String getVersionedClasspath() {
		return versionedClasspath;
	}

	public void setVersionedClasspath(String versionedClasspath) {
		this.versionedClasspath = versionedClasspath;
	}
}
