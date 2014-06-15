/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.minicreatures.common.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import kihira.minicreatures.common.personality.Mood;
import kihira.minicreatures.common.personality.MoodTest;
import kihira.minicreatures.common.personality.Personality;

public class GsonHelper {

    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Mood.class, new MoodAdapter<Mood>()); //
        gsonBuilder.registerTypeAdapter(Mood.class, new MoodAdapter<MoodTest>());

        return gsonBuilder.create();
    }

    public static String toJson(Personality personality) {
        return createGson().toJson(personality);
    }

    public static Personality fromJsonToPersonality(String json) {
        return createGson().fromJson(json, Personality.class);
    }

    public static Mood fromJsonToMood(JsonReader json) {
        return createGson().fromJson(json, Mood.class);
    }
}
