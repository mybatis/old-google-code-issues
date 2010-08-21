package org.sample.mybatis.mappers;

import org.junit.BeforeClass;
import org.sample.util.DatabaseInitializer;

/**
 *
 * @author nmaves
 */
public class MapperTestBase {

	@BeforeClass
	public static void init() {
		DatabaseInitializer.init();
	}
}
