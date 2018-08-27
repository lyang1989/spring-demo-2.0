package com.princeli.demo.mvc.service;


/**
 * @program: spring-demo-2.0
 * @description: 修改服务
 * @author: ly
 * @create: 2018-08-23 15:05
 **/
public interface IModifyService {

	/**
	 * 增加
	 */
	public String add(String name, String addr);
	
	/**
	 * 修改
	 */
	public String edit(Integer id, String name);
	
	/**
	 * 删除
	 */
	public String remove(Integer id);
	
}
