package org.mybatis.spring.showcase.before;

public class BlogService {

	private BlogDao blogDao;

	public BlogDao getBlogDao() {
		return blogDao;
	}

	public void setBlogDao(BlogDao blogDao) {
		this.blogDao = blogDao;
	}

	public Blog doSomethingWithABlog() {
		return blogDao.selectBlog(1);
	}

}
