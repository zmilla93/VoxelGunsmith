package com.voxelplugineering.voxelsniper.api.service.persistence;

import com.google.common.base.Optional;


public interface DataSource
{

    Optional<String> getName();
    
    boolean exists();
    
}
