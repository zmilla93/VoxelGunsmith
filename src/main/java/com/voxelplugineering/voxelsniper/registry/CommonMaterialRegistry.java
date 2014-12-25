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
package com.voxelplugineering.voxelsniper.registry;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.world.material.Material;

/**
 * A standard material registry for materials.
 * 
 * @param <T> The underlying material type
 */
public class CommonMaterialRegistry<T> implements MaterialRegistry<T>
{

    private final WeakRegistry<T, Material> registry;
    private Material air;

    /**
     * Creates a new {@link CommonMaterialRegistry}.
     */
    public CommonMaterialRegistry()
    {
        this.registry = new WeakRegistry<T, Material>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Material getAirMaterial()
    {
        return this.air;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Material> getMaterial(String name)
    {
        return this.registry.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Material> getMaterial(T material)
    {
        return this.registry.get(material);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerMaterial(String name, T object, Material material)
    {
        this.registry.register(name, object, material);
        if (name.equalsIgnoreCase("air") || name.equalsIgnoreCase("void") || name.equalsIgnoreCase("null"))
        {
            this.air = material;
        }
    }

}