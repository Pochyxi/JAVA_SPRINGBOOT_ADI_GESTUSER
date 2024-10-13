package com.adi.gestuser.service.impl;

import com.adi.gestuser.exception.ErrorCodeList;
import com.adi.gestuser.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class BaseServiceImpl<E, I> {

    private JpaRepository<E, I> baseRepository;

    public E findById(I id) {
        return  baseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException( ErrorCodeList.NF404));
    }
}
