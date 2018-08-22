package com.princeli.demo.mvc.action;

import java.util.HashMap;
import java.util.Map;
import com.princeli.demo.mvc.service.IQueryService;
import com.princeli.framework.annotation.Autowried;
import com.princeli.framework.annotation.Controller;
import com.princeli.framework.annotation.RequestMapping;
import com.princeli.framework.annotation.RequestParam;
import com.princeli.framework.webmvc.ModelAndView;

/**
 * 公布接口url
 * @author Tom
 *
 */
@Controller
@RequestMapping("/")
public class PageAction {

	@Autowried
	IQueryService queryService;
	
	@RequestMapping("/first.html")
	public ModelAndView query(@RequestParam("teacher") String teacher){
		String result = queryService.query(teacher);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("teacher", teacher);
		model.put("data", result);
		model.put("token", "123456");
		return new ModelAndView("first.html",model);
	}
	
}
