package com.github.spocot.limabean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LibHandler implements Runnable{

	private String url;
	private String folder;

	public LibHandler(String url, String folder){
		this.url = url;
		this.folder = folder;
	}

	public List<String> getLibs() throws MalformedURLException, IOException{
		List<String> libs = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(URI.create(this.url + "/filelist.txt").toURL().openStream()));
		String libName;
		System.out.println("Required Libraries:");
		while((libName = br.readLine()) != null){
			libs.add(libName);
			System.out.println("\t" + libName);
		}
		br.close();
		return libs;
	}

	public static void downloadFile(String url, String outputFile) throws IOException{
		new File(outputFile).getParentFile().mkdirs();
		URL site = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(site.openStream());
		FileOutputStream fos = new FileOutputStream(outputFile);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

	public void downloadLibs(List<String> libs){
		System.out.println("Downloading Libraries:");
		for(String lib : libs){
			try {
				System.out.println("\t" + this.url + "/" + lib);
				downloadFile(this.url + "/" + lib, this.folder + File.separatorChar + "libs" + File.separatorChar + lib);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void extractLibs(){
		
		String output = this.folder + File.separatorChar + "bin";
		
		File[] libs = new File(this.folder + File.separatorChar + "libs").listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".zip");
			}

		});

		System.out.println("Extracting Libraries:");
		for(File lib : libs){
			System.out.println("\t" + lib.getAbsolutePath());
			try{
				ZipInputStream zis = new ZipInputStream(new FileInputStream(lib));
				ReadableByteChannel rbc = Channels.newChannel(zis);
				ZipEntry ze;
				while((ze = zis.getNextEntry()) != null){
					String entryName = ze.getName();
					if(ze.isDirectory()){
						System.out.println("\t\t" + output + File.separatorChar + entryName);
						new File(output + File.separatorChar + entryName).mkdirs();
						continue;
					}
					System.out.println("\t\t\t" + output + File.separatorChar + entryName);
					FileOutputStream fos = new FileOutputStream(output + File.separatorChar + entryName);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					zis.closeEntry();
				}
				rbc.close();
				zis.close();
			}catch(IOException e){
				e.printStackTrace();
				continue;
			}
		}
	}

	@Override
	public void run() {
		List<String> libs = null;
		try {
			libs = this.getLibs();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(libs == null){
				return;
			}
		}
		
		this.downloadLibs(libs);
		this.extractLibs();
	}
}
