package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.BulletOptimizeRequest;
import com.thomas.rgsga.dto.BulletOptimizeResponse;

public interface BulletOptimizeService {

    BulletOptimizeResponse optimizeBullet(BulletOptimizeRequest request);
}
