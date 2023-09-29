/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.property;

import java.util.Locale;

import org.apache.ibatis.reflection.ReflectionException;

/**
 * 属性名称处理器 PropertyNamer , 对外提供static方法
 * 类PropertyNamer用final修饰, 构造器用private提供
 *
 * @author Clinton Begin
 */
public final class PropertyNamer {

  private PropertyNamer() {
    // Prevent Instantiation of Static Class
  }

  /**
   * 通过is/get/set方法找出对应的属性
   * @param name 方法名
   * @return 属性名
   */
  public static String methodToProperty(String name) {
    // 如果是is开头的, 截取前两个字符后面的
    if (name.startsWith("is")) {
      name = name.substring(2);
    //  如果是get/set开头的, 截取前三个字符后面的
    } else if (name.startsWith("get") || name.startsWith("set")) {
      name = name.substring(3);
    } else {
      // 如果没找到, 直接抛出异常
      throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
    }

    // 处理属性名, 将首字符转换成小写的
    if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
      name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
    }

    return name;
  }

  /**
   *
   * @param name 方法名
   * @return 是否为属性
   */
  public static boolean isProperty(String name) {
    return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
  }

  /**
   *
   * @param name 方法名
   * @return 是否为get方法
   */
  public static boolean isGetter(String name) {
    return name.startsWith("get") || name.startsWith("is");
  }

  /**
   *
   * @param name 方法名
   * @return 是否为set方法
   */
  public static boolean isSetter(String name) {
    return name.startsWith("set");
  }

}
