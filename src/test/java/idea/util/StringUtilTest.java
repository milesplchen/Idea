/**
 * 
 */
package idea.util;

import static org.junit.Assert.*;
import org.junit.*;

import java.security.NoSuchAlgorithmException;

/**
 * @author Miles Chen
 *
 */
public class StringUtilTest {
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRemoveUrl() {
		String expected = "";
		String result = StringUtil.removeUrl("http://xxx.xxx");
		assertEquals(expected, result);
	}

	@Test
	public void testLongestCommonSubsequence() {
		String expected = "ace";
		String result = StringUtil.longestCommonSubsequence("xaxbxcxdxex", "oaocoeo");
		assertEquals(expected, result);
	}

	@Test
	public void testLongestContiguousCommonSubsequence() {
		String expected = "bc";
		String result = StringUtil.longestContiguousCommonSubsequence("abcdefgh", "obceo");
		assertEquals(expected, result);

		expected = "fghi";
		result = StringUtil.longestContiguousCommonSubsequence("oobczoeofghio", "axbczdxefghijk");
		assertEquals(expected, result);
	}

	@Test
	public void testSimilarity() {
		double expected = 0.6;
		double result = StringUtil.similarity("abcde", "ace");
		assertEquals(expected, result, 0.001);
	}

	@Test
	public void testRandomString() {
		int expected = 9;
		String result = StringUtil.randomString(9);
		assertEquals(expected, result.length());
	}

	@Test
	public void testEncrypt() throws NoSuchAlgorithmException {
		StringUtil.encrypt("abc", StringUtil.MD5);
//		fail("Not yet implemented");
	}

	@Test
	public void testPatternMatch() {
		StringUtil su = new StringUtil();
		su.compilePattern("ttt");
		int expected = 1;
		int result = su.countMatch("ttt");
		assertEquals(expected, result);

		result = su.countMatch(null);
		assertEquals(0, result);
	}
}
