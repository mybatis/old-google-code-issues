package org.apache.ibatis.builder;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Configuration;

import java.io.Reader;

public class XmlConfigBuilderTest {

  @Test
  public void shouldSuccessfullyLoadMinimalXMLConfigFile() throws Exception {
    String resource = "org/apache/ibatis/builder/MinimalMapperConfig.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    XMLConfigBuilder builder = new XMLConfigBuilder(reader);
    Configuration config = builder.parse();
    assertNotNull(config);
  }

}
