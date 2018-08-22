package com.princeli.demo.mvc.service.impl;


import com.princeli.demo.mvc.service.IDemoService;
import com.princeli.framework.annotation.Service;

@Service
public class DemoService implements IDemoService {

	@Override
	public String get(String name) {
		return "My name is " + name;
	}

}
