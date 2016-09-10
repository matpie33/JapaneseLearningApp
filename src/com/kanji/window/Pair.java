package com.kanji.window;

public class Pair <O,E> {
	private O o;
	private E e;

	public Pair (O o, E e){
		this.setO(o);
		this.setE(e);
	}

	public O getO() {
		return o;
	}

	public void setO(O o) {
		this.o = o;
	}

	public E getE() {
		return e;
	}

	public void setE(E e) {
		this.e = e;
	}
	
	
	
}
