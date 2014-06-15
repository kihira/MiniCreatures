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

package kihira.minicreatures.common.personality;

import net.minecraft.util.MathHelper;

import java.io.Serializable;

public class MoodVariable implements Serializable {

    private int currentValue = 0;
    private int maxValue = 0;
    private int minValue = 0;
    private int restingValue = 0;

    public MoodVariable() {}

    public MoodVariable(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void changeValue(float change) {
        MathHelper.clamp_int(this.currentValue += change, this.minValue, this.maxValue);
    }

    public boolean isWithinBounds(int value) {
        return value >= this.getMinValue() && value <= this.getMaxValue();
    }

    //Setters
    public MoodVariable setCurrentValue(int value) {
        MathHelper.clamp_int(this.currentValue = value, this.minValue, this.maxValue);
        return this;
    }

    public MoodVariable setMaxValue(int value) {
        this.maxValue = value;
        return this;
    }

    public MoodVariable setMinValue(int value) {
        this.minValue = value;
        return this;
    }

    public void setRestingValue(int restingValue) {
        this.restingValue = restingValue;
    }

    //Getters
    public int getCurrentValue() {
        return this.currentValue;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public int getRestingValue() {
        return restingValue;
    }

    @Override
    public String toString() {
        return String.format("[Max: %s, Min: %s, Current: %s, Resting: %s]", this.getMaxValue(), this.getMinValue(), this.getCurrentValue(), this.getRestingValue());
    }
}
