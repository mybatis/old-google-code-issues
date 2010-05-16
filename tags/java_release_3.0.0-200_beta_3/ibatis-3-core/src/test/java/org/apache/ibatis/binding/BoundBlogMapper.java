package org.apache.ibatis.binding;

import domain.blog.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@CacheNamespace
public interface BoundBlogMapper {

  //======================================================

  Blog selectBlogWithPostsUsingSubSelect(int id);

  //======================================================

  int selectRandom();

  //======================================================

  @Select({
      "SELECT *",
      "FROM blog"
      })
  List<Blog> selectBlogs();

  //======================================================

  List<Blog> selectBlogsFromXML();

  //======================================================

  @Select({
      "SELECT *",
      "FROM blog"
      })
  List<Map> selectBlogsAsMaps();

  //======================================================

  @SelectProvider(type = BoundBlogSql.class, method = "selectBlogsSql")
  List<Blog> selectBlogsUsingProvider();

  //======================================================

  @Select("SELECT * FROM post ORDER BY id")
  @TypeDiscriminator(
      column = "draft",
      javaType = String.class,
      cases = {@Case(value = "1", type = DraftPost.class)}
  )
  List<Post> selectPosts();

  //======================================================

  @Select("SELECT * FROM post ORDER BY id")
  @Results({
    @Result(id = true, property = "id", column = "id")
      })
  @TypeDiscriminator(
      column = "draft",
      javaType = int.class,
      cases = {@Case(value = "1", type = DraftPost.class,
          results = {@Result(id = true, property = "id", column = "id")})}
  )
  List<Post> selectPostsWithResultMap();

  //======================================================

  @Select("SELECT * FROM " +
      "blog WHERE id = #{id}")
  Blog selectBlog(int id);

  //======================================================

  @Select("SELECT * FROM " +
      "blog WHERE id = #{id}")
  Map selectBlogAsMap(Map params);

  //======================================================

}
