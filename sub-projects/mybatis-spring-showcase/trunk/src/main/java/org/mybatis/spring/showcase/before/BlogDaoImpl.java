package org.mybatis.spring.showcase.before;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class BlogDaoImpl implements BlogDao, RowMapper<Blog> {

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Blog selectBlog(int id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return (Blog) jdbcTemplate.queryForObject("select * from blog where id=?", this, 1);
	}

	public Blog mapRow(ResultSet rs, int rowNum) throws SQLException {
		Blog blog = new Blog();
		blog.setId(rs.getInt(1));
		blog.setTitle(rs.getString(2));
		return blog;
	}

}
