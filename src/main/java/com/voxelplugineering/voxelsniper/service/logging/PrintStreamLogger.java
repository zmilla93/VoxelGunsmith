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
package com.voxelplugineering.voxelsniper.service.logging;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintStream;

/**
 * A logger wrapping a {@link PrintStream}. Designed for use for logging to consoles such as
 * standard out.
 */
public class PrintStreamLogger implements com.voxelplugineering.voxelsniper.service.logging.Logger
{

    private final PrintStream stream;
    private LogLevel level;

    /**
     * Creates a new {@link PrintStreamLogger}.
     * 
     * @param stream The print stream to wrap
     * @param level The logging level
     */
    public PrintStreamLogger(PrintStream stream, LogLevel level)
    {
        this.stream = checkNotNull(stream);
        this.level = checkNotNull(level);
    }

    /**
     * Creates a new {@link PrintStreamLogger}.
     * 
     * @param stream The print stream to wrap
     */
    public PrintStreamLogger(PrintStream stream)
    {
        this(stream, LogLevel.INFO);
    }

    @Override
    public LogLevel getLevel()
    {
        return this.level;
    }

    @Override
    public void setLevel(LogLevel level)
    {
        checkNotNull(level);
        this.level = level;
    }

    @Override
    public void log(LogLevel level, String msg)
    {
        if (level.isGEqual(this.level))
        {
            printWithPrefix("[" + level.name() + "]", msg);
        }
    }

    @Override
    public void debug(String msg)
    {
        if (this.level.isGEqual(LogLevel.DEBUG))
        {
            printWithPrefix("[DEBUG]", msg);
        }
    }

    @Override
    public void info(String msg)
    {
        if (this.level.isGEqual(LogLevel.INFO))
        {
            printWithPrefix("[INFO]", msg);
        }
    }

    @Override
    public void warn(String msg)
    {
        if (this.level.isGEqual(LogLevel.WARN))
        {
            printWithPrefix("[WARNING]", msg);
        }
    }

    @Override
    public void error(String msg)
    {
        printWithPrefix("[ERROR]", msg);
    }

    @Override
    public void error(Throwable e)
    {
        e.printStackTrace();
    }

    @Override
    public void error(Throwable e, String msg)
    {
        printWithPrefix("[ERROR]", msg);
        e.printStackTrace();
    }

    private void printWithPrefix(String prefix, String msg)
    {
        String[] lines = msg.split("\n");
        for (String l : lines)
        {
            this.stream.println(prefix + " " + l);
        }
    }

}
