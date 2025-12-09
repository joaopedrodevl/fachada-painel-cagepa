package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Admin;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IAdminJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminService {

    private final IAdminJpaRepository adminJpaRepository;

    public AdminService(IAdminJpaRepository adminJpaRepository) {
        this.adminJpaRepository =  adminJpaRepository;
    }

    public Admin createAdmin(String username, String password) {
        var admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminJpaRepository.save(admin);

        return admin;
    }
}
