package com.princeli.demo.mvc.action;

import com.princeli.demo.mvc.service.IModifyService;
import com.princeli.demo.mvc.service.IQueryService;
import com.princeli.framework.annotation.Autowried;
import com.princeli.framework.annotation.Controller;
import com.princeli.framework.annotation.RequestMapping;
import com.princeli.framework.annotation.RequestParam;
import com.princeli.framework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/web")
public class MyAction {

	@Autowried
	IQueryService queryService;

	@Autowried
	IModifyService modifyService;

	@RequestMapping("/query.json")
	public ModelAndView query(HttpServletRequest request, HttpServletResponse response,
							  @RequestParam("name") String name){
		String result = queryService.query(name);
		System.out.println(result);
		return out(response,result);
	}

	@RequestMapping("/add*.json")
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response,
							  @RequestParam("name") String name,@RequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		return out(response,result);
	}

	@RequestMapping("/remove.json")
	public ModelAndView remove(HttpServletRequest request,HttpServletResponse response,
								 @RequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}

	@RequestMapping("/edit.json")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response,
							   @RequestParam("id") Integer id,
							   @RequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}



	private ModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
