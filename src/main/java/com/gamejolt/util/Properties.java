/**
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.gamejolt.util;

import com.gamejolt.GameJoltException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Properties {
    private Map<String, String> values = new LinkedHashMap<String, String>();

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public int getInt(String key) {
        String value = get(key);
        if (isBlank(key)) return 0;
        return Integer.parseInt(value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public URL getUrl(String key) {
        try {
            return new URL(get(key));
        } catch (MalformedURLException e) {
            throw new GameJoltException(e);
        }
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public List<String> getDelimited(String key, String delimiter) {
        List<String> parts = new ArrayList<String>();
        if (contains(key)) {
            String[] pieces = get(key).split(delimiter);
            for (String piece : pieces) {
                if (piece.trim().length() > 0) {
                    parts.add(piece.trim());
                }
            }
        }
        return parts;
    }

    public boolean isBlank(String key) {
        String value = get(key);
        return value == null || value.trim().length() == 0;
    }
}
