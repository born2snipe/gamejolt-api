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

import com.gamejolt.Trophy;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TrophyResponseParser {

    public List<Trophy> parse(String content) {
        List trophies = new ArrayList();

        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i += 6) {
            Properties properties = creatProperties(lines, i, 6);
            int id = properties.getInt("id");
            if (id == 0) break;
            String title = properties.get("title");
            Trophy.Difficulty difficulty = Trophy.Difficulty.valueOf(properties.get("difficulty").toUpperCase());
            String description = properties.get("description");
            URL imageUrl = properties.getUrl("image_url");
            String achieved = properties.get("achieved");
            trophies.add(new Trophy(id, title, difficulty, description, imageUrl, achieved));
        }

        return trophies;
    }

    private Properties creatProperties(String[] lines, int offset, int count) {
        String content = "";
        for (int i = offset; i < offset + count; i++) {
            content += lines[i] + "\n";
        }
        return new Properties(content);
    }
}
