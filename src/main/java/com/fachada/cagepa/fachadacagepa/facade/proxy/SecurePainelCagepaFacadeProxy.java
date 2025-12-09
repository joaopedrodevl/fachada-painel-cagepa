package com.fachada.cagepa.fachadacagepa.facade.proxy;

import com.fachada.cagepa.fachadacagepa.domain.application.services.AuthService;
import com.fachada.cagepa.fachadacagepa.facade.ISecurePainelCagepaFacadeProxy;
import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurePainelCagepaFacadeProxy implements ISecurePainelCagepaFacadeProxy {
    private final PainelCagepaFacade painelCagepaFacade;
    private final AuthService authService;

    public SecurePainelCagepaFacadeProxy(PainelCagepaFacade painelCagepaFacade, AuthService authService) {
        this.painelCagepaFacade = painelCagepaFacade;
        this.authService = authService;
        this.authService.initializeDefaultAdmin();
    }

    @Override
    public String login(String username, String password) throws IOException {
        var login = authService.login(username, password);

        if (login != null) painelCagepaFacade.iniciarMonitoramento();

        return login;
    }
}
