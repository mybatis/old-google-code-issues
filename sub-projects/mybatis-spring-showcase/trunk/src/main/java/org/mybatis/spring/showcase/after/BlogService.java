package org.mybatis.spring.showcase.after;

public class BlogService {

	private BlogMapper blogMapper;

	public BlogMapper getBlogMapper() {
		return blogMapper;
	}

	public void setBlogMapper(BlogMapper blogMapper) {
		this.blogMapper = blogMapper;
	}

	public Blog doSomethingWithABlog() {
		return blogMapper.selectBlog(1);
	}

}
