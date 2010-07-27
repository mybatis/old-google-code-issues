package testdomain;

import com.ibatis.common.util.PaginatedList;

import java.util.List;
import java.util.Map;

public interface DocumentMapper {

  List getDocuments ();

  Document getDocument (int id);

  void insertDocument(Document doc);

  void updateDocument(Document doc);

  void deleteDocument(Document doc);

  List getDocuments(Object aNull, int i, int i1);

  PaginatedList getDocuments(Object aNull, int i);

  Map getDocuments(Object aNull, String i);

  Map getDocuments(Object aNull, String i, String i1);
  
}
