package com.github.spocot.limabean;

import java.io.File;

public class Test {

	public static void main(String[] args){
		LibHandler lh = new LibHandler("http://www.ohpul.com/xoda/files",System.getProperty("user.home") + File.separatorChar + "LiMa Bean");
		Thread t = new Thread(lh);
		t.start();
	}
}
