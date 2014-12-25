/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.api.entity;

import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;

/**
 * Represents an entity within a world.
 */
public interface Entity
{

    /**
     * Gets the world that this player is currently within.
     * 
     * @return this player's world
     */
    World getWorld();

    /**
     * Gets the name of the entity, may be a generic name if the entity has no special name.
     * 
     * @return The name
     */
    String getName();

    /**
     * Gets the EntityType for this entity.
     * 
     * @return The entity type
     */
    EntityType getType();

    /**
     * Gets the location of this entity.
     * 
     * @return The location
     */
    Location getLocation();

    /**
     * Sets the location of this entity.
     * <p>
     * TODO proper handling for cross-world transfers?
     * 
     * @param newLocation The new location
     */
    void setLocation(Location newLocation);

}