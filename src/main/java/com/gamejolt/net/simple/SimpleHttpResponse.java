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

package com.gamejolt.net.simple;


import com.gamejolt.net.HttpResponse;

public class SimpleHttpResponse extends HttpResponse {
    public final int code;
    public final byte[] content;

    SimpleHttpResponse(int code, byte[] content) {
        this.code = code;
        this.content = content;
    }

    public String getContentAsString() {
        return new String(content);
    }

    public int getCode() {
        return code;
    }

}
