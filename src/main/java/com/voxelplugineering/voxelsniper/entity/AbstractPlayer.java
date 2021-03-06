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
package com.voxelplugineering.voxelsniper.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.voxelplugineering.voxelsniper.GunsmithLogger;
import com.voxelplugineering.voxelsniper.brush.BrushChain;
import com.voxelplugineering.voxelsniper.brush.BrushContext;
import com.voxelplugineering.voxelsniper.brush.BrushKeys;
import com.voxelplugineering.voxelsniper.brush.BrushManager;
import com.voxelplugineering.voxelsniper.brush.BrushVars;
import com.voxelplugineering.voxelsniper.brush.BrushWrapper;
import com.voxelplugineering.voxelsniper.brush.CommonBrushManager;
import com.voxelplugineering.voxelsniper.brush.GlobalBrushManager;
import com.voxelplugineering.voxelsniper.config.BaseConfiguration;
import com.voxelplugineering.voxelsniper.config.VoxelSniperConfiguration;
import com.voxelplugineering.voxelsniper.service.alias.AliasHandler;
import com.voxelplugineering.voxelsniper.service.alias.CommonAliasHandler;
import com.voxelplugineering.voxelsniper.service.alias.GlobalAliasHandler;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.RayTrace;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.Block;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.queue.ChangeQueue;
import com.voxelplugineering.voxelsniper.world.queue.CommonUndoQueue;
import com.voxelplugineering.voxelsniper.world.queue.UndoQueue;

import java.io.File;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * An abstract player.
 * 
 * @param <T> The underlying player type
 */
public abstract class AbstractPlayer<T> extends AbstractEntity<T> implements Player
{

    private File dataDir;
    private BrushManager personalBrushManager;
    private BrushChain currentBrush;
    private BrushVars brushVariables;
    private Queue<ChangeQueue> pending;
    private AliasHandler personalAliasHandler;
    private UndoQueue history;
    private boolean processing = false;

    /**
     * Creates a new {@link AbstractPlayer} with a weak reference to the player.
     * 
     * @param player The player object
     * @param parentBrushManager The parent brush manager
     * @param context The context
     */
    protected AbstractPlayer(T player, BrushManager parentBrushManager, Context context)
    {
        super(player);
        this.personalBrushManager = new CommonBrushManager(context, parentBrushManager);
        this.brushVariables = new BrushVars();
        this.pending = new LinkedList<ChangeQueue>();
        this.personalAliasHandler = new CommonAliasHandler(this, context.getRequired(GlobalAliasHandler.class));
        this.history = new CommonUndoQueue(this);
    }

    /**
     * Creates a new {@link AbstractPlayer} with a weak reference to the player.
     * 
     * @param player The player object
     * @param context The context
     */
    protected AbstractPlayer(T player, Context context)
    {
        super(player);
        this.personalBrushManager = new CommonBrushManager(context, context.getRequired(GlobalBrushManager.class));
        this.brushVariables = new BrushVars();
        this.pending = new LinkedList<ChangeQueue>();
        this.personalAliasHandler = new CommonAliasHandler(this, context.getRequired(GlobalAliasHandler.class));
        this.history = new CommonUndoQueue(this);
    }

    /**
     * Initializes the player.
     */
    public void init(Context context)
    {
        try
        {
            resetSettings(context);

            PlatformProxy platform = context.getRequired(PlatformProxy.class);
            String path = VoxelSniperConfiguration.playerDataDirectory;
            if (!path.isEmpty() && !path.endsWith(File.separator))
            {
                path += File.separator;
            }
            if (VoxelSniperConfiguration.useUUIDsForDataDirectories)
            {
                path += getUniqueId().toString();
            } else
            {
                path += getName();
            }
            this.dataDir = new File(platform.getRoot(), path);
            if (!this.dataDir.exists())
            {
                this.dataDir.mkdirs();
            }
        } catch (Exception e)
        {
            GunsmithLogger.getLogger().error(e, "Error setting up default player settings.");
        }
    }

    @Override
    public File getAliasSource()
    {
        return new File(this.dataDir, "aliases.json");
    }

    @Override
    public boolean isPlayer()
    {
        return true;
    }

    @Override
    public void sendMessage(String format, Object... args)
    {
        sendMessage(String.format(format, args));
    }

    @Override
    public BrushManager getBrushManager()
    {
        return this.personalBrushManager;
    }

    @Override
    public void setBrushManager(BrushManager manager)
    {
        this.personalBrushManager = manager;
    }

    @Override
    public void setCurrentBrush(BrushChain brush)
    {
        this.currentBrush = brush;
    }

    @Override
    public BrushChain getCurrentBrush()
    {
        return this.currentBrush;
    }

    @Override
    public BrushVars getBrushVars()
    {
        return this.brushVariables;
    }

    @Override
    public void resetSettings(Context context)
    {
        this.brushVariables.clear();
        this.brushVariables.set(BrushContext.GLOBAL, BrushKeys.PLAYER, this);
        PermissionProxy perms = context.get(PermissionProxy.class).orElse(null);
        if (perms != null && !perms.hasPermission(this, "voxelsniper.sniper"))
        {
            return;
        }
        String fullBrush = VoxelSniperConfiguration.defaultBrush;
        fullBrush = getAliasHandler().getRegistry("brush").get().expand(fullBrush);
        BrushChain brush = new BrushChain(fullBrush);
        for (String b : fullBrush.split(" "))
        {
            Optional<BrushWrapper> br = getBrushManager().getBrush(b);
            if (br.isPresent())
            {
                if (perms != null && !perms.hasPermission(this, br.get().getPermission()))
                {
                    sendMessage(VoxelSniperConfiguration.brushPermissionMessage, b);
                    continue;
                }
                brush.chain(br.get());
            } else
            {
                sendMessage("Could not find brush: " + b);
            }
        }
        setCurrentBrush(brush);
        sendMessage(VoxelSniperConfiguration.brushSetMessage, brush.getName());
        double size = VoxelSniperConfiguration.defaultBrushSize;
        this.brushVariables.set(BrushContext.GLOBAL, BrushKeys.BRUSH_SIZE, size);
        sendMessage(VoxelSniperConfiguration.brushSizeChangedMessage, size);
        String materialName = VoxelSniperConfiguration.defaultBrushMaterial;
        Material mat = getWorld().getMaterialRegistry().getAirMaterial();
        Optional<Material> material = getWorld().getMaterialRegistry().getMaterial(materialName);
        if (material.isPresent())
        {
            mat = material.get();
            sendMessage(VoxelSniperConfiguration.materialSetMessage, mat.getName());
            getBrushVars().set(BrushContext.GLOBAL, BrushKeys.MATERIAL, mat.getDefaultState());
            getBrushVars().set(BrushContext.GLOBAL, BrushKeys.MASK_MATERIAL, mat.getDefaultState());
            getBrushVars().set(BrushContext.GLOBAL, BrushKeys.MASK_MATERIAL_WILDCARD, VoxelSniperConfiguration.defaultMaterialMaskWildcard);
        }
    }

    @Override
    public void undoHistory(int n)
    {
        int c = this.history.undo(n);
        this.sendMessage(VoxelSniperConfiguration.undoMessage, c);
    }

    @Override
    public void redoHistory(int n)
    {
        int c = this.history.redo(n);
        this.sendMessage(VoxelSniperConfiguration.redoMessage, c);
    }

    @Override
    public boolean hasPendingChanges()
    {
        return this.pending.size() != 0;
    }

    @Override
    public int getPendingChangeCount()
    {
        return this.pending.size();
    }

    @Override
    public Optional<ChangeQueue> getNextPendingChange()
    {
        return Optional.ofNullable(this.pending.peek());
    }

    @Override
    public void addPending(ChangeQueue queue)
    {
        checkNotNull(queue, "ChangeQueue cannot be null");
        queue.reset();
        this.pending.add(queue);
    }

    @Override
    public void clearNextPending(boolean force)
    {
        if (!this.pending.isEmpty() && (this.pending.peek().isFinished() || force))
        {
            this.pending.remove();
        }
    }

    @Override
    public AliasHandler getAliasHandler()
    {
        return this.personalAliasHandler;
    }

    @Override
    public UndoQueue getUndoHistory()
    {
        return this.history;
    }

    @Override
    public Optional<Block> getTargetBlock()
    {
        int minY = BaseConfiguration.minimumWorldDepth;
        int maxY = BaseConfiguration.maximumWorldHeight;
        double step = BaseConfiguration.rayTraceStep;
        Vector3d eyeOffs = new Vector3d(0, BaseConfiguration.playerEyeHeight, 0);
        double range = VoxelSniperConfiguration.rayTraceRange;
        RayTrace ray = new RayTrace(getLocation(), getYaw(), getPitch(), range, minY, maxY, step, eyeOffs);
        ray.trace();
        return Optional.ofNullable(ray.getTargetBlock());
    }
    
    @Override
    public boolean isProcessing()
    {
        return this.processing;
    }

    @Override
    public void setProcessing(boolean state)
    {
        this.processing = state;
    }

}
