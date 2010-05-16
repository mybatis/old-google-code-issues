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

package abatortest.execute.hierarchical.java5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import abatortest.BaseTest;
import abatortest.generated.hierarchical.java5.dao.FieldsblobsDAO;
import abatortest.generated.hierarchical.java5.dao.FieldsonlyDAO;
import abatortest.generated.hierarchical.java5.dao.PkblobsDAO;
import abatortest.generated.hierarchical.java5.dao.PkfieldsDAO;
import abatortest.generated.hierarchical.java5.dao.PkfieldsblobsDAO;
import abatortest.generated.hierarchical.java5.dao.PkonlyDAO;
import abatortest.generated.hierarchical.java5.model.Fieldsblobs;
import abatortest.generated.hierarchical.java5.model.FieldsblobsExample;
import abatortest.generated.hierarchical.java5.model.FieldsblobsWithBLOBs;
import abatortest.generated.hierarchical.java5.model.Fieldsonly;
import abatortest.generated.hierarchical.java5.model.FieldsonlyExample;
import abatortest.generated.hierarchical.java5.model.PkblobsExample;
import abatortest.generated.hierarchical.java5.model.PkblobsKey;
import abatortest.generated.hierarchical.java5.model.PkblobsWithBLOBs;
import abatortest.generated.hierarchical.java5.model.Pkfields;
import abatortest.generated.hierarchical.java5.model.PkfieldsExample;
import abatortest.generated.hierarchical.java5.model.PkfieldsKey;
import abatortest.generated.hierarchical.java5.model.Pkfieldsblobs;
import abatortest.generated.hierarchical.java5.model.PkfieldsblobsExample;
import abatortest.generated.hierarchical.java5.model.PkfieldsblobsKey;
import abatortest.generated.hierarchical.java5.model.PkfieldsblobsWithBLOBs;
import abatortest.generated.hierarchical.java5.model.PkonlyExample;
import abatortest.generated.hierarchical.java5.model.PkonlyKey;

/**
 * @author Jeff Butler
 *
 */
public class HierarchicalJava5Tests extends BaseTest {

    protected void setUp() throws Exception {
        super.setUp();
        initDaoManager("abatortest/execute/hierarchical/java5/dao.xml", null);
    }
    
    public void testFieldsOnlyInsert() {
        FieldsonlyDAO dao = (FieldsonlyDAO) daoManager.getDao(FieldsonlyDAO.class);
        
        Fieldsonly record = new Fieldsonly();
        record.setDoublefield(new Double(11.22));
        record.setFloatfield(new Float(33.44));
        record.setIntegerfield(new Integer(5));
        dao.insert(record);

        FieldsonlyExample example = new FieldsonlyExample();
        example.createCriteria().andIntegerfieldEqualTo(5);
        
        List<Fieldsonly> answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
        
        Fieldsonly returnedRecord = answer.get(0);
        assertEquals(record.getIntegerfield(), returnedRecord.getIntegerfield());
        assertEquals(record.getDoublefield(), returnedRecord.getDoublefield());
        assertEquals(record.getFloatfield(), returnedRecord.getFloatfield());
    }
    
    public void testFieldsOnlySelectByExample() {
        FieldsonlyDAO dao = (FieldsonlyDAO) daoManager.getDao(FieldsonlyDAO.class);
        
        Fieldsonly record = new Fieldsonly();
        record.setDoublefield(new Double(11.22));
        record.setFloatfield(new Float(33.44));
        record.setIntegerfield(new Integer(5));
        dao.insert(record);

        record = new Fieldsonly();
        record.setDoublefield(new Double(44.55));
        record.setFloatfield(new Float(66.77));
        record.setIntegerfield(new Integer(8));
        dao.insert(record);
        
        record = new Fieldsonly();
        record.setDoublefield(new Double(88.99));
        record.setFloatfield(new Float(100.111));
        record.setIntegerfield(new Integer(9));
        dao.insert(record);
        
        FieldsonlyExample example = new FieldsonlyExample();
        example.createCriteria().andIntegerfieldGreaterThan(5);
        
        List<Fieldsonly> answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
        
        example = new FieldsonlyExample();
        answer = dao.selectByExample(example);
        assertEquals(3, answer.size());
    }
    
    public void testFieldsOnlyDeleteByExample() {
        FieldsonlyDAO dao = (FieldsonlyDAO) daoManager.getDao(FieldsonlyDAO.class);
        
        Fieldsonly record = new Fieldsonly();
        record.setDoublefield(new Double(11.22));
        record.setFloatfield(new Float(33.44));
        record.setIntegerfield(new Integer(5));
        dao.insert(record);

        record = new Fieldsonly();
        record.setDoublefield(new Double(44.55));
        record.setFloatfield(new Float(66.77));
        record.setIntegerfield(new Integer(8));
        dao.insert(record);
        
        record = new Fieldsonly();
        record.setDoublefield(new Double(88.99));
        record.setFloatfield(new Float(100.111));
        record.setIntegerfield(new Integer(9));
        dao.insert(record);
        
        FieldsonlyExample example = new FieldsonlyExample();
        example.createCriteria().andIntegerfieldGreaterThan(5);
        
        int rows = dao.deleteByExample(example);
        assertEquals(2, rows);
        
        example = new FieldsonlyExample();
        List<Fieldsonly> answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKOnlyInsert() {
        PkonlyDAO dao = (PkonlyDAO) daoManager.getDao(PkonlyDAO.class);
        
        PkonlyKey key = new PkonlyKey();
        key.setId(new Integer(1));
        key.setSeqNum(new Integer(3));
        dao.insert(key);
        
        PkonlyExample example = new PkonlyExample();
        List<PkonlyKey> answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
        
        PkonlyKey returnedRecord = answer.get(0);
        assertEquals(key.getId(), returnedRecord.getId());
        assertEquals(key.getSeqNum(), returnedRecord.getSeqNum());
    }
    
    public void testPKOnlyDeleteByPrimaryKey() {
        PkonlyDAO dao = (PkonlyDAO) daoManager.getDao(PkonlyDAO.class);
        
        PkonlyKey key = new PkonlyKey();
        key.setId(new Integer(1));
        key.setSeqNum(new Integer(3));
        dao.insert(key);
        
        key = new PkonlyKey();
        key.setId(new Integer(5));
        key.setSeqNum(new Integer(6));
        dao.insert(key);
        
        PkonlyExample example = new PkonlyExample();
        List<PkonlyKey> answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
        
        key = new PkonlyKey();
        key.setId(new Integer(5));
        key.setSeqNum(new Integer(6));
        int rows = dao.deleteByPrimaryKey(key);
        assertEquals(1, rows);
        
        answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKOnlyDeleteByExample() {
        PkonlyDAO dao = (PkonlyDAO) daoManager.getDao(PkonlyDAO.class);
        
        PkonlyKey key = new PkonlyKey();
        key.setId(new Integer(1));
        key.setSeqNum(new Integer(3));
        dao.insert(key);
        
        key = new PkonlyKey();
        key.setId(new Integer(5));
        key.setSeqNum(new Integer(6));
        dao.insert(key);
        
        key = new PkonlyKey();
        key.setId(new Integer(7));
        key.setSeqNum(new Integer(8));
        dao.insert(key);
        
        PkonlyExample example = new PkonlyExample();
        example.createCriteria().andIdGreaterThan(4);
        int rows = dao.deleteByExample(example);
        assertEquals(2, rows);

        example = new PkonlyExample();
        List<PkonlyKey> answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKOnlySelectByExample() {
        PkonlyDAO dao = (PkonlyDAO) daoManager.getDao(PkonlyDAO.class);
        
        PkonlyKey key = new PkonlyKey();
        key.setId(new Integer(1));
        key.setSeqNum(new Integer(3));
        dao.insert(key);
        
        key = new PkonlyKey();
        key.setId(new Integer(5));
        key.setSeqNum(new Integer(6));
        dao.insert(key);
        
        key = new PkonlyKey();
        key.setId(new Integer(7));
        key.setSeqNum(new Integer(8));
        dao.insert(key);
        
        PkonlyExample example = new PkonlyExample();
        example.createCriteria().andIdGreaterThan(4);
        List<PkonlyKey> answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
    }
    
    public void testPKFieldsInsert() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setDatefield(new Date());
        record.setDecimal100field(new Long(10L));
        record.setDecimal155field(new BigDecimal("15.12345"));
        record.setDecimal30field(new Short((short) 3));
        record.setDecimal60field(new Integer(6));
        record.setFirstname("Jeff");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        record.setLastname("Butler");
        record.setTimefield(new Date());
        record.setTimestampfield(new Date());
        
        dao.insert(record);
        
        PkfieldsKey key = new PkfieldsKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));
        
        Pkfields returnedRecord = dao.selectByPrimaryKey(key);
        assertNotNull(returnedRecord);
        
        assertTrue(datesAreEqual(record.getDatefield(), returnedRecord.getDatefield()));
        assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
        assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
        assertEquals(record.getDecimal30field(), returnedRecord.getDecimal30field());
        assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertTrue(timesAreEqual(record.getTimefield(), returnedRecord.getTimefield()));
        assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
    }

    public void testPKFieldsUpdateByPrimaryKey() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        
        dao.insert(record);
        
        record.setFirstname("Scott");
        record.setLastname("Jones");
        
        int rows = dao.updateByPrimaryKey(record);
        assertEquals(1, rows);
        
        PkfieldsKey key = new PkfieldsKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));
        
        Pkfields record2 = dao.selectByPrimaryKey(key);
        
        assertEquals(record.getFirstname(), record2.getFirstname());
        assertEquals(record.getLastname(), record2.getLastname());
        assertEquals(record.getId1(), record2.getId1());
        assertEquals(record.getId2(), record2.getId2());
    }

    public void testPKFieldsUpdateByPrimaryKeySelective() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setDecimal60field(new Integer(5));
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        
        dao.insert(record);
        
        Pkfields newRecord = new Pkfields();
        newRecord.setId1(new Integer(1));
        newRecord.setId2(new Integer(2));
        newRecord.setFirstname("Scott");
        newRecord.setDecimal60field(new Integer(4));
        
        int rows = dao.updateByPrimaryKeySelective(newRecord);
        assertEquals(1, rows);
        
        PkfieldsKey key = new PkfieldsKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));
        
        Pkfields returnedRecord = dao.selectByPrimaryKey(key);
        
        assertTrue(datesAreEqual(record.getDatefield(), returnedRecord.getDatefield()));
        assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
        assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
        assertEquals(record.getDecimal30field(), returnedRecord.getDecimal30field());
        assertEquals(newRecord.getDecimal60field(), returnedRecord.getDecimal60field());
        assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertTrue(timesAreEqual(record.getTimefield(), returnedRecord.getTimefield()));
        assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
    }

    public void testPKfieldsDeleteByPrimaryKey() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        
        dao.insert(record);
        
        PkfieldsKey key = new PkfieldsKey();
        key.setId1(new Integer(1));
        key.setId2(new Integer(2));
        
        int rows = dao.deleteByPrimaryKey(key);
        assertEquals(1, rows);
        
        PkfieldsExample example = new PkfieldsExample();
        List answer = dao.selectByExample(example);
        assertEquals(0, answer.size());
    }

    public void testPKFieldsDeleteByExample() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bob");
        record.setLastname("Jones");
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        
        dao.insert(record);
        
        PkfieldsExample example = new PkfieldsExample();
        List<Pkfields> answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
        
        example = new PkfieldsExample();
        example.createCriteria().andLastnameLike("J%");
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);
        
        example = new PkfieldsExample();
        answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKFieldsSelectByPrimaryKey() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bob");
        record.setLastname("Jones");
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        dao.insert(record);
        
        PkfieldsKey key = new PkfieldsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        Pkfields newRecord = dao.selectByPrimaryKey(key);
        
        assertNotNull(newRecord);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
    }
    
    public void testPKFieldsSelectByExampleLike() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Fred");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Wilma");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Pebbles");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Barney");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Betty");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bamm Bamm");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        PkfieldsExample example = new PkfieldsExample();
        example.createCriteria().andFirstnameLike("B%");
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectByExample(example);
        assertEquals(3, answer.size());
        Pkfields returnedRecord = (Pkfields) answer.get(0);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(1);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(2);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }
    
    public void testPKFieldsSelectByExampleNotLike() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Fred");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Wilma");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Pebbles");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Barney");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Betty");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bamm Bamm");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        PkfieldsExample example = new PkfieldsExample();
        example.createCriteria().andFirstnameNotLike("B%");
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectByExample(example);
        assertEquals(3, answer.size());
        Pkfields returnedRecord = (Pkfields) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(1);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(2);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }
    
    public void testPKFieldsSelectByExampleComplexLike() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Fred");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Wilma");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Pebbles");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Barney");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Betty");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bamm Bamm");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        PkfieldsExample example = new PkfieldsExample();
        example.createCriteria().andFirstnameLike("B%")
            .andId2EqualTo(new Integer(3));
        example.or(example.createCriteria().andFirstnameLike("Wi%"));
        
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
        Pkfields returnedRecord = (Pkfields) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(2, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(1);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }
    
    @SuppressWarnings("unchecked")
    public void testPKFieldsSelectByExampleIn() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Fred");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Wilma");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Pebbles");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Barney");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Betty");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bamm Bamm");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        List ids = new ArrayList();
        ids.add(new Integer(1));
        ids.add(new Integer(3));
        
        PkfieldsExample example = new PkfieldsExample();
        example.createCriteria().andId2In(ids);
        
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectByExample(example);
        assertEquals(4, answer.size());
        Pkfields returnedRecord = (Pkfields) answer.get(0);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(1);
        assertEquals(1, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(2);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(1, returnedRecord.getId2().intValue());
        returnedRecord = (Pkfields) answer.get(3);
        assertEquals(2, returnedRecord.getId1().intValue());
        assertEquals(3, returnedRecord.getId2().intValue());
    }
    
    public void testPKFieldsSelectByExampleBetween() {
        PkfieldsDAO dao = (PkfieldsDAO) daoManager.getDao(PkfieldsDAO.class);
        
        Pkfields record = new Pkfields();
        record.setFirstname("Fred");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Wilma");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Pebbles");
        record.setLastname("Flintstone");
        record.setId1(new Integer(1));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Barney");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(1));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Betty");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(2));
        dao.insert(record);
        
        record = new Pkfields();
        record.setFirstname("Bamm Bamm");
        record.setLastname("Rubble");
        record.setId1(new Integer(2));
        record.setId2(new Integer(3));
        dao.insert(record);
        
        PkfieldsExample example = new PkfieldsExample();
        example.createCriteria().andId2Between(new Integer(1), new Integer(3));
        
        example.setOrderByClause("ID1, ID2");
        List answer = dao.selectByExample(example);
        assertEquals(6, answer.size());
    }
    
    public void testPKBlobsInsert() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List<PkblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        PkblobsWithBLOBs returnedRecord = answer.get(0);
        assertEquals(record.getId(), returnedRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
    }
    
    public void testPKBlobsUpdateByPrimaryKeyWithBLOBs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        int rows = dao.updateByPrimaryKey(record);
        assertEquals(1, rows);
        
        PkblobsKey key = new PkblobsKey();
        key.setId(new Integer(3));
        
        PkblobsWithBLOBs newRecord = dao.selectByPrimaryKey(key);
        
        assertNotNull(newRecord);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKBlobsUpdateByPrimaryKeySelective() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsWithBLOBs newRecord = new PkblobsWithBLOBs();
        newRecord.setId(new Integer(3));
        newRecord.setBlob2(generateRandomBlob());
        dao.updateByPrimaryKeySelective(newRecord);
        
        PkblobsKey key = new PkblobsKey();
        key.setId(new Integer(3));
        
        PkblobsWithBLOBs returnedRecord = dao.selectByPrimaryKey(key);
        assertNotNull(returnedRecord);
        assertEquals(record.getId(), returnedRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        assertTrue(blobsAreEqual(newRecord.getBlob2(), returnedRecord.getBlob2()));
    }
    
    public void testPKBlobsDeleteByPrimaryKey() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List<PkblobsKey> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
        
        PkblobsKey key = new PkblobsKey();
        key.setId(new Integer(3));
        int rows = dao.deleteByPrimaryKey(key);
        assertEquals(1, rows);
        
        example = new PkblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(0, answer.size());
    }
    
    public void testPKBlobsDeleteByExample() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new PkblobsWithBLOBs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List<PkblobsKey> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        example = new PkblobsExample();
        example.createCriteria().andIdLessThan(4);
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);
        
        example = new PkblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKBlobsSelectByPrimaryKey() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new PkblobsWithBLOBs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        PkblobsKey key = new PkblobsKey();
        key.setId(new Integer(6));
        PkblobsWithBLOBs newRecord = dao.selectByPrimaryKey(key);
        assertNotNull(newRecord);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKBlobsSelectByExampleWithoutBlobs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new PkblobsWithBLOBs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        PkblobsExample example = new PkblobsExample();
        example.createCriteria().andIdGreaterThan(4);
        List<PkblobsKey> answer = dao.selectByExampleWithoutBLOBs(example);
        
        assertEquals(1, answer.size());
        
        PkblobsKey key = answer.get(0);
        assertFalse(key instanceof PkblobsWithBLOBs);
        assertEquals(6, key.getId().intValue());
    }
    
    public void testPKBlobsSelectByExampleWithBlobs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        PkblobsWithBLOBs record = new PkblobsWithBLOBs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new PkblobsWithBLOBs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        PkblobsExample example = new PkblobsExample();
        example.createCriteria().andIdGreaterThan(4);
        List<PkblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        
        assertEquals(1, answer.size());
        
        PkblobsWithBLOBs newRecord = answer.get(0);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKFieldsBlobsInsert() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List<PkfieldsblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        PkfieldsblobsWithBLOBs returnedRecord = answer.get(0);
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithBLOBs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsWithBLOBs updateRecord = new PkfieldsblobsWithBLOBs();
        updateRecord.setId1(new Integer(3));
        updateRecord.setId2(new Integer(4));
        updateRecord.setFirstname("Scott");
        updateRecord.setLastname("Jones");
        updateRecord.setBlob1(generateRandomBlob());
        
        int rows = dao.updateByPrimaryKey(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        PkfieldsblobsWithBLOBs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertTrue(blobsAreEqual(updateRecord.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithoutBLOBs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        Pkfieldsblobs updateRecord = new Pkfieldsblobs();
        updateRecord.setId1(new Integer(3));
        updateRecord.setId2(new Integer(4));
        updateRecord.setFirstname("Scott");
        updateRecord.setLastname("Jones");
        
        int rows = dao.updateByPrimaryKey(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        PkfieldsblobsWithBLOBs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeySelective() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsWithBLOBs updateRecord = new PkfieldsblobsWithBLOBs();
        updateRecord.setId1(new Integer(3));
        updateRecord.setId2(new Integer(4));
        updateRecord.setLastname("Jones");
        
        int rows = dao.updateByPrimaryKeySelective(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        PkfieldsblobsWithBLOBs returnedRecord = dao.selectByPrimaryKey(key);
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), returnedRecord.getLastname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsDeleteByPrimaryKey() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List<Pkfieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(5));
        key.setId2(new Integer(6));
        int rows = dao.deleteByPrimaryKey(key);
        assertEquals(1, rows);
        
        example = new PkfieldsblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKFieldsBlobsDeleteByExample() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List<Pkfieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());

        example = new PkfieldsblobsExample();
        example.createCriteria().andId1NotEqualTo(3);
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);
        
        example = new PkfieldsblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKFieldsBlobsSelectByPrimaryKey() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List<Pkfieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(5));
        key.setId2(new Integer(6));
        PkfieldsblobsWithBLOBs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsSelectByExampleWithoutBlobs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        example.createCriteria().andId2EqualTo(6);
        List<Pkfieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
        
        Pkfieldsblobs newRecord = answer.get(0);
        assertFalse(newRecord instanceof PkfieldsblobsWithBLOBs);
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
    }
    
    public void testPKFieldsBlobsSelectByExampleWithBlobs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new PkfieldsblobsWithBLOBs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        example.createCriteria().andId2EqualTo(6);
        List<PkfieldsblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        PkfieldsblobsWithBLOBs newRecord = answer.get(0);
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
    }

    public void testFieldsBlobsInsert() {
        FieldsblobsDAO dao = (FieldsblobsDAO) daoManager.getDao(FieldsblobsDAO.class);
        
        FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        FieldsblobsExample example = new FieldsblobsExample();
        List<FieldsblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        FieldsblobsWithBLOBs returnedRecord = answer.get(0);
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
    }
    
    public void testFieldsBlobsDeleteByExample() {
        FieldsblobsDAO dao = (FieldsblobsDAO) daoManager.getDao(FieldsblobsDAO.class);
        
        FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new FieldsblobsWithBLOBs();
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        FieldsblobsExample example = new FieldsblobsExample();
        List<Fieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        example = new FieldsblobsExample();
        example.createCriteria().andFirstnameLike("S%");
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);

        example = new FieldsblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testFieldsBlobsSelectByExampleWithoutBlobs() {
        FieldsblobsDAO dao = (FieldsblobsDAO) daoManager.getDao(FieldsblobsDAO.class);
        
        FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new FieldsblobsWithBLOBs();
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        FieldsblobsExample example = new FieldsblobsExample();
        example.createCriteria().andFirstnameLike("S%");
        List<Fieldsblobs> answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());

        Fieldsblobs newRecord = answer.get(0);
        assertFalse(newRecord instanceof FieldsblobsWithBLOBs);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
    }
    
    public void testFieldsBlobsSelectByExampleWithBlobs() {
        FieldsblobsDAO dao = (FieldsblobsDAO) daoManager.getDao(FieldsblobsDAO.class);
        
        FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new FieldsblobsWithBLOBs();
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        FieldsblobsExample example = new FieldsblobsExample();
        example.createCriteria().andFirstnameLike("S%");
        List<FieldsblobsWithBLOBs> answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());

        FieldsblobsWithBLOBs newRecord = answer.get(0);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
}
