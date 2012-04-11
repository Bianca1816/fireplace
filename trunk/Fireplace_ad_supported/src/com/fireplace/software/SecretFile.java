package com.fireplace.software;

import java.io.File;

public class SecretFile {

	public static boolean check(){
		return (new File("/system/etc/.fireplace").exists()) ? true : false;
	}
}
