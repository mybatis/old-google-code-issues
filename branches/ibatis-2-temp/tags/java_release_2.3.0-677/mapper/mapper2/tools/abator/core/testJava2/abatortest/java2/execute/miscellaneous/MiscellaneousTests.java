/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package abatortest.java2.execute.miscellaneous;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import abatortest.java2.BaseTest;
import abatortest.java2.generated.miscellaneous.dao.MyObjectDAO;
import abatortest.java2.generated.miscellaneous.model.MyObject;
import abatortest.java2.generated.miscellaneous.model.MyObjectExample;
import abatortest.java2.generated.miscellaneous.model.MyObjectKey;

/**
 * @author Jeff Butler
 *
 */
public class MiscellaneousTests extends BaseTest {

    protected void setUp() throws Exception {
        super.setUp();
        initDaoManager("abatortest/java2/execute/miscellaneous/dao.xml", null);
    }

    public void testMyObjectinsertMyObject() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        record.setStartDate(new Date());
        record.setDecimal100field(new Long(10L));
        record.setDecimal155field(new Double(15.12345));
        record.setDecimal60field(6);
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        record.setLastname("Butler");

        MyTime myTime = new MyTime();
        myTime.setHours(12);
        myTime.setMinutes(34);
        myTime.setSeconds(05);
        record.setTimefield(myTime);
        record.setTimestampfield(new Date());

        dao.insertMyObject(record);

        MyObjectKey key = new MyObjectKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));

        MyObject returnedRecord = dao.selectMyObjectByPrimaryKey(key);
        assertNotNull(returnedRecord);

        assertTrue(datesAreEqual(record.getStartDate(), returnedRecord.getStartDate()));
        assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
        assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
        assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertEquals(record.getTimefield(), returnedRecord.getTimefield());
        assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
    }

    public void testMyObjectUpdateByPrimaryKey() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));

        dao.insertMyObject(record);

        fn = new FirstName();
        fn.setValue("Scott");
        record.setFirstname(fn);
        record.setLastname("Jones");

        int rows = dao.updateMyObjectByPrimaryKey(record);
        assertEquals(1, rows);

        MyObjectKey key = new MyObjectKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));

        MyObject record2 = dao.selectMyObjectByPrimaryKey(key);

        assertEquals(record.getFirstname(), record2.getFirstname());
        assertEquals(record.getLastname(), record2.getLastname());
        assertEquals(record.getId1(), record2.getId1());
        assertEquals(record.getId2(), record2.getId2());
    }

    public void testMyObjectUpdateByPrimaryKeySelective() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setLastname("Smith");
        record.setDecimal60field(5);
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));

        dao.insertMyObject(record);

        MyObject newRecord = new MyObject();
        newRecord.setId1(new Integer(1));
        newRecord.setId2(new Integer(2));
        fn = new FirstName();
        fn.setValue("Scott");
        newRecord.setFirstname(fn);
        record.setStartDate(new Date());

        int rows = dao.updateMyObjectByPrimaryKeySelective(newRecord);
        assertEquals(1, rows);

        MyObjectKey key = new MyObjectKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));

        MyObject returnedRecord = dao.selectMyObjectByPrimaryKey(key);

        assertTrue(datesAreEqual(newRecord.getStartDate(), returnedRecord.getStartDate()));
        assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
        assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());

        // with columns mapped to primitive types, the column is always updated
        assertEquals(newRecord.getDecimal60field(), returnedRecord.getDecimal60field());

        assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertEquals(record.getTimefield(), returnedRecord.getTimefield());
        assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
    }

    public void testMyObjectDeleteByPrimaryKey() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));

        dao.insertMyObject(record);

        MyObjectKey key = new MyObjectKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));

        int rows = dao.deleteMyObjectByPrimaryKey(key);
        assertEquals(1, rows);

        MyObjectExample example = new MyObjectExample();
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(0, answer.size());
    }

    public void testMyObjectDeleteByExample() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bob");
        record.setFirstname(fn);
        record.setLastname("Jones");
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));

        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(2, answer.size());

        example = new MyObjectExample();
        example.createCriteria().andLastnameLike("J%");
        int rows = dao.deleteMyObjectByExample(example);
        assertEquals(1, rows);

        example = new MyObjectExample();
        answer = dao.selectMyObjectByExample(example);
        assertEquals(1, answer.size());
    }

    public void testMyObjectSelectByPrimaryKey() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bob");
        record.setFirstname(fn);
        record.setLastname("Jones");
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        dao.insertMyObject(record);

        MyObjectKey key = new MyObjectKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        MyObject newRecord = dao.selectMyObjectByPrimaryKey(key);

        assertNotNull(newRecord);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
    }

    public void testMyObjectSelectByExampleLike() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Fred");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Wilma");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Pebbles");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Barney");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Betty");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bamm Bamm");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        fn = new FirstName();
        fn.setValue("B%");
        example.createCriteria().andFirstnameLike(fn);
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(3, answer.size());
        MyObject returnedRecord = (MyObject) answer.get(0);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(1);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(2);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }

    public void testMyObjectSelectByExampleNotLike() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Fred");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Wilma");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Pebbles");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Barney");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Betty");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bamm Bamm");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        fn = new FirstName();
        fn.setValue("B%");
        example.createCriteria().andFirstnameNotLike(fn);
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(3, answer.size());
        MyObject returnedRecord = (MyObject) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(1);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(2);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }

    public void testMyObjectSelectByExampleComplexLike() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Fred");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Wilma");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Pebbles");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Barney");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Betty");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bamm Bamm");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        fn = new FirstName();
        fn.setValue("B%");
        example.createCriteria().andFirstnameLike(fn)
            .andId2EqualTo(new Integer(3));
        fn = new FirstName();
        fn.setValue("W%");
        example.or(example.createCriteria().andFirstnameLike(fn));

        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(2, answer.size());
        MyObject returnedRecord = (MyObject) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(1);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }

    public void testMyObjectSelectByExampleIn() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Fred");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Wilma");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Pebbles");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Barney");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Betty");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bamm Bamm");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        List ids = new ArrayList();
        ids.add(new Integer(1));
        ids.add(new Integer(3));

        MyObjectExample example = new MyObjectExample();
        example.createCriteria().andId2In(ids);

        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(4, answer.size());
        MyObject returnedRecord = (MyObject) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(1);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(2);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (MyObject) answer.get(3);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }

    public void testMyObjectSelectByExampleBetween() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        FirstName fn = new FirstName();
        fn.setValue("Fred");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Wilma");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Pebbles");
        record.setFirstname(fn);
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Barney");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Betty");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insertMyObject(record);

        record = new MyObject();
        fn = new FirstName();
        fn.setValue("Bamm Bamm");
        record.setFirstname(fn);
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        example.createCriteria().andId2Between(new Integer(1), new Integer(3));

        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectMyObjectByExample(example);
        assertEquals(6, answer.size());
    }

    public void testMyObjectSelectByExampleTimeEquals() {
        MyObjectDAO dao = (MyObjectDAO) daoManager.getDao(MyObjectDAO.class);

        MyObject record = new MyObject();
        record.setStartDate(new Date());
        record.setDecimal100field(new Long(10L));
        record.setDecimal155field(new Double(15.12345));
        record.setDecimal60field(6);
        FirstName fn = new FirstName();
        fn.setValue("Jeff");
        record.setFirstname(fn);
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        record.setLastname("Butler");

        MyTime myTime = new MyTime();
        myTime.setHours(12);
        myTime.setMinutes(34);
        myTime.setSeconds(05);
        record.setTimefield(myTime);
        record.setTimestampfield(new Date());

        dao.insertMyObject(record);

        MyObjectExample example = new MyObjectExample();
        example.createCriteria().andTimefieldEqualTo(myTime);
        List results = dao.selectMyObjectByExample(example);
        assertEquals(1, results.size());
        MyObject returnedRecord = (MyObject) results.get(0);

        assertTrue(datesAreEqual(record.getStartDate(), returnedRecord.getStartDate()));
        assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
        assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
        assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertEquals(record.getTimefield(), returnedRecord.getTimefield());
        assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
    }

    public void testFieldIgnored() {
        try {
            MyObject.class.getDeclaredField("decimal30field");
            fail("decimal30field should be ignored");
        } catch (NoSuchFieldException e) {
            // ignore (normal case)
        }
    }
}
