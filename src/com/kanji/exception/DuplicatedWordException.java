package com.kanji.exception;

import com.sun.org.apache.bcel.internal.generic.DUP;

public class DuplicatedWordException extends Exception {

	public DuplicatedWordException (String message){
		super(message);
	}
}
