package org.sample.mybatis.mappers;

import org.sample.beans.Contact;

import java.util.List;

public interface ContactMapper {

	Integer insert(Contact contact);

	List<Contact> selectAll();

	Contact select(Integer id);

	Integer update(Contact contact);

	Integer delete(Integer id);

}
