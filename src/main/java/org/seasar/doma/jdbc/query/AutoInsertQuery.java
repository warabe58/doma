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
package org.seasar.doma.jdbc.query;

import static org.seasar.doma.internal.util.AssertionUtil.assertNotNull;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;

import org.seasar.doma.internal.jdbc.entity.AbstractPostInsertContext;
import org.seasar.doma.internal.jdbc.entity.AbstractPreInsertContext;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlBuilder;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.entity.EntityPropertyDesc;
import org.seasar.doma.jdbc.entity.EntityDesc;
import org.seasar.doma.jdbc.entity.GeneratedIdPropertyDesc;
import org.seasar.doma.jdbc.entity.Property;
import org.seasar.doma.jdbc.id.IdGenerationConfig;
import org.seasar.doma.message.Message;

/**
 * @author taedium
 * @param <ENTITY>
 *            エンティティ
 */
public class AutoInsertQuery<ENTITY> extends AutoModifyQuery<ENTITY> implements
        InsertQuery {

    protected boolean nullExcluded;

    protected GeneratedIdPropertyDesc<ENTITY, ?, ?> generatedIdPropertyDesc;

    protected IdGenerationConfig idGenerationConfig;

    public AutoInsertQuery(EntityDesc<ENTITY> entityDesc) {
        super(entityDesc);
    }

    @Override
    public void prepare() {
        super.prepare();
        assertNotNull(method, entityDesc, entity);
        executable = true;
        preInsert();
        prepareIdAndVersionPropertyDescs();
        prepareOptions();
        prepareTargetPropertyDescs();
        prepareIdValue();
        prepareVersionValue();
        prepareSql();
        assertNotNull(sql);
    }

    protected void preInsert() {
        AutoPreInsertContext<ENTITY> context = new AutoPreInsertContext<ENTITY>(
                entityDesc, method, config);
        entityDesc.preInsert(entity, context);
        if (context.getNewEntity() != null) {
            entity = context.getNewEntity();
        }
    }

    @Override
    protected void prepareIdAndVersionPropertyDescs() {
        super.prepareIdAndVersionPropertyDescs();
        generatedIdPropertyDesc = entityDesc.getGeneratedIdPropertyDesc();
        if (generatedIdPropertyDesc != null) {
            idGenerationConfig = new IdGenerationConfig(config, entityDesc);
            generatedIdPropertyDesc
                    .validateGenerationStrategy(idGenerationConfig);
            autoGeneratedKeysSupported = generatedIdPropertyDesc
                    .isAutoGeneratedKeysSupported(idGenerationConfig);
        }
    }

    protected void prepareTargetPropertyDescs() {
        targetPropertyDescs = new ArrayList<>(entityDesc
                .getEntityPropertyDescs().size());
        for (EntityPropertyDesc<ENTITY, ?> propertyDesc : entityDesc
                .getEntityPropertyDescs()) {
            if (!propertyDesc.isInsertable()) {
                continue;
            }
            Property<ENTITY, ?> property = propertyDesc.createProperty();
            property.load(entity);
            if (propertyDesc.isId()) {
                if (propertyDesc != generatedIdPropertyDesc
                        || generatedIdPropertyDesc
                                .isIncluded(idGenerationConfig)) {
                    targetPropertyDescs.add(propertyDesc);
                }
                if (generatedIdPropertyDesc == null
                        && property.getWrapper().get() == null) {
                    throw new JdbcException(Message.DOMA2020,
                            entityDesc.getName(), propertyDesc.getName());
                }
                continue;
            }
            if (propertyDesc.isVersion()) {
                targetPropertyDescs.add(propertyDesc);
                continue;
            }
            if (nullExcluded) {
                if (property.getWrapper().get() == null) {
                    continue;
                }
            }
            if (!isTargetPropertyName(propertyDesc.getName())) {
                continue;
            }
            targetPropertyDescs.add(propertyDesc);
        }
    }

    protected void prepareIdValue() {
        if (generatedIdPropertyDesc != null && idGenerationConfig != null) {
            ENTITY newEntity = generatedIdPropertyDesc.preInsert(entityDesc,
                    entity, idGenerationConfig);
            entity = newEntity;
        }
    }

    protected void prepareVersionValue() {
        if (versionPropertyDesc != null) {
            entity = versionPropertyDesc.setIfNecessary(entityDesc, entity, 1);
        }
    }

    protected void prepareSql() {
        Naming naming = config.getNaming();
        Dialect dialect = config.getDialect();
        PreparedSqlBuilder builder = new PreparedSqlBuilder(config,
                SqlKind.INSERT, sqlLogType);
        builder.appendSql("insert into ");
        builder.appendSql(entityDesc.getQualifiedTableName(naming::apply,
                dialect::applyQuote));
        builder.appendSql(" (");
        for (EntityPropertyDesc<ENTITY, ?> propertyDesc : targetPropertyDescs) {
            builder.appendSql(propertyDesc.getColumnName(naming::apply,
                    dialect::applyQuote));
            builder.appendSql(", ");
        }
        builder.cutBackSql(2);
        builder.appendSql(") values (");
        for (EntityPropertyDesc<ENTITY, ?> propertyDesc : targetPropertyDescs) {
            Property<ENTITY, ?> property = propertyDesc.createProperty();
            property.load(entity);
            builder.appendParameter(property.asInParameter());
            builder.appendSql(", ");
        }
        builder.cutBackSql(2);
        builder.appendSql(")");
        sql = builder.build(this::comment);
    }

    @Override
    public void generateId(Statement statement) {
        if (generatedIdPropertyDesc != null && idGenerationConfig != null) {
            ENTITY newEntity = generatedIdPropertyDesc.postInsert(entityDesc,
                    entity, idGenerationConfig, statement);
            entity = newEntity;
        }
    }

    @Override
    public void complete() {
        postInsert();
    }

    protected void postInsert() {
        AutoPostInsertContext<ENTITY> context = new AutoPostInsertContext<ENTITY>(
                entityDesc, method, config);
        entityDesc.postInsert(entity, context);
        if (context.getNewEntity() != null) {
            entity = context.getNewEntity();
        }
    }

    public void setNullExcluded(boolean nullExcluded) {
        this.nullExcluded = nullExcluded;
    }

    protected static class AutoPreInsertContext<E> extends
            AbstractPreInsertContext<E> {

        public AutoPreInsertContext(EntityDesc<E> entityDesc, Method method,
                Config config) {
            super(entityDesc, method, config);
        }
    }

    protected static class AutoPostInsertContext<E> extends
            AbstractPostInsertContext<E> {

        public AutoPostInsertContext(EntityDesc<E> entityDesc, Method method,
                Config config) {
            super(entityDesc, method, config);
        }
    }
}
