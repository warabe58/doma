/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * DB固有の型用の {@link JdbcType} の実装です。
 * {@link PreparedStatement#setObject(int, Object, int)} を使って値を設定します。
 * 
 * @author nakamura-to
 * @since 2.4.0
 */
public class PortableObjectType<T> extends AbstractJdbcType<T> {

    private final JdbcType<T> baseType;

    public PortableObjectType(JdbcType<T> baseType) {
        super(Types.OTHER);
        this.baseType = baseType;
    }

    @Override
    public T doGetValue(ResultSet resultSet, int index)
            throws SQLException {
        return baseType.getValue(resultSet, index);
    }

    @Override
    protected void doSetValue(PreparedStatement preparedStatement, int index,
            T value) throws SQLException {
        preparedStatement.setObject(index, value, this.type);
    }

    @Override
    protected T doGetValue(CallableStatement callableStatement, int index)
            throws SQLException {
        return baseType.getValue(callableStatement, index);
    }

    @Override
    protected String doConvertToLogFormat(T value) {
        return baseType.convertToLogFormat(value);
    }
}
