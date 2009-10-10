package domain.blog.mappers;

import org.apache.ibatis.executor.resultset.RowBounds;

import java.util.*;

public interface BlogMapper {

  List<Map> selectAllPosts();

  List<Map> selectAllPosts(RowBounds rowBounds);

  List<Map> selectAllPosts(RowBounds rowBounds, Object param);

}
