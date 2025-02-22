/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.defaults;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * DefaultParameterHandlerTest
 *
 * @author Ryan Lamore
 */
class DefaultParameterHandlerTest {

  @Test
  void setParametersThrowsProperException() throws SQLException {
    final MappedStatement mappedStatement = getMappedStatement();
    final Object parameterObject = null;
    final BoundSql boundSql = mock(BoundSql.class);

    TypeHandler<Object> typeHandler = mock(TypeHandler.class);
    doThrow(new SQLException("foo")).when(typeHandler).setParameter(any(PreparedStatement.class), anyInt(), any(),
        any(JdbcType.class));
    ParameterMapping parameterMapping = new ParameterMapping.Builder(mappedStatement.getConfiguration(), "prop",
        typeHandler).build();
    List<ParameterMapping> parameterMappings = Collections.singletonList(parameterMapping);
    when(boundSql.getParameterMappings()).thenReturn(parameterMappings);

    DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject,
        boundSql);

    PreparedStatement ps = mock(PreparedStatement.class);
    try {
      defaultParameterHandler.setParameters(ps);
      Assertions.fail("Should have thrown TypeException");
    } catch (Exception e) {
      Assertions.assertTrue(e instanceof TypeException, "expected TypeException");
      Assertions.assertTrue(e.getMessage().contains("mapping: ParameterMapping"));
    }

  }

  MappedStatement getMappedStatement() {
    final Configuration config = new Configuration();
    final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
    return new MappedStatement.Builder(config, "testSelect", new StaticSqlSource(config, "some select statement"),
        SqlCommandType.SELECT).resultMaps(new ArrayList<ResultMap>() {
          private static final long serialVersionUID = 1L;

          {
            add(new ResultMap.Builder(config, "testMap", HashMap.class, new ArrayList<ResultMapping>() {
              private static final long serialVersionUID = 1L;

              {
                add(new ResultMapping.Builder(config, "cOlUmN1", "CoLuMn1", registry.getTypeHandler(Integer.class))
                    .build());
              }
            }).build());
          }
        }).build();
  }

  @Test
  void testParameterObjectMetaObjectGetValue() {
    Configuration config = new Configuration();
    TypeHandlerRegistry registry = config.getTypeHandlerRegistry();

    MappedStatement mappedStatement = new MappedStatement.Builder(config, "testSelect", new StaticSqlSource(config, "some select statement"), SqlCommandType.SELECT).build();

    Author parameterObject = mock(Author.class);

    BoundSql boundSql = new BoundSql(config, "some select statement", new ArrayList<ParameterMapping>() {
      {
        add(new ParameterMapping.Builder(config, "username", registry.getTypeHandler(String.class)).build());
        add(new ParameterMapping.Builder(config, "password", registry.getTypeHandler(String.class)).build());
        add(new ParameterMapping.Builder(config, "email", registry.getTypeHandler(String.class)).build());
        add(new ParameterMapping.Builder(config, "bio", registry.getTypeHandler(String.class)).jdbcType(JdbcType.VARCHAR).build());
        add(new ParameterMapping.Builder(config, "favouriteSection", registry.getTypeHandler(Section.class)).jdbcType(JdbcType.VARCHAR).build());
      }
    }, parameterObject);

    DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);

    PreparedStatement ps = mock(PreparedStatement.class);

    defaultParameterHandler.setParameters(ps);

    verify(parameterObject, times(1)).getUsername();
    verify(parameterObject, times(1)).getPassword();
    verify(parameterObject, times(1)).getEmail();
    verify(parameterObject, times(1)).getBio();
    verify(parameterObject, times(1)).getFavouriteSection();
  }
}
