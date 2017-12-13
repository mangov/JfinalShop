package com.jfinalshop.util;

/**
 * 工具类 - SQL 单引号等的转义
 * 
 */

public class SQLUtil {

	/**
	 * 
	 * 对content的内容进行转换后，在作为oracle查询的条件字段值。使用/作为oracle的转义字符,比较合适。<br>
	 * 既能达到效果,而且java代码相对容易理解，建议这种使用方式<br>
	 * "%'" + content + "'%  ESCAPE '/' "这种拼接sql看起来也容易理解<br>
	 * 
	 * @param content
	 * @return
	 */
	public static String decodeSpecialCharsWhenLikeUseBackslash(String content) {
		// 单引号是oracle字符串的边界,oralce中用2个单引号代表1个单引号
		String afterDecode = content.replaceAll("'", "''");
		// 由于使用了/作为ESCAPE的转义特殊字符,所以需要对该字符进行转义
		// 这里的作用是将"a/a"转成"a//a"
		afterDecode = afterDecode.replaceAll("/", "//");
		// 使用转义字符 /,对oracle特殊字符% 进行转义,只作为普通查询字符，不是模糊匹配
		afterDecode = afterDecode.replaceAll("%", "/%");
		// 使用转义字符 /,对oracle特殊字符_ 进行转义,只作为普通查询字符，不是模糊匹配
		afterDecode = afterDecode.replaceAll("_", "/_");
		return afterDecode;
	}

	/**
	 * 对content的内容进行转换后，在作为oracle查询的条件字段值。使用\作为oracle的转义字符。<br>
	 * 这种做法也能达到目的，但不是好的做法，比较容易出错，而且代码很那看懂。<br>
	 * "%'" + content + "'%  ESCAPE '\' "这种拼接sql实际上是错误的.<br>
	 * "%'" + content + "'%  ESCAPE '\\' "这种拼接sql才是正确的<br>
	 * 
	 * @param content
	 * @return
	 */
	public static String decodeSpecialCharsWhenLikeUseSlash(String content) {
		// 单引号是oracle字符串的边界,oralce中用2个单引号代表1个单引号
		String afterDecode = content.replaceAll("'", "''");
		// 由于使用了\作为ESCAPE的转义特殊字符,所以需要对该字符进行转义
		// 由于\在java和正则表达式中都是特殊字符,需要进行特殊处理
		// 这里的作用是将"a\a"转成"a\\a"
		afterDecode = afterDecode.replaceAll("\\\\", "\\\\\\\\");
		// 使用转义字符 \,对oracle特殊字符% 进行转义,只作为普通查询字符，不是模糊匹配
		afterDecode = afterDecode.replaceAll("%", "\\\\%");
		// 使用转义字符 \,对oracle特殊字符_ 进行转义,只作为普通查询字符，不是模糊匹配
		afterDecode = afterDecode.replaceAll("_", "\\\\_");
		return afterDecode;
	}
}