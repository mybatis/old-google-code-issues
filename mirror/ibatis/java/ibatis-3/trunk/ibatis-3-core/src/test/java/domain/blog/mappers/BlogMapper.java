package domain.blog.mappers;

import org.apache.ibatis.executor.resultset.RowLimit;

import java.util.*;

public interface BlogMapper {

  List<Map> selectAllPosts();

  List<Map> selectAllPosts(RowLimit rowLimit);

  List<Map> selectAllPosts(RowLimit rowLimit, Object param);

}
