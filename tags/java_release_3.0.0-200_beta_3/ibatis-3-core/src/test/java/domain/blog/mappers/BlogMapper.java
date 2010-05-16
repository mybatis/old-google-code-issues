package domain.blog.mappers;

import java.util.*;

public interface BlogMapper {

  List<Map> selectAllPosts(Object param);

  List<Map> selectAllPosts(int offset, int limit);

  List<Map> selectAllPosts(Object param, int offset, int limit);

}
