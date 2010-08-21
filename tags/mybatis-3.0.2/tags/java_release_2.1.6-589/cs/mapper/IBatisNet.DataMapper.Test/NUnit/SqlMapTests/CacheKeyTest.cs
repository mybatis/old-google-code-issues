using IBatisNet.DataMapper;
using IBatisNet.DataMapper.TypeHandlers;
using NUnit.Framework;


namespace IBatisNet.DataMapper.Test.NUnit.SqlMapTests
{
	/// <summary>
	/// Summary description for CacheKeyTest.
	/// </summary>
	[TestFixture]
	public class CacheKeyTest
	{
		[Test]
		public void ShouldNotConsider1LAndNegative9223372034707292159LToBeEqual()
		{
			// old version of ObjectProbe gave TestClass based on these longs the same HashCode
			DoTestClassEquals(1L, -9223372034707292159L);
		}

		[Test]
		public void ShouldNotConsider1LAndNegative9223372036524971138LToBeEqual()
		{
			// current version of ObjectProbe gives TestClass based on these longs the same HashCode
			DoTestClassEquals(1L, -9223372036524971138L);
		}

		private static void DoTestClassEquals(long firstLong, long secondLong)
		{
			TypeHandlerFactory factory = new TypeHandlerFactory();

			// Two cache keys are equal except for the parameter.
			CacheKey key = new CacheKey(factory, "STATEMENT", "SQL", new TestClass(firstLong), new string[] {"AProperty"}, 0, 0, CacheKeyType.Object);
			CacheKey aDifferentKey = new CacheKey(factory, "STATEMENT", "SQL", new TestClass(secondLong), new string[] {"AProperty"}, 0, 0, CacheKeyType.Object);

			Assert.IsFalse(aDifferentKey.Equals(key)); // should not be equal.
		}

		private class TestClass
		{
			private long _property = long.MinValue;

			public TestClass(long aProperty)
			{
				_property = aProperty;
			}

			public long AProperty
			{
				get { return _property; }
				set { _property = value; }
			}
		}

	}
}
