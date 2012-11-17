package org.mybatis.spring.showcase;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mybatis.spring.showcase.before.Blog;
import org.mybatis.spring.showcase.before.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/mybatis/spring/showcase/before/applicationContext.xml")
public class TestBefore {
	
	@Autowired
	private BlogService blogService;
	
	@org.junit.Test
	public void go() {
		Blog blog = blogService.doSomethingWithABlog();
		Assert.assertEquals(1, blog.getId());
	}

}
