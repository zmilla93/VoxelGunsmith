package com.voxelplugineering.voxelsniper.shape;

import java.util.Map;

import com.voxelplugineering.voxelsniper.api.shape.Shape;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.shape.csg.CuboidShape;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.util.nbt.CompoundTag;

/**
 * A named {@link ComplexMaterialShape}.
 */
public class NamedWorldSection extends ComplexMaterialShape
{

    private String name;

    /**
     * Creates a new {@link NamedWorldSection}.
     * 
     * @param shape The shape
     * @param defaultMaterial The default material
     */
    public NamedWorldSection(Shape shape, Material defaultMaterial)
    {
        super(shape, defaultMaterial);
        this.name = "";
    }

    /**
     * Creates a new {@link NamedWorldSection}. This shape is initialized as a
     * shallow copy of the given shape.
     * 
     * @param shape The {@link ComplexMaterialShape} to replicate.
     */
    public NamedWorldSection(ComplexMaterialShape shape)
    {
        super(shape.getShape(), shape.getDefaultMaterial());
    }

    /**
     * Creates a new {@link NamedWorldSection} with the given material
     * dictionary.
     * 
     * @param shape The shape
     * @param materialDict The material dictionary to use
     */
    public NamedWorldSection(CuboidShape shape, Map<Short, Material> materialDict)
    {
        super(shape, materialDict.get(0));
        for (short key : materialDict.keySet())
        {
            this.registerMaterial(key, materialDict.get(key));
        }
    }

    /**
     * Gets the name of this shape.
     * 
     * @return The name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name for this shape.
     * 
     * @param name The new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets if this world section has any tile entity data.
     * 
     * @return Has tile entities
     */
    public boolean hasTileEntities()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the tile entities within this world section. The vector keys are the
     * tiles position relative to the origin of this world section.
     * 
     * @return The tiles
     */
    public Map<Vector3i, CompoundTag> getTileEntities()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Sets what tile entities are stored within this world section. The vector keys
     * should the locations of the tiles relative to the origin of the world
     * section.
     * 
     * @param tileEntitiesMap The new tile entites
     */
    public void setTileEntityMap(Map<Vector3i, CompoundTag> tileEntitiesMap)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Gets if this world section has any entity data.
     * 
     * @return Has entities
     */
    public boolean hasEntities()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the entities within this world section. The vector keys are the
     * entities position relative to the origin of this world section.
     * 
     * @return The entities
     */
    public Map<Vector3i, CompoundTag> getEntities()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Sets what entities are stored within this world section. The vector keys
     * should the locations of the entities relative to the origin of the world
     * section.
     * 
     * @param entitiesMap The new entities
     */
    public void setEntitiesMap(Map<Vector3i, CompoundTag> entitiesMap)
    {
        // TODO Auto-generated method stub

    }

}