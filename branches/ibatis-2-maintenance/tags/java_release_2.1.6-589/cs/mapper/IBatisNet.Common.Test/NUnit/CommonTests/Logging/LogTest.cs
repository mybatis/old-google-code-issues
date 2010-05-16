using System.Reflection;
using IBatisNet.Common.Logging;
using NUnit.Framework;


namespace IBatisNet.Common.Test.NUnit.CommonTests.Logging
{
	/// <summary>
	/// Summary description for LogTest.
	/// </summary>
	[TestFixture]
	public class LogTest
	{
		private ILog _log = null;


		[SetUp]
		public void SetUp()
		{
			_log = LogManager.GetLogger( MethodBase.GetCurrentMethod().DeclaringType );
			_log.Info( "Starting tests..." );
		}

		[TearDown]
		public void TearDown()
		{
			_log.Info( "Ending tests..." );
		}

		[Test]
		public void LogDebug()
		{
			_log.Debug("test LogDebug");
		}

		[Test]
		public void LogInfo()
		{
			_log.Info("test LogInfo");
		}

		[Test]
		public void LogError()
		{
			_log.Error("test LogError");
		}

		[Test]
		public void LogFatal()
		{
			_log.Fatal("test LogFatal");
		}

		[Test]
		public void LogWarn()
		{
			_log.Warn("test LogWarn");
		}
	}
}