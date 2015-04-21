# Introduction #

Some SQL queries return complex information that cannot be described by simple XML or annotations.
For this special case, MyBatis has a ResultHandler interface that gives you great control over the ResultSet returned by the SQL query.


## ResultHandler example ##

MyBatis can populate beans before calling ResultHandler's only method: handleResult(ResultContext).
First, let's define a complex object that MyBatis will populate.

```
public interface GrandFatherMapper {
	public static class GrandFatherWithGrandChildren {
		public GrandFather grandFather;
		public Child child;
	}
	public void selectComplex(ResultHandler handler);
}
```

Then we will create a resultMap and a select that will use that object.

```
<mapper namespace="ca.qc.ircm.examples.resulthandler.GrandFatherMapper">
    <resultMap id="ComplexMap" type="ca.qc.ircm.examples.resulthandler.GrandFatherMapper$GrandFatherWithGrandChildren">
    	  <association property="grandFather" javaType="GrandFather">
            <id property="id" column="GrandFather_id"/>
            <result property="name" column="GrandFather_name"/>
    	  </association>
    	  <association property="child" javaType="Child">
            <id property="id" column="Child_id"/>
            <result property="name" column="Child_name"/>
    	  </association>
    </resultMap>
    <select id="selectComplex" resultMap="ComplexMap">
    	  SELECT GrandFather.id AS GrandFather_id, GrandFather.name AS GrandFather_name, Child.id AS Child_id, Child.name AS Child_name
    	  FROM GrandFather
    	  JOIN Father ON GrandFather.id = Father.grand_father_id
    	  JOIN Child ON Father.id = Child.father_id
    </select>
</mapper>
```

Finally, let's call that select with a ResultHandler to obtain a Map of grand fathers and their childrens.

```
    public Map<GrandFather, List<Child>> selectGrandFathersWithChildren() {
    	SqlSession sqlSession = sqlSessionFactory.openSession();
    	try {
    		class MyResultHandler implements ResultHandler {
    			Map<GrandFather, List<Child>> grandFatherWithChildren = new HashMap<GrandFather, List<Child>>();
				@Override
				public void handleResult(ResultContext context) {
					final GrandFatherWithGrandChildren complex = (GrandFatherWithGrandChildren)context.getResultObject();
					if (!grandFatherWithChildren.containsKey(complex.grandFather)) {
						grandFatherWithChildren.put(complex.grandFather, new ArrayList<Child>());
					}
					grandFatherWithChildren.get(complex.grandFather).add(complex.child);
				}
    		};
    		MyResultHandler handler = new MyResultHandler();
    		GrandFatherMapper grandFatherMapper = sqlSession.getMapper(GrandFatherMapper.class);
    		grandFatherMapper.selectComplex(handler);
    		
    		return handler.grandFatherWithChildren;
    	} finally {
    		sqlSession.close();
    	}
    }
```