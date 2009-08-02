/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.doma.jdbc.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.doma.domain.Domain;
import org.seasar.doma.jdbc.JdbcMappingVisitor;
import org.seasar.doma.jdbc.SelectForUpdateType;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.Sql;
import org.seasar.doma.jdbc.SqlLogFormattingVisitor;
import org.seasar.doma.jdbc.SqlNode;
import org.seasar.doma.jdbc.type.JdbcType;

/**
 * RDBMSの方言です。RDBMSの差異を吸収します。
 * <p>
 * このインタフェースの実装はスレッドセーフでなければいけません。
 * <p>
 * 
 * @author taedium
 * 
 */
public interface Dialect {

    /**
     * 名前を返します。
     * 
     * @return 名前
     */
    String getName();

    /**
     * SQLノードを変換します。
     * 
     * @param sqlNode
     *            SQLノード
     * @param options
     *            オプション
     * @return 変換されたSQLノード
     */
    SqlNode transformSelectSqlNode(SqlNode sqlNode, SelectOptions options);

    /**
     * 一意制約違反かどうかを返します。
     * 
     * @param sqlException
     *            SQL例外
     * @return 一意制約違反ならば {@code true}
     */
    boolean isUniqueConstraintViolated(SQLException sqlException);

    /**
     * INSERT文にIDENTITYカラムを含むかどうかを返します。
     * 
     * @return 含む場合 {@code true}
     */
    boolean includesIdentityColumn();

    /**
     * IDENTITYをサポートしているかどうかを返します。
     * 
     * @return サポートしている場合 {@code true}
     */
    boolean supportsIdentity();

    /**
     * シーケンスをサポートするかどうかを返します。
     * 
     * @return サポートしている場合 {@code true}
     */
    boolean supportsSequence();

    /**
     * {@link Statement#getGeneratedKeys()} をサポートしているかどうかを返します。
     * 
     * @return サポートしている場合 {@code true}
     */
    boolean supportsAutoGeneratedKeys();

    /**
     * {@link Statement#executeBatch()} が更新件数を返すことをサポートしているかどうかを返します。
     * 
     * @return サポートしている場合 {@code true}
     */
    boolean supportsBatchUpdateResults();

    /**
     * 悲観的排他制御をサポートしているかどうかを返します。
     * 
     * @param type
     *            悲観的排他制御の種別
     * @param withTargets
     *            ロックの対象が指定されている場合 {@code true}
     * @return サポートしている場合 {@code true}
     */
    boolean supportsSelectForUpdate(SelectForUpdateType type,
            boolean withTargets);

    /**
     * ストアドプロシージャ-やストアドファンクションで {@link ResultSet}
     * をOUTパラメータとして戻すことをサポートしてるかどうかを返します。
     * 
     * @return サポートしている場合 {@code true}
     */
    boolean supportsResultSetReturningAsOutParameter();

    /**
     * データベースで生成されたIDENTITYを取得するためのSQLを返します。
     * <p>
     * {@link #supportsIdentity()} が {@code true} を返す場合にのみ呼び出し可能です。
     * 
     * @param qualifiedTableName
     *            テーブルの完全修飾名
     * @param columnName
     *            IDENTITYカラムの名前
     * @return IDENTITYを取得するためのSQL
     */
    Sql<?> getIdentitySelectSql(String qualifiedTableName, String columnName);

    /**
     * シーケンスの次の値を取得するためのSQLを返します。
     * <p>
     * {@link #supportsSequence()} が {@code true} を返す場合にのみ呼び出し可能です。
     * 
     * @param qualifiedSequenceName
     *            シーケンスの完全修飾名
     * @param allocationSize
     *            割り当てサイズ
     * @return シーケンスの次の値を取得するためのSQL
     */
    Sql<?> getSequenceNextValSql(String qualifiedSequenceName,
            long allocationSize);

    /**
     * {@link ResultSet} の {@link JdbcType} を返します。
     * <p>
     * {@link #supportsResultSetReturningAsOutParameter()} が {@code true}
     * を返す場合にのみ呼び出し可能です。
     * 
     * @return {@link ResultSet} の {@link JdbcType}
     */
    JdbcType<ResultSet> getResultSetType();

    /**
     * 引用符で囲みます。
     * 
     * @param name
     *            テーブルやカラムの名前
     * @return {@code name} を引用符で囲んだ値
     */
    String applyQuote(String name);

    /**
     * 引用符を取り除きます。
     * 
     * @param name
     *            テーブルやカラムの名前
     * @return {@code name} から引用符を除去した値
     */
    String removeQuote(String name);

    /**
     * SQL例外の根本原因を返します。
     * 
     * @param sqlException
     *            SQL例外
     * @return 根本原因
     */
    Throwable getRootCause(SQLException sqlException);

    /**
     * {@link Domain} をJDBCの型とマッピングするビジターを返します。
     * 
     * @return {@link Domain} をJDBCの型とマッピングするビジター
     */
    JdbcMappingVisitor getJdbcMappingVisitor();

    /**
     * SQLのバインド変数にマッピングされる {@link Domain} をログ用のフォーマットされた文字列へと変換するビジターを返します。
     * 
     * @return SQLのバインド変数にマッピングされる {@link Domain} をログ用のフォーマットされた文字列へと変換するビジター
     */
    SqlLogFormattingVisitor getSqlLogFormattingVisitor();

}