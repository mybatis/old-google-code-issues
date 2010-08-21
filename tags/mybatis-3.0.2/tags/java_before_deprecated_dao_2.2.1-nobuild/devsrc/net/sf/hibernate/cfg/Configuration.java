package net.sf.hibernate.cfg;

import net.sf.hibernate.SessionFactory;

import java.util.Properties;

public class Configuration {
    public Configuration addClass(Class aClass) {
      return this;
    }

    public Configuration addResource(java.lang.String string){
      return this;
    }
  
    public Configuration setProperties(Properties props) {
      return this;
    }

    public SessionFactory buildSessionFactory() {
        return null;
    }
}
