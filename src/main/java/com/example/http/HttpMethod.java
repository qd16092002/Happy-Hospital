package com.example.http;

public enum HttpMethod {

	GET, //
	POST, //
	PUT, //
	DELETE, //
	HEAD;

	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return name();
	}

}