package com.princeli.demo.mvc.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.princeli.demo.mvc.service.IQueryService;
import com.princeli.framework.annotation.Service;


@Service
public class QueryService implements IQueryService {

	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
