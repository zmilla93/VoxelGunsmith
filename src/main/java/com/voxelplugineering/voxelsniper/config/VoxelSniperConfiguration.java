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
package com.voxelplugineering.voxelsniper.config;

/**
 * A configuration container which is exposed for user settings.
 */
public class VoxelSniperConfiguration
{
    //Default constants
    double rayTraceRange = 250;
    int blockChangesPerSecond = 160000;
    int undoHistorySize = 20;

    //Default brush settings
    String defaultBrush = "voxel material";
    double defaultBrushSize = 3;
    String defaultBrushMaterial = "AIR";

    //Default messages
    String permissionsRequiredMessage = "You require more permissions in order to perform this action.";
    String defaultHelpMessage = "No help is provided for this command.";
    String brushSizeChangedMessage = "Your brush size was changed to %.1f";
    String brushNotFoundMessage = "Could not find a brush part named %s";
    String brushSetMessage = "Your brush has been set to %s";
    String materialNotFoundMessage = "Could not find that material.";
    String materialSetMessage = "Set material to %s";
    String materialMaskSetMessage = "Set secondary material to %s";
}