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
import kihira.minicreatures.common.personality.Mood;

public class GsonHelper {

    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Mood.class, new SubClassDeserializer<Mood>());

        return gsonBuilder.create();
    }

    public static String toJson(Object object) {
        return createGson().toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return createGson().fromJson(json, clazz);
    }
}
