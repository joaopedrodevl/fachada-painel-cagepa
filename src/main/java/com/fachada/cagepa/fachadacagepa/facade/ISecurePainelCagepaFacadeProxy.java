package com.fachada.cagepa.fachadacagepa.facade;

import java.io.IOException;

public interface ISecurePainelCagepaFacadeProxy {
    String login(String username, String password) throws IOException;
}
