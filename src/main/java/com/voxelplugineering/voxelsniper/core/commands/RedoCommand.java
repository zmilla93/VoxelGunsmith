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
package com.voxelplugineering.voxelsniper.core.commands;

import com.voxelplugineering.voxelsniper.api.entity.Player;
import com.voxelplugineering.voxelsniper.api.service.command.CommandSender;
import com.voxelplugineering.voxelsniper.api.service.text.TextFormat;
import com.voxelplugineering.voxelsniper.core.util.Context;

/**
 * A command get fetching the help information for a brush.
 */
public class RedoCommand extends Command
{

    /**
     * Creates a new Command instance.
     */
    public RedoCommand(Context context)
    {
        super("redo", "Redoes your last n undone changes. Usage: /redo [n]", context);
        setAliases("vredo");
        setPermissions("voxelsniper.command.redo");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        int n = 1;
        if (args.length > 0)
        {
            try
            {
                n = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored)
            {
                assert true;
            }
            if (n < 1)
            {
                n = 1;
            }
        }
        if (sender.isPlayer())
        {
            ((Player) sender).redoHistory(n);
            return true;
        }
        sender.sendMessage(TextFormat.RED + "Sorry, this is a player only command.");
        return true;
    }
}
