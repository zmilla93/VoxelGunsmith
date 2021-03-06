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
package com.voxelplugineering.voxelsniper.brush.effect.morphological;

import com.voxelplugineering.voxelsniper.brush.Brush;
import com.voxelplugineering.voxelsniper.brush.BrushKeys;
import com.voxelplugineering.voxelsniper.brush.BrushVars;
import com.voxelplugineering.voxelsniper.brush.ExecutionResult;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.shape.ComplexMaterialShape;
import com.voxelplugineering.voxelsniper.shape.MaterialShape;
import com.voxelplugineering.voxelsniper.shape.Shape;
import com.voxelplugineering.voxelsniper.shape.csg.CuboidShape;
import com.voxelplugineering.voxelsniper.shape.csg.PrimativeShapeFactory;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.Block;
import com.voxelplugineering.voxelsniper.world.Location;
import com.voxelplugineering.voxelsniper.world.World;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;
import com.voxelplugineering.voxelsniper.world.queue.ShapeChangeQueue;

import java.util.Optional;

/**
 * An abstract notion of a Morphological hit-or-miss transform with a moving window termed a
 * 'Structuring Element'.
 * 
 * This brush takes a shape, structuring element and morphological material operation, and changes
 * the material of the shape by the operation applied with the structuring element.
 * 
 * New operations should therefore be implementations of MorphologicalMaterialOperation, and
 * associated with their own brush that instantiates this class.
 */
public abstract class FilterBrush extends Brush
{

    private final FilterOperation operation;

    /**
     * Creates a new {@link FilterBrush}.
     * 
     * @param operation The filter operation
     */
    public FilterBrush(FilterOperation operation)
    {
        this.operation = operation;
    }

    protected abstract String getName();

    @Override
    public ExecutionResult run(Player player, BrushVars args)
    {
        boolean excludeFluid = true;
        if (args.has(BrushKeys.EXCLUDE_FLUID))
        {
            excludeFluid = args.get(BrushKeys.EXCLUDE_FLUID, Boolean.class).get();
        }

        Optional<Shape> s = args.get(BrushKeys.SHAPE, Shape.class);
        if (!s.isPresent())
        {
            player.sendMessage("You must have at least one shape brush before your" + this.getName() + "brush.");
            return ExecutionResult.abortExecution();
        }

        Optional<MaterialState> m = args.get(BrushKeys.MATERIAL, MaterialState.class);
        if (!m.isPresent())
        {
            player.sendMessage("You must select a material.");
            return ExecutionResult.abortExecution();
        }

        Optional<String> kernalShape = args.get(BrushKeys.KERNEL, String.class);
        Optional<Double> kernalSize = args.get(BrushKeys.KERNEL_SIZE, Double.class);
        double size = kernalSize.orElse(1.0);
        String kernelString = kernalShape.orElse("voxel");
        Optional<Shape> se = PrimativeShapeFactory.createShape(kernelString, size);
        if (!se.isPresent())
        {
            se = Optional.<Shape>of(new CuboidShape(3, 3, 3, new Vector3i(1, 1, 1)));
        }

        Optional<Block> l = args.get(BrushKeys.TARGET_BLOCK, Block.class);
        MaterialShape ms = new ComplexMaterialShape(s.get(), m.get());

        World world = player.getWorld();
        Location loc = l.get().getLocation();
        Shape shape = s.get();
        Shape structElem = se.get();

        // Extract the location in the world to x0, y0 and z0.
        for (int x = 0; x < ms.getWidth(); x++)
        {
            int x0 = loc.getFlooredX() + x - shape.getOrigin().getX();
            for (int y = 0; y < ms.getHeight(); y++)
            {
                int y0 = loc.getFlooredY() + y - shape.getOrigin().getY();
                for (int z = 0; z < ms.getLength(); z++)
                {
                    int z0 = loc.getFlooredZ() + z - shape.getOrigin().getZ();
                    if (!shape.get(x, y, z, false))
                    {
                        continue;
                    }

                    for (int a = 0; a < structElem.getWidth(); a++)
                    {
                        for (int b = 0; b < structElem.getHeight(); b++)
                        {
                            for (int c = 0; c < structElem.getLength(); c++)
                            {

                                if (!structElem.get(a, b, c, false))
                                {
                                    continue;
                                }

                                int a0 = a - structElem.getOrigin().getX();
                                int b0 = b - structElem.getOrigin().getY();
                                int c0 = c - structElem.getOrigin().getZ();

                                if (excludeFluid && world.getBlock(x0 + a0, y0 + b0, z0 + c0).get().getMaterial().getType().isLiquid())
                                {
                                    continue;
                                }

                                // Request visitor to perform check operation on
                                // relevant voxel.
                                MaterialState mat = world.getBlock(x0 + a0, y0 + b0, z0 + c0).get().getMaterial();
                                this.operation.checkPosition(x0, y0, z0, a0, b0, c0, world, mat);
                            }
                        }
                    }

                    // Request visitor to decide final material.
                    if (this.operation.getResult().isPresent())
                    {
                        ms.setMaterial(x, y, z, false, this.operation.getResult().get());
                    }
                    this.operation.reset();
                }
            }
        }
        new ShapeChangeQueue(player, loc, ms).flush();
        return ExecutionResult.continueExecution();
    }
}
