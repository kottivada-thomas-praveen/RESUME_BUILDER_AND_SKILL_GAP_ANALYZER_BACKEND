package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.CareerRequest;
import com.thomas.rgsga.dto.CareerResponse;
import com.thomas.rgsga.entity.User;

public interface CareerService {

    CareerResponse recommendCareers(User user, CareerRequest request);
}
