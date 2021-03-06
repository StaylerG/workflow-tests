/*
 * (C) Copyright 2006-2008 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephane Lacoin (Nuxeo EP Software Engineer)
 */
package org.nuxeo.ecm.platform.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class ExpressionContext extends ELContext {

    private static final Map<String, Method> DEFAULT_FUNCTIONS = //
            Stream.of(Functions.class.getMethods())
                    .filter(m -> Modifier.isStatic(m.getModifiers()))
                    .collect(collectingAndThen(toMap(m -> "nx:" + m.getName(), Function.identity()), Collections::unmodifiableMap));

    private static class MyVariableMapper extends VariableMapper {

        protected final Map<String, ValueExpression> map = new HashMap<String, ValueExpression>();

        @Override
        public ValueExpression resolveVariable(String variable) {
            return map.get(variable);
        }

        @Override
        public ValueExpression setVariable(String variable, ValueExpression expression) {
            return map.put(variable, expression);
        }
    }

    private static class MyFunctionMapper extends FunctionMapper {

        private final Map<String, Method> map = new HashMap<String, Method>();

        @SuppressWarnings("unused")
        public void setFunction(String prefix, String localName, Method method) {
            map.put(prefix + ":" + localName, method);
        }

        @Override
        public Method resolveFunction(String prefix, String localName) {
            String key = prefix + ":" + localName;
            return map.getOrDefault(key, DEFAULT_FUNCTIONS.get(key));
        }

        @Override
        public void mapFunction(String prefix, String localName, Method method) {
            map.put(prefix + ":" + localName, method);
        }
    }

    protected final ELResolver resolver = new ExpressionResolver();

    protected final FunctionMapper functionMapper = new MyFunctionMapper();

    protected final VariableMapper variableMapper = new MyVariableMapper();

    @Override
    public ELResolver getELResolver() {
        return resolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }

}
