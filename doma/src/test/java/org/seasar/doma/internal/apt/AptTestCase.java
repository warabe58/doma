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
package org.seasar.doma.internal.apt;

import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import junit.framework.AssertionFailedError;

import org.seasar.aptina.unit.AptinaTestCase;
import org.seasar.doma.internal.util.Resources;
import org.seasar.doma.message.MessageCode;

/**
 * @author taedium
 * 
 */
public abstract class AptTestCase extends AptinaTestCase {

    protected Locale locale = Locale.JAPAN;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/test/java");
        addOption("-Atest=true");
        setCharset("UTF-8");
        setLocale(locale);
    }

    protected String getExpectedContent() throws Exception {
        String path = getClass().getName().replace(".", "/");
        String suffix = "_" + getName().substring("test".length()) + ".txt";
        return Resources.getResourceAsString(path + suffix);
    }

    protected void assertGeneratedSource(Class<?> originalClass)
            throws Exception {
        String generatedClassName = originalClass.getName()
                + Options.DEFAULT_SUFFIX;
        try {
            assertEqualsGeneratedSource(getExpectedContent(), generatedClassName);
        } catch (AssertionFailedError error) {
            System.out.println(getGeneratedSource(generatedClassName));
            throw error;
        }
    }

    protected void assertMessageCode(MessageCode messageCode) {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = getDiagnostics();
        if (diagnostics.size() == 1) {
            if (messageCode == extractMessageCode(diagnostics.get(0))) {
                return;
            }
        }
        fail();
    }

    protected MessageCode getMessageCode() {
        for (Diagnostic<? extends JavaFileObject> diagnostic : getDiagnostics()) {
            return extractMessageCode(diagnostic);
        }
        return null;
    }

    protected MessageCode extractMessageCode(
            Diagnostic<? extends JavaFileObject> diagnostic) {
        String message = diagnostic.getMessage(locale);
        int start = message.indexOf('[');
        int end = message.indexOf(']');
        if (start > 0 && end > 0) {
            String code = message.substring(start + 1, end);
            if (code.startsWith("DOMA")) {
                return Enum.valueOf(MessageCode.class, code);
            }
        }
        return null;
    }

}
