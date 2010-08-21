package oracle.toplink.sessions;

import oracle.toplink.queryframework.ReportQuery;
import oracle.toplink.exceptions.TopLinkException;

import java.util.List;

public class Session {
  public List executeQuery(ReportQuery query) {
    return null;
  }

  public UnitOfWork acquireUnitOfWork() throws TopLinkException {
    return null;
  }

  public void release() throws TopLinkException {
  }

  public boolean isConnected() {

    return false;
  }
}
