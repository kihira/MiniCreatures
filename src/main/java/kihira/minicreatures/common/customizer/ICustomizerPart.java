/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures.common.customizer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public interface ICustomizerPart {

    public boolean isPartValidForEntity(Entity entity, EnumPartCategory enumPartCategory);

    /**
     * These are the same parameters that are passed to render in ModelBase with the addition of the model being rendered
     */
    public void render(Entity entity, ModelBase model, float par2, float par3, float par4, float par5, float par6, float par7);
}
