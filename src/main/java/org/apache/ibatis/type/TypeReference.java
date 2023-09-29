/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型处理器可以处理不同Java类型的数据，而这些类型处理器都是TypeHandler接口的子类，因此都可以作为TypeHandler来使用
 *
 * 当Mybatis取到栽一个TypeHandler时, 却不知道它到底是用来处理哪一种Java类型的处理器?
 * TypeReference 能够判断出一个TypeHandler用来处理的目标类型, 而它判断的方法也很简单
 * 取出TypeHandler实现类中的泛型 T 的类型, 这个值的类型也便是该 TypeHandler能处理的目标类型, 该功能由getSuperclassTypeParameter方法实现
 * 该方法能够找出目标类型存入类的 rawType属性
 */

/**
 * References a generic type.
 *
 * @param <T> the referenced type
 * @since 3.1.0
 * @author Simone Tripodi
 */
public abstract class TypeReference<T> {

  private final Type rawType;

  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  /**
   * 解析出当前TypeHandler实现类能够处理的目标类型
   * @param clazz typeHandler实现类
   * @return 该typeHandler实现类能够处理的目标类型
   */
  Type getSuperclassTypeParameter(Class<?> clazz) {
    //获取clazz类的有带有泛型的直接父类
    Type genericSuperclass = clazz.getGenericSuperclass();
    if (genericSuperclass instanceof Class) {
      // try to climb up the hierarchy until meet something useful
      if (TypeReference.class != genericSuperclass) {
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }

      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }

    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    if (rawType instanceof ParameterizedType) {
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
