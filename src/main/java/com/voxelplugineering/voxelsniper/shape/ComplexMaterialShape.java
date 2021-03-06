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
package com.voxelplugineering.voxelsniper.shape;

import static com.google.common.base.Preconditions.checkNotNull;

import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link MaterialShape} which a material for each point in the shape.
 */
public class ComplexMaterialShape implements MaterialShape
{

    private short nextId = 1;
    private BiMap<Short, MaterialState> materialDictionary;
    private BiMap<MaterialState, Short> inverseDictionary;
    private byte[] materialsA;
    private byte[] materialsB;
    private Shape shape;
    private MaterialState defaultMaterial;

    /**
     * Creates a new {@link MaterialShape}.
     * 
     * @param shape The shape
     * @param defaultMaterial The default material for the shape
     */
    public ComplexMaterialShape(Shape shape, MaterialState defaultMaterial)
    {
        checkNotNull(shape);
        checkNotNull(defaultMaterial, "Default material cannot be null!");
        if (shape instanceof ComplexShape)
        {
            this.shape = shape;
        } else
        {
            this.shape = new ComplexShape(shape);
        }
        this.materialDictionary = HashBiMap.create();
        this.inverseDictionary = this.materialDictionary.inverse();
        this.materialsA = new byte[shape.getWidth() * shape.getLength() * shape.getHeight()];
        this.materialDictionary.put((short) 0, defaultMaterial);
        this.flood(defaultMaterial);
        this.defaultMaterial = defaultMaterial;
    }

    /**
     * Returns the wrapped shape.
     * 
     * @return The shape
     */
    @Override
    public Shape getShape()
    {
        return this.shape;
    }

    @Override
    public boolean isMutable()
    {
        return true;
    }

    /**
     * Gets the material at the given location.
     * 
     * @param x The x position to get
     * @param y The y position to get
     * @param z The z position to get
     * @param relative Whether to offset the given position to the origin
     * @return The material, or {@link Optional#empty()} if the point in the shape is not set
     */
    @Override
    public Optional<MaterialState> getMaterial(int x, int y, int z, boolean relative)
    {
        if (this.getShape().get(x, y, z, relative))
        {
            if (get(x, y, z) == -1)
            {
                set(x, y, z, (short) 0); // the default material
            }
            return Optional.of(this.materialDictionary.get(get(x, y, z)));
        }
        return Optional.empty();
    }

    private short get(int x, int y, int z)
    {
        int index = getIndex(x, y, z);
        short ret = (short) (this.materialsA[index] & 0xFF);
        if (this.nextId > 255)
        {
            ret = (short) (ret | (this.materialsB[index] & 0xFF));
        }
        return ret;
    }

    private void set(int x, int y, int z, short data)
    {
        int index = getIndex(x, y, z);
        this.materialsA[index] = (byte) (data & 0xff);
        if (this.nextId > 255)
        {
            if (this.materialsB == null)
            {
                this.materialsB = new byte[this.materialsA.length];
            }

            this.materialsB[index] = (byte) ((data & 0xff00) >> 8);
        }
    }

    /**
     * Sets the given point in the shape and applies the given material to that point.
     * 
     * @param x The x position
     * @param y The y position
     * @param z The z position
     * @param relative Whether to offset the given position to the origin
     * @param material The material to set the given point to
     */
    @Override
    public void setMaterial(int x, int y, int z, boolean relative, MaterialState material)
    {
        checkNotNull(material);
        if (!this.shape.isMutable())
        {
            this.shape = new ComplexShape(this.shape);
        }
        if (relative)
        {
            x += getShape().getOrigin().getX();
            y += getShape().getOrigin().getY();
            z += getShape().getOrigin().getZ();
        }
        if (x >= getShape().getWidth() || x < 0 || y >= getShape().getHeight() || y < 0 || z >= getShape().getLength() || z < 0)
        {
            throw new ArrayIndexOutOfBoundsException("Tried to set material outside of the shape. (" + x + ", " + y + ", " + z + ")");
        }
        short id = this.getOrRegisterMaterial(material);
        set(x, y, z, id);
        getShape().set(x, y, z, false);
    }

    /**
     * Unsets the given location in the shape and clears any material data for that point.
     * 
     * @param x The x position
     * @param y The y position
     * @param z The z position
     * @param relative Whether to offset the given position to the origin
     */
    public void unsetMaterial(int x, int y, int z, boolean relative)
    {
        if (!this.shape.isMutable())
        {
            this.shape = new ComplexShape(this.shape);
        }
        if (relative)
        {
            x += getShape().getOrigin().getX();
            y += getShape().getOrigin().getY();
            z += getShape().getOrigin().getZ();
        }
        if (x >= getShape().getWidth() || x < 0 || y >= getShape().getHeight() || y < 0 || z >= getShape().getLength() || z < 0)
        {
            throw new ArrayIndexOutOfBoundsException("Tried to unset material outside of the shape. (" + x + ", " + y + ", " + z + ")");
        }
        set(x, y, z, (short) -1);
        getShape().unset(x, y, z, false);
    }

    /**
     * Sets every point of the shape which is set to the given material.
     * 
     * @param material The material
     */
    @Override
    public void flood(MaterialState material)
    {
        short id = this.getOrRegisterMaterial(material);
        for (int x = 0; x < getShape().getWidth(); x++)
        {
            for (int y = 0; y < getShape().getHeight(); y++)
            {
                for (int z = 0; z < getShape().getLength(); z++)
                {
                    if (this.getShape().get(x, y, z, false))
                    {
                        set(x, y, z, id);
                    } else
                    {
                        set(x, y, z, (short) -1);
                    }
                }
            }
        }
    }

    /**
     * Fills the entire horizontal layers between y and height with the given material.
     * 
     * @param material The material
     * @param y The starting y layer
     * @param height The height of the area to fill
     */
    public void setHorizontalLayer(MaterialState material, int y, int height)
    {
        short id = this.getOrRegisterMaterial(material);
        int startIndex = getIndex(0, y, 0);
        int endIndex = getIndex(this.shape.getWidth() - 1, y + height - 1, this.shape.getLength() - 1) + 1;
        Arrays.fill(this.materialsA, startIndex, endIndex, (byte) (id & 0xff));
        if (this.nextId > 255)
        {
            if (this.materialsB == null)
            {
                this.materialsB = new byte[this.materialsA.length];
            }

            Arrays.fill(this.materialsB, startIndex, endIndex, (byte) ((id & 0xff00) >> 8));
        }
    }

    /**
     * Returns the id for the given material within this {@link MaterialShape}'s dictionary. If the
     * material is not found in the dictionary then the next id is allocated to it.
     * 
     * @param material The material to fetch
     * @return The id for the material
     */
    private short getOrRegisterMaterial(MaterialState material)
    {
        if (this.inverseDictionary.containsKey(material))
        {
            return this.inverseDictionary.get(material);
        }
        short id = this.nextId++;
        this.materialDictionary.put(id, material);
        return id;
    }

    /**
     * Returns the width of the underlying shape (x-axis size). Note that this is not the width of
     * the region set, but rather the width of the total possible volume.
     * 
     * @return The width
     */
    @Override
    public int getWidth()
    {
        return this.shape.getWidth();
    }

    /**
     * Returns the height of this shape (y-axis size). Note that this is not the height of the
     * region set, but rather the height of the total possible volume.
     * 
     * @return The height
     */
    @Override
    public int getHeight()
    {
        return this.shape.getHeight();
    }

    /**
     * Returns the length of this shape (z-axis size). Note that this is not the length of the
     * region set, but rather the length of the total possible volume.
     * 
     * @return The length
     */
    @Override
    public int getLength()
    {
        return this.shape.getLength();
    }

    /**
     * Returns the origin of this shape.
     * 
     * @return The origin
     */
    @Override
    public Vector3i getOrigin()
    {
        return this.shape.getOrigin();
    }

    /**
     * Gets the index for the given location.
     * 
     * @param x The X position
     * @param y The Y position
     * @param z The Z position
     * @return The index
     */
    protected int getIndex(int x, int y, int z)
    {
        return y * (this.shape.getWidth() * this.shape.getLength()) + z * (this.shape.getWidth()) + x;
    }

    /**
     * Floods the shape with the default material, effectively resetting it to initial conditions.
     */
    @Override
    public void reset()
    {
        flood(this.defaultMaterial);
    }

    @Override
    public boolean get(int x, int y, int z, boolean relative)
    {
        return this.shape.get(x, y, z, relative);
    }

    @Override
    public void set(int x, int y, int z, boolean relative)
    {
        this.shape.set(x, y, z, relative);
    }

    @Override
    public MaterialShape clone()
    {
        // TODO MaterialShape clone
        return null;
    }

    @Override
    public void unset(int x, int y, int z, boolean relative)
    {
        this.shape.unset(x, y, z, relative);
    }

    @Override
    public MaterialState getDefaultMaterial()
    {
        return this.defaultMaterial;
    }

    @Override
    public byte[] getLowerMaterialData()
    {
        return this.materialsA;
    }

    @Override
    public byte[] getUpperMaterialData()
    {
        return this.materialsB;
    }

    @Override
    public boolean hasExtraData()
    {
        // Note: nextId is always one greater than the actual max id required,
        // therefore this check is against 256, not 255.
        return this.nextId > 256;
    }

    @Override
    public Map<Short, MaterialState> getMaterialsDictionary()
    {
        return this.materialDictionary;
    }

    @Override
    public int getMaxMaterialId()
    {
        return this.nextId - 1;
    }

    /**
     * Registers a material at the given key index.
     * 
     * @param key The key
     * @param material The material
     */
    protected void registerMaterial(short key, MaterialState material)
    {
        this.materialDictionary.put(key, material);
        this.nextId = (short) Math.max(this.nextId, key);
    }

    @Override
    public void setDefaultMaterial(MaterialState material)
    {
        if (this.defaultMaterial.equals(material))
        {
            return;
        }
        MaterialState existing = this.defaultMaterial;
        this.defaultMaterial = material;
        short other = getOrRegisterMaterial(material);
        this.materialDictionary.put((short) 0, material);
        this.materialDictionary.put(other, existing);
        this.inverseDictionary.put(material, (short) 0);
        this.inverseDictionary.put(existing, other);
        if (this.materialsB != null)
        {
            for (int i = 0; i < this.materialsA.length; i++)
            {
                if (this.materialsA[i] == (other & 0xff) && this.materialsB[i] == (other & 0xff00 >> 8))
                {
                    this.materialsA[i] = 0;
                    this.materialsB[i] = 0;
                } else if (this.materialsA[i] == 0 && this.materialsB[i] == 0)
                {
                    this.materialsA[i] = (byte) (other & 0xff);
                    this.materialsB[i] = (byte) (other & 0xff00 >> 8);
                }
            }
        } else
        {
            for (int i = 0; i < this.materialsA.length; i++)
            {
                if (this.materialsA[i] == (other & 0xff))
                {
                    this.materialsA[i] = 0;
                } else if (this.materialsA[i] == 0)
                {
                    this.materialsA[i] = (byte) (other & 0xff);
                }
            }
        }
    }

    @Override
    public void fillFrom(Shape shape)
    {
        throw new UnsupportedOperationException(); // TODO
    }

}
