/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.tmgg.web.perm;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * TODO ，复杂实现，目前较为简单
 * 如果内容较多，需考虑缓存到硬盘
 */
public class Session {

    private Map<Object, Object> attributes;


    public Collection<Object> getAttributeKeys() {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return Collections.emptySet();
        }
        return attributes.keySet();
    }


    public Object getAttribute(Object key) {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }

    public void setAttribute(Object key, Object value) {
        if (value == null) {
            removeAttribute(key);
        } else {
            getAttributes().put(key, value);
        }
    }

    public Object removeAttribute(Object key) {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return null;
        } else {
            return attributes.remove(key);
        }
    }


    private Map<Object, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }
}
