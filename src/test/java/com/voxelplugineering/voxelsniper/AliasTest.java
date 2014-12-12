package com.voxelplugineering.voxelsniper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.voxelplugineering.voxelsniper.common.alias.AliasHandler;
import com.voxelplugineering.voxelsniper.common.alias.AliasRegistry;

/**
 * A test suite for the alias system
 */
public class AliasTest
{
    
    AliasRegistry alias;

    /**
     * Sets up an {@link AliasRegistry} for use during the tests.
     */
    @Before
    public void setup()
    {
        alias = new AliasRegistry();
    }
    
    /**
     * A basic test
     */
    @Test
    public void test()
    {
        alias.register("abc", "absd");
        
        String init = "test abc def";
        
        assertEquals("test absd def", alias.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test for shorter aliases having preference.
     */
    @Test
    public void testLength()
    {
        alias.register("abc def", "rawr");
        alias.register("abc", "absd");
        
        String init = "test abc def";
        
        assertEquals("test absd def", alias.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test for aliases containing multiple words.
     */
    @Test
    public void testMulti()
    {
        alias.register("abc def", "rawr");
        
        String init = "test abc def";
        
        assertEquals("test rawr", alias.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test that aliases cannot be infinitely repeated (if they contain themselves).
     */
    @Test
    public void testInfiniteProtection()
    {
        alias.register("abc", "abc def");
        
        String init = "test abc";
        
        assertEquals("test abc def", alias.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test that aliases cannot be infinitely repeated (if they contain themselves), but can be applied multiple times to different points.
     */
    @Test
    public void testInfiniteProtection2()
    {
        alias.register("abc", "abc def");
        
        String init = "test abc abc";
        
        assertEquals("test abc def abc def", alias.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test for a child inheriting a parents aliases
     */
    @Test
    public void testParentage()
    {
        AliasRegistry child = new AliasRegistry(alias);
        
        alias.register("abc", "absd");
        
        String init = "test abc def";
        
        assertEquals("test absd def", child.expand(init));
        
        alias.clear();
    }
    
    /**
     * A test for a child's aliases overriding those of a parent
     */
    @Test
    public void testParentage2()
    {
        AliasRegistry child = new AliasRegistry(alias);
        
        alias.register("abc", "rawr");
        child.register("abc", "absd");
        
        String init = "test abc def";
        
        assertEquals("test absd def", child.expand(init));
        
        alias.clear();
    }
    
    @Test
    public void testSave() throws IOException
    {
        File f = new File("test.json");
        AliasHandler handler = new AliasHandler();
        handler.registerTarget("test");
        AliasRegistry testReg = handler.getRegistry("test").get();
        testReg.register("abc", "rawr");
        testReg.register("abs", "dbh");
        testReg.register("af", "he");
        testReg.register("ag", "sdf");
        assertTrue(testReg.getAlias("abc").isPresent());
        handler.save(f);
        testReg.clear();
        assertTrue(!testReg.getAlias("abc").isPresent());
        handler.load(f);
        assertEquals(testReg.getAlias("abc").get(), "rawr");
        assertEquals(testReg.getAlias("abs").get(), "dbh");
        assertEquals(testReg.getAlias("af").get(), "he");
        assertEquals(testReg.getAlias("ag").get(), "sdf");
    }
}