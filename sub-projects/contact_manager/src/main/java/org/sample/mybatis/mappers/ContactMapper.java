package org.sample.mybatis.mappers;

import org.sample.beans.Contact;

public interface ContactMapper {

	Integer insert(Contact contact);

	Contact selectAll(Integer id);

	Contact select(Integer id);

	Integer update(Contact contact);

	Integer delete(Integer id);

}
