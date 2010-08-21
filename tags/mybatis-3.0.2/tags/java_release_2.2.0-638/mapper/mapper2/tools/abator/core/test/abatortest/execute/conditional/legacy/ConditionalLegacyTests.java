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

package abatortest.execute.conditional.legacy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import abatortest.BaseTest;
import abatortest.generated.conditional.legacy.dao.FieldsblobsDAO;
import abatortest.generated.conditional.legacy.dao.FieldsonlyDAO;
import abatortest.generated.conditional.legacy.dao.PkblobsDAO;
import abatortest.generated.conditional.legacy.dao.PkfieldsDAO;
import abatortest.generated.conditional.legacy.dao.PkfieldsblobsDAO;
import abatortest.generated.conditional.legacy.dao.PkonlyDAO;
import abatortest.generated.conditional.legacy.model.Fieldsblobs;
import abatortest.generated.conditional.legacy.model.FieldsblobsExample;
import abatortest.generated.conditional.legacy.model.FieldsblobsWithBLOBs;
import abatortest.generated.conditional.legacy.model.Fieldsonly;
import abatortest.generated.conditional.legacy.model.FieldsonlyExample;
import abatortest.generated.conditional.legacy.model.Pkblobs;
import abatortest.generated.conditional.legacy.model.PkblobsExample;
import abatortest.generated.conditional.legacy.model.Pkfields;
import abatortest.generated.conditional.legacy.model.PkfieldsExample;
import abatortest.generated.conditional.legacy.model.PkfieldsKey;
import abatortest.generated.conditional.legacy.model.Pkfieldsblobs;
import abatortest.generated.conditional.legacy.model.PkfieldsblobsExample;
import abatortest.generated.conditional.legacy.model.PkfieldsblobsKey;
import abatortest.generated.conditional.legacy.model.PkonlyExample;
import abatortest.generated.conditional.legacy.model.PkonlyKey;

/**
 * @author Jeff Butler
 *
 */
public class ConditionalLegacyTests extends BaseTest {

    protected void setUp() throws Exception {
        super.setUp();
        initDaoManager("abatortest/execute/conditional/legacy/dao.xml", null);
    }
    
    public void testFieldsOnlyInsert() {
        FieldsonlyDAO dao = (FieldsonlyDAO) daoManager.getDao(FieldsonlyDAO.class);
        
        Fieldsonly record = new Fieldsonly();
        record.setDoublefield(new Double(11.22));
        record.setFloatfield(new Float(33.44));
        record.setIntegerfield(new Integer(5));
        dao.insert(record);

        FieldsonlyExample example = new FieldsonlyExample();
        example.setIntegerfield(new Integer(5));
        example.setIntegerfield_Indicator(FieldsonlyExample.EXAMPLE_EQUALS);
        
        List answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
        
        Fieldsonly returnedRecord = (Fieldsonly) answer.get(0);
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
        example.setIntegerfield(new Integer(5));
        example.setIntegerfield_Indicator(FieldsonlyExample.EXAMPLE_GREATER_THAN);
        
        List answer = dao.selectByExample(example);
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
        example.setIntegerfield(new Integer(5));
        example.setIntegerfield_Indicator(FieldsonlyExample.EXAMPLE_GREATER_THAN);
        
        int rows = dao.deleteByExample(example);
        assertEquals(2, rows);
        
        example = new FieldsonlyExample();
        List answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKOnlyInsert() {
        PkonlyDAO dao = (PkonlyDAO) daoManager.getDao(PkonlyDAO.class);
        
        PkonlyKey key = new PkonlyKey();
        key.setId(new Integer(1));
        key.setSeqNum(new Integer(3));
        dao.insert(key);
        
        PkonlyExample example = new PkonlyExample();
        List answer = dao.selectByExample(example);
        assertEquals(1, answer.size());
        
        PkonlyKey returnedRecord = (PkonlyKey) answer.get(0);
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
        List answer = dao.selectByExample(example);
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
        example.setId(4);
        example.setId_Indicator(PkonlyExample.EXAMPLE_GREATER_THAN);
        int rows = dao.deleteByExample(example);
        assertEquals(2, rows);

        example = new PkonlyExample();
        List answer = dao.selectByExample(example);
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
        example.setId(4);
        example.setId_Indicator(PkonlyExample.EXAMPLE_GREATER_THAN);
        List answer = dao.selectByExample(example);
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
        List answer = dao.selectByExample(example);
        assertEquals(2, answer.size());
        
        example = new PkfieldsExample();
        example.setLastname("J%");
        example.setLastname_Indicator(PkfieldsExample.EXAMPLE_LIKE);
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
    
    public void testPKFieldsSelectByExample() {
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
        example.setId1(new Integer(3));
        example.setId1_Indicator(PkfieldsExample.EXAMPLE_EQUALS);
        example.setId2(new Integer(4));
        example.setId2_Indicator(PkfieldsExample.EXAMPLE_EQUALS);
        
        List answer = dao.selectByExample(example);
        
        assertEquals(1, answer.size());
        
        Pkfields newRecord = (Pkfields) answer.get(0);
        
        assertNotNull(newRecord);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
    }
    
    public void testPKBlobsInsert() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        Pkblobs returnedRecord = (Pkblobs) answer.get(0);
        assertEquals(record.getId(), returnedRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
    }
    
    public void testPKBlobsUpdateByPrimaryKeyWithBLOBs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        int rows = dao.updateByPrimaryKey(record);
        assertEquals(1, rows);
        
        Pkblobs newRecord = dao.selectByPrimaryKey(new Integer(3));
        
        assertNotNull(newRecord);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKBlobsUpdateByPrimaryKeySelective() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        Pkblobs newRecord = new Pkblobs();
        newRecord.setId(new Integer(3));
        newRecord.setBlob2(generateRandomBlob());
        dao.updateByPrimaryKeySelective(newRecord);
        
        Pkblobs returnedRecord = dao.selectByPrimaryKey(new Integer(3));
        assertNotNull(returnedRecord);
        assertEquals(record.getId(), returnedRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        assertTrue(blobsAreEqual(newRecord.getBlob2(), returnedRecord.getBlob2()));
    }
    
    public void testPKBlobsDeleteByPrimaryKey() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
        
        int rows = dao.deleteByPrimaryKey(new Integer(3));
        assertEquals(1, rows);
        
        example = new PkblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(0, answer.size());
    }
    
    public void testPKBlobsDeleteByExample() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkblobs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        PkblobsExample example = new PkblobsExample();
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        example = new PkblobsExample();
        example.setId(new Integer(4));
        example.setId_Indicator(PkblobsExample.EXAMPLE_LESS_THAN);
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);
        
        example = new PkblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKBlobsSelectByPrimaryKey() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkblobs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        Pkblobs newRecord = dao.selectByPrimaryKey(new Integer(6));
        assertNotNull(newRecord);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKBlobsSelectByExampleWithoutBlobs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkblobs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        PkblobsExample example = new PkblobsExample();
        example.setId(new Integer(4));
        example.setId_Indicator(PkblobsExample.EXAMPLE_GREATER_THAN);
        List answer = dao.selectByExampleWithoutBLOBs(example);
        
        assertEquals(1, answer.size());
        
        record = (Pkblobs) answer.get(0);
        assertEquals(6, record.getId().intValue());
        assertNull(record.getBlob1());
        assertNull(record.getBlob2());
    }
    
    public void testPKBlobsSelectByExampleWithBlobs() {
        PkblobsDAO dao = (PkblobsDAO) daoManager.getDao(PkblobsDAO.class);
        
        Pkblobs record = new Pkblobs();
        record.setId(new Integer(3));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkblobs();
        record.setId(new Integer(6));
        record.setBlob1(generateRandomBlob());
        record.setBlob2(generateRandomBlob());
        dao.insert(record);

        PkblobsExample example = new PkblobsExample();
        example.setId(new Integer(4));
        example.setId_Indicator(PkblobsExample.EXAMPLE_GREATER_THAN);
        List answer = dao.selectByExampleWithBLOBs(example);
        
        assertEquals(1, answer.size());
        
        Pkblobs newRecord = (Pkblobs) answer.get(0);
        assertEquals(record.getId(), newRecord.getId());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
    
    public void testPKFieldsBlobsInsert() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        Pkfieldsblobs returnedRecord = (Pkfieldsblobs) answer.get(0);
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(record.getLastname(), returnedRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithBLOBs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
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
        updateRecord.setBlob1(generateRandomBlob());
        
        int rows = dao.updateByPrimaryKeyWithBLOBs(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        Pkfieldsblobs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertTrue(blobsAreEqual(updateRecord.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithoutBLOBs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
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
        
        int rows = dao.updateByPrimaryKeyWithoutBLOBs(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        Pkfieldsblobs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), newRecord.getLastname());
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsUpdateByPrimaryKeySelective() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        Pkfieldsblobs updateRecord = new Pkfieldsblobs();
        updateRecord.setId1(new Integer(3));
        updateRecord.setId2(new Integer(4));
        updateRecord.setLastname("Jones");
        
        int rows = dao.updateByPrimaryKeySelective(updateRecord);
        assertEquals(1, rows);
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(3));
        key.setId2(new Integer(4));
        Pkfieldsblobs returnedRecord = dao.selectByPrimaryKey(key);
        assertEquals(record.getFirstname(), returnedRecord.getFirstname());
        assertEquals(updateRecord.getLastname(), returnedRecord.getLastname());
        assertEquals(record.getId1(), returnedRecord.getId1());
        assertEquals(record.getId2(), returnedRecord.getId2());
        assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsDeleteByPrimaryKey() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkfieldsblobs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List answer = dao.selectByExampleWithoutBLOBs(example);
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
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkfieldsblobs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());

        example = new PkfieldsblobsExample();
        example.setId1(new Integer(3));
        example.setId1_Indicator(PkfieldsblobsExample.EXAMPLE_NOT_EQUALS);
        int rows = dao.deleteByExample(example);
        assertEquals(1, rows);
        
        example = new PkfieldsblobsExample();
        answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
    }
    
    public void testPKFieldsBlobsSelectByPrimaryKey() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkfieldsblobs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        PkfieldsblobsKey key = new PkfieldsblobsKey();
        key.setId1(new Integer(5));
        key.setId2(new Integer(6));
        Pkfieldsblobs newRecord = dao.selectByPrimaryKey(key);
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
    }
    
    public void testPKFieldsBlobsSelectByExampleWithoutBlobs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkfieldsblobs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        example.setId2(new Integer(6));
        example.setId2_Indicator(PkfieldsblobsExample.EXAMPLE_EQUALS);
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());
        
        Pkfieldsblobs newRecord = (Pkfieldsblobs) answer.get(0);
        assertEquals(record.getId1(), newRecord.getId1());
        assertEquals(record.getId2(), newRecord.getId2());
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertNull(newRecord.getBlob1());
    }
    
    public void testPKFieldsBlobsSelectByExampleWithBlobs() {
        PkfieldsblobsDAO dao = (PkfieldsblobsDAO) daoManager.getDao(PkfieldsblobsDAO.class);
        
        Pkfieldsblobs record = new Pkfieldsblobs();
        record.setId1(new Integer(3));
        record.setId2(new Integer(4));
        record.setFirstname("Jeff");
        record.setLastname("Smith");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        record = new Pkfieldsblobs();
        record.setId1(new Integer(5));
        record.setId2(new Integer(6));
        record.setFirstname("Scott");
        record.setLastname("Jones");
        record.setBlob1(generateRandomBlob());
        dao.insert(record);
        
        PkfieldsblobsExample example = new PkfieldsblobsExample();
        example.setId2(new Integer(6));
        example.setId2_Indicator(PkfieldsblobsExample.EXAMPLE_EQUALS);
        List answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        Pkfieldsblobs newRecord = (Pkfieldsblobs) answer.get(0);
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
        List answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());
        
        FieldsblobsWithBLOBs returnedRecord = (FieldsblobsWithBLOBs) answer.get(0);
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
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(2, answer.size());
        
        example = new FieldsblobsExample();
        example.setFirstname("S%");
        example.setFirstname_Indicator(FieldsblobsExample.EXAMPLE_LIKE);
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
        example.setFirstname("S%");
        example.setFirstname_Indicator(FieldsblobsExample.EXAMPLE_LIKE);
        List answer = dao.selectByExampleWithoutBLOBs(example);
        assertEquals(1, answer.size());

        Fieldsblobs newRecord = (Fieldsblobs) answer.get(0);
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
        example.setFirstname("S%");
        example.setFirstname_Indicator(FieldsblobsExample.EXAMPLE_LIKE);
        List answer = dao.selectByExampleWithBLOBs(example);
        assertEquals(1, answer.size());

        FieldsblobsWithBLOBs newRecord = (FieldsblobsWithBLOBs) answer.get(0);
        assertEquals(record.getFirstname(), newRecord.getFirstname());
        assertEquals(record.getLastname(), newRecord.getLastname());
        assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
    }
}
