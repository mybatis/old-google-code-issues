package compatibility.sqlmap;

import com.ibatis.db.sqlmap.RowHandler;
import compatibility.BaseCompat;

public class RowHanderCompat implements RowHandler {

  public void handleRow(Object object) {
    BaseCompat.println("UsingRowHandler: " + object);
  }

}
