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

import net.minecraft.util.math.MathHelper;

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

    /**
     * This changes the {@link #currentValue} by the specified amount. This also ensures that the value remains within
     * the {@link #minValue} and {@link #maxValue} range
     *
     * Note that it <i>does not</i> set it to the passed param but increases/decreases it by the amount specified.
     *
     * @param change The amount to change the current value by
     */
    public void changeValue(float change) {
        MathHelper.clamp_int(this.currentValue += change, this.minValue, this.maxValue);
    }

    /**
     * Checks if the passed value is within the {@link #minValue} and {@link #maxValue} range
     * @param value The value
     * @return Whether it is in range or not
     */
    public boolean isWithinBounds(int value) {
        return value >= this.getMinValue() && value <= this.getMaxValue();
    }

    /**
     * Sets current value.
     *
     * @param value the value
     * @return the current instance
     */
    public MoodVariable setCurrentValue(int value) {
        MathHelper.clamp_int(this.currentValue = value, this.minValue, this.maxValue);
        return this;
    }

    /**
     * Sets max value.
     *
     * @param value the value
     * @return the current instance
     */
    public MoodVariable setMaxValue(int value) {
        this.maxValue = value;
        return this;
    }

    /**
     * Sets min value.
     *
     * @param value the value
     * @return the current instance
     */
    public MoodVariable setMinValue(int value) {
        this.minValue = value;
        return this;
    }

    /**
     * Sets resting value.
     *
     * @param restingValue the resting value
     */
    public void setRestingValue(int restingValue) {
        this.restingValue = restingValue;
    }

    /**
     * Gets current value.
     *
     * @return the current value
     */
    public int getCurrentValue() {
        return this.currentValue;
    }

    /**
     * Gets max value.
     *
     * @return the max value
     */
    public int getMaxValue() {
        return this.maxValue;
    }

    /**
     * Gets min value.
     *
     * @return the min value
     */
    public int getMinValue() {
        return this.minValue;
    }

    /**
     * Gets resting value.
     *
     * @return the resting value
     */
    public int getRestingValue() {
        return restingValue;
    }

    @Override
    public String toString() {
        return String.format("[Max: %s, Min: %s, Current: %s, Resting: %s]", this.getMaxValue(), this.getMinValue(), this.getCurrentValue(), this.getRestingValue());
    }
}
