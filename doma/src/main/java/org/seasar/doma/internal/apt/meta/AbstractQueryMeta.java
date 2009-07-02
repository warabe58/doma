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
package org.seasar.doma.internal.apt.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

import org.seasar.doma.internal.jdbc.command.Command;
import org.seasar.doma.internal.jdbc.query.Query;

/**
 * @author taedium
 * 
 */
public abstract class AbstractQueryMeta implements QueryMeta {

    protected String name;

    protected ExecutableElement executableElement;

    protected List<String> typeParameterNames = new ArrayList<String>();

    protected String returnTypeName;

    protected List<String> thrownTypeNames = new ArrayList<String>();

    protected QueryKind queryKind;

    protected Integer queryTimeout;

    protected Boolean versionIgnored;

    protected Boolean versionIncluded;

    protected Boolean nullExcluded;

    protected Boolean optimisticLockExceptionSuppressed;

    protected Map<String, String> methodParameters = new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    public void addTypeParameterName(String typeParameterName) {
        typeParameterNames.add(typeParameterName);
    }

    public Iterator<String> getTypeParameterNames() {
        return typeParameterNames.iterator();
    }

    public String getReturnTypeName() {
        return returnTypeName;
    }

    public void setReturnTypeName(String returnTypeName) {
        this.returnTypeName = returnTypeName;
    }

    public void addThrownTypeName(String thrownTypeName) {
        thrownTypeNames.add(thrownTypeName);
    }

    public Iterator<String> getThrownTypeNames() {
        return thrownTypeNames.iterator();
    }

    public Class<? extends Query> getQueryClass() {
        if (queryKind == null) {
            return null;
        }
        return queryKind.getQueryClass();
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Command> getCommandClass() {
        if (queryKind == null) {
            return null;
        }
        return queryKind.getCommandClass();
    }

    public QueryKind getQueryKind() {
        return queryKind;
    }

    public void setQueryKind(QueryKind queryKind) {
        this.queryKind = queryKind;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Boolean isVersionIgnored() {
        return versionIgnored;
    }

    public void setVersionIgnored(Boolean versionIgnored) {
        this.versionIgnored = versionIgnored;
    }

    public Boolean isVersionIncluded() {
        return versionIncluded;
    }

    public void setVersionIncluded(Boolean versionIncluded) {
        this.versionIncluded = versionIncluded;
    }

    public Boolean isOptimisticLockExceptionSuppressed() {
        return optimisticLockExceptionSuppressed;
    }

    public void setOptimisticLockExceptionSuppressed(
            Boolean optimisticLockExceptionSuppressed) {
        this.optimisticLockExceptionSuppressed = optimisticLockExceptionSuppressed;
    }

    public Boolean isNullExcluded() {
        return nullExcluded;
    }

    public void setNullExcluded(Boolean nullExcluded) {
        this.nullExcluded = nullExcluded;
    }

    public Iterator<Map.Entry<String, String>> getMethodParameters() {
        return methodParameters.entrySet().iterator();
    }

    public void addMethodParameter(String parameterName, String parameterTypeName) {
        this.methodParameters.put(parameterName, parameterTypeName);
    }

}
