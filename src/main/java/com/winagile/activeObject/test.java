package com.winagile.activeObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.icu.text.SimpleDateFormat;

public class test {

	public static void main(String[] args) {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		now.getTime();
		System.out.println(String.valueOf(now.getTime()));

		Date date = new Date(now.getTime());
		System.out.println(String.valueOf(date.getTime()));
		
		List<String> te = new ArrayList<String>();
		te.add("xx");
		te.add("yy");
		
		System.out.println(te.toString());
	}
}
