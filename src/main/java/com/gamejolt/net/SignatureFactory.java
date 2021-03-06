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

package com.gamejolt.net;

import com.gamejolt.util.Checksum;

import java.util.Map;


public class SignatureFactory {
    private Checksum checksum = new Checksum();

    public String build(String baseUrl, Map<String, String> parameters) {
        QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
        queryStringBuilder.parameters(parameters);
        return checksum.md5(baseUrl + queryStringBuilder.toString());
    }

    protected void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }
}
