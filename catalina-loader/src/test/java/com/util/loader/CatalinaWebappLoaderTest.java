package com.util.loader;

import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.directory.DirContext;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import com.util.loader.CatalinaWebappLoader;

public class CatalinaWebappLoaderTest {

	private static final String STP_PATH = "D://stp//java";

	CatalinaWebappLoader loader;
	
	@Mock
	File mockedDir;
	
	@Mock
	File mockedJarFile;
	
	@Mock
	StandardContext mockedContainer;
	
	@Mock
	StandardContext mockedParent;
	
	@Mock
	DirContext mockedDirContext;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		//Faking the testing impediments
		loader = new CatalinaWebappLoader(){
			protected File getVersionedJarDirectory(final String javaVersion) {
				//This is mocked at runtime
				try {
					when(mockedDir.toURI()).thenReturn(new URI("file://"+getVersionedClasspath()+"/"+javaVersion));
				} catch (URISyntaxException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
				return mockedDir;
			}
			
			protected synchronized void setState(LifecycleState state){
			}
			
			protected File getFile(File versionedJarLocationDirectory, String fileName) {
				return mockedJarFile;
			}
		};
		
		loader.setContainer(mockedContainer);
		
		//Stub out system class loader
		when(mockedContainer.getResources()).thenReturn(mockedDirContext);
		when(mockedContainer.getParent()).thenReturn(mockedParent);
		when(mockedContainer.getName()).thenReturn("test");
	}
	
	
	
	@Test
	public void does_not_load_anything_if_version_path_NOT_set() throws Exception {
			
		loader.setVersionedClasspath(null);
		loader.startInternal();
		
		verify(mockedJarFile, never()).isFile();
		verify(mockedJarFile, never()).toURI();
	}
	
	@Test
	public void does_not_load_non_jar_files() throws Exception {
		
		when(mockedDir.isDirectory()).thenReturn(true);
		when(mockedDir.list()).thenReturn(new String[]{"mssqljdbc42.txt", "mssqljdbc43.doc", "mssql.dll"});
		 
		loader.setVersionedClasspath(null);
		loader.startInternal();
		
		verify(mockedJarFile, never()).isFile();
		verify(mockedJarFile, never()).toURI();
	}
	
	@Test
	public void loades_all_jars_from_version_directory() throws Exception {
		when(mockedDir.isDirectory()).thenReturn(true);
		when(mockedDir.list()).thenReturn(new String[]{"mssqljdbc42.jar", "mssqljdbc43.jar", "mssql.dll"});
		
		when(mockedJarFile.isFile()).thenReturn(true);
		when(mockedJarFile.toURI()).thenReturn(new URI("file://msqljdbc42.jar"));
		
		loader.setVersionedClasspath(STP_PATH);
		loader.startInternal();
		
		verify(mockedJarFile, new Times(2)).isFile();
		verify(mockedJarFile, new Times(2)).toURI();
	}
}
