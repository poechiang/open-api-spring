package me.jeffrey.open.utils;

import java.util.Optional;
import java.util.regex.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Desensitizer {

  /**
   * [自定义]使用指定正则表达式替换掩码字符串
   * @param origin      原始字符串
   * @param pattern     正则表达式,所有模式字符串中的命名捕获组都将原样输出
   * @param symbol      替换字符串
   * @param keepLength  脱敏前后保持原有长度
   * @return 结果
   *
   * @示例:
   * <pre>{@code
   *    // 以手机号码为例,以下是一个前3后4,带4位掩码长度的demo
   *    int prefixLength = 3;
   *    int suffixLength = 4
   *    String patternFmt = "(?:<prefix>^.{{%1$d}).*(?:<suffix>.{{%2$d}}$)";
   *    String pattern  = patternFmt.formatted(prefixLength,suffixLength);
   *    desensitize(originPhoneNumber,pattern,'*'); // out 123****789
   * }</pre>
   */
  static String desensitize(String origin, String pattern, char symbol, boolean keepLength) {
    String markStr = Optional.of(symbol).orElse('*').toString().repeat(origin.length());
    // 统计正则表达式中所有的命名分组
    Matcher m = Pattern.compile("(?<=\\(\\?<)\\w+(?=>)").matcher(pattern);
    StringBuilder stringBuilder = new StringBuilder(markStr);

    Matcher matcher = Pattern.compile(pattern).matcher(origin);
    if (matcher.matches()) {
      while (m.find()) {
        String groupName = m.group();
        int start = matcher.start(groupName);
        int end = matcher.end(groupName);
        String group = matcher.group(groupName);
        
        stringBuilder.replace(start, end, group);
      }
    }

    // 如果不需要保持长度,则将结果字符串中所有长度大于等于5的替换为4个symbol长度
    if (!keepLength) {
      return Pattern.compile("\\%1$c{5,}".formatted(symbol))
          .matcher(stringBuilder)
          .replaceAll(String.valueOf(symbol).repeat(4));
    }

    return stringBuilder.toString();
  }

  /**
   * [自定义]使用指定正则表达式替换掩码字符串
   * @param origin    原始字符串
   * @param pattern   正则表达式,所有模式字符串中的命名捕获组都将原样输出
   * @param symbol    替换字符串
   * @return 结果
   *
   * @示例:
   * <pre>{@code
   *    // 以手机号码为例,以下是一个前3后4,带4位掩码长度的demo
   *    int prefixLength = 3;
   *    int suffixLength = 4
   *    String patternFmt = "(?:<prefix>^.{{%1$d}).*(?:<suffix>.{{%2$d}}$)";
   *    String pattern  = patternFmt.formatted(prefixLength,suffixLength);
   *    desensitize(originPhoneNumber,pattern,"****"); // out 123****789
   * }</pre>
   */
  static String desensitize(String origin, String pattern, char symbol) {
    return desensitize(origin, pattern, symbol, false);
  }

  /**
   * [自定义] 当原始字符串长度小于前后明文长度之和时,内容输入同等长度的mark字符串
   *
   * @param origin 原始字符串
   * @param pattern 正则表达式,使用prefix和suffix命名捕获组排除原样输出部分
   * @param symbol 替换字符串,如果掩码为一个字符,则默认替换为4个字符,否则按实际提供数量输出
   * @param keepLength 脱敏前后保持原有长度
   * @return 结果
   */
  public static String custom(String origin, String pattern, char symbol, boolean keepLength) {

    return desensitize(origin, pattern, symbol, keepLength);
  }
  /**
   * [自定义] 当原始字符串长度小于前后明文长度之和时,内容输入同等长度的mark字符串
   *
   * @param origin 原始字符串
   * @param pattern 正则表达式,使用prefix和suffix命名捕获组排除原样输出部分
   * @param symbol 替换字符串,如果掩码为一个字符,则默认替换为4个字符,否则按实际提供数量输出
   * @return 结果
   */
  public static String custom(String origin, String pattern, char symbol) {

    return custom(origin, pattern, symbol, false);
  }

  /**
   * [中文姓名] 前0后1
   *
   * @implNote
   *     <pre>{@code "(?<suffix>.{1}$)";}</pre>
   *
   * @param origin 中文姓名
   * @param symbol 替换字符串
   * @return 结果
   */
  public static String cnName(String origin, char symbol) {
    return desensitize(origin, "(?<suffix>.{1}$)", symbol);
  }
  /**
   * [英文姓名] 前1后3
   *
   * @implNote
   *     <pre>{@code "(?<prefix>^[a-zA-Z]).+(?<suffix>.{3}$)";}</pre>
   *
   * @param origin 英文姓名
   * @param symbol 替换字符串
   * @return 结果
   */
  public static String enName(String origin, char symbol) {
    return desensitize(origin, "(?<prefix>^[a-zA-Z]).+(?<suffix>.{3}$)", symbol);
  }

  /**
   * [身份证号码] 前4后2,“*”
   *
   * @implNote
   *     <pre>{@code "(?<prefix>^\\d{4}).*(?:<suffix>\\d{2}$)"}</pre>
   *
   * @param origin 身份证号码
   * @param symbol 替换字符串
   * @return 结果
   */
  public static String idNumber(String origin, char symbol) {
    return desensitize(origin, "(?<prefix>^\\d{4}).*(?:<suffix>\\d{2}$)", symbol);
  }

  /**
   * [手机号码] (国家地区) 手机号码
   *
   * @implNote
   *     <pre>{@code String pattern = "(?<prefix>\(\d{3}\)\d{3}|\d{3})\d{4}(?<suffix>\d{4})"}</pre>
   *
   * @param origin 手机号码
   * @param symbol 替换字符串
   * @return (86)123****789
   */
  public static String mobile(String origin, char symbol) {

    return desensitize(
        origin, "(?<country>(\\(\\+?\\d{2,3}\\))?\\d{3})\\d{4}(?<suffix>\\d{4})", symbol);
  }

  /**
   * [手机号码] (国家地区) 区号 - 固定电话
   *
   * @implNote
   *     <pre>{@code
   * String areaPattern = "(?<prefix>0)\d{2,3}";
   * String telPattern = "(?<prefix>\\d{3}).+(?<suffix>\\d{4})";
   *
   * }</pre>
   *
   * @param origin 固定电话
   * @param symbol 替换字符串
   * @return (86)0***-1****23、(86)0***-1****234、0***-1****23、0***-1****234、1****23、1****234、
   */
  public static String telephone(String origin, char symbol) {

    String pattern =
        "(?<country>\\(\\+?\\d{2,3}\\))?((?<codePrefix>\\d)\\d{2}(?<splitter>\\d?-))?(?<prefix>\\d{2})\\d{2,3}(?<suffix>\\d{3})";

    return desensitize(origin, pattern, symbol);
  }

  /**
   * [邮箱] 前1后?,“*”
   *
   * @implNote
   *     <pre>{@code "(?<prefix>^\w).+(?<suffix>@.+$)}"</pre>
   *
   * @param origin 邮箱地址
   * @param symbol 替换字符串
   * @return 结果
   */
  public static String email(String origin, char symbol) {

    return desensitize(origin, "(?<prefix>^\\w).+(?<suffix>@.+$)", symbol);
  }

  /**
   * [地址] 前10后0,“*”
   *
   * @param origin 地址
   * @param symbol 替换字符串
   * @return 结果
   */
  public static String address(String origin, char symbol) {
    return desensitize(origin, "(?<prefix>.{6}).+(?<suffix>.{3})", symbol);
  }
}
