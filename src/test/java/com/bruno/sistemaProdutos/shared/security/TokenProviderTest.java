package com.bruno.sistemaProdutos.shared.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    private TokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String secretKey = "X9k@Lm2#Qv7!pR4$Nz8&Ta1*Yw6%Ud3^Jc5+Hg0=Mf2?Bx9/Kr4~Pe7;Sl1:Do8|Wi3<";
    private final Long expiration = 3600000L;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "key", secretKey);
        ReflectionTestUtils.setField(tokenProvider, "expiration", expiration);
    }

    @Test
    @DisplayName("Deve gerar token JWT a partir de Authentication")
    void deveGerarTokenApartirDeAuthentication() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(tokenProvider.isTokenValid(token));
        assertEquals("usuario@teste.com", tokenProvider.getUsernameFromToken(token));
        assertEquals("usuario@teste.com", tokenProvider.getUsername(token));
    }

    @Test
    @DisplayName("Deve validar token gerado com sucesso")
    void deveValidarTokenValido() {
        String token = tokenProvider.buildToken("admin@teste.com");

        boolean isValid = tokenProvider.isTokenValid(token);

        assertTrue(isValid);
        assertEquals("admin@teste.com", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Deve retornar false para token inválido ou corrompido")
    void deveRetornarFalseParaTokenInvalido() {
        boolean isValid = tokenProvider.isTokenValid("token.invalido.aqui");

        assertFalse(isValid);
    }
}
