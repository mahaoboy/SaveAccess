package com.winagile.activeObject;

import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;

public class test {
	
	public static void main(String[] args){
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sf.format(now));
	}
}
