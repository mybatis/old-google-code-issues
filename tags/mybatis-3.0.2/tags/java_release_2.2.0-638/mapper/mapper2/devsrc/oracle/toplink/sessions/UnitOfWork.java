package oracle.toplink.sessions;

import oracle.toplink.exceptions.TopLinkException;

public class UnitOfWork {
  public boolean isActive() {
    return false;
  }

  public void commit() throws TopLinkException {
  }

  public void release() throws TopLinkException {
  }

  public void revertAndResume() throws TopLinkException {
  }
}
