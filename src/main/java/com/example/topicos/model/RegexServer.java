package com.example.topicos.model;

public enum RegexServer {
	
	A("(^[a-z]{3}[0-9]{0,1}-[a-z]{3}[0-9]{0,1})"),
	B("(^[a-z]{1}[0-9]{1}-[a-z]{3})"),
	C("(^[a-z]{3}[0-9]{0,1}[a-z]{1}.[a-z]{1}.[a-z]{4})"),
	D("(^[a-z]{4}[0-9]{1}.[a-z]{5})"),
	E("(^[a-z]{1}[0-9]{2}.[A-Z]{3}.)"),
	F("(^[A-Z]{3}.[a-z]{2}.[a-z]{1}.[a-z]{4})"),
	G("(^[a-z]{5}-[a-z]{3}[0-9]{1})"),
	H("(^[0-9]{3}.[a-z]{3}.)"),
	I("(^[a-z]{1}[0-9]{1}.[a-z]{3})"),
	J("(^[a-z]{3}[0-9]{1}-[a-z]{3}[0-9]{1})"),
	K("(^[a-z]{2}[0-9]{1}.[a-z]{2}-[a-z]{3}.)"),
	L("(^[a-z]{2}-[a-z]{3}-[a-z]{2})"),
	M("(^[A-Z]{1}-[A-Z]{3}-)");
	
	private final String regexServer;

	RegexServer(String regexServer) {
		this.regexServer = regexServer;
	}

	public String getRegexServer() {
		return regexServer;
	}
}
