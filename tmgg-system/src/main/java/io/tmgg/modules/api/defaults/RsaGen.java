package io.tmgg.modules.api.defaults;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;

import java.security.KeyPairGenerator;

public class RsaGen {
    public static void main(String[] args) {
        RSA rsa = SecureUtil.rsa();

        System.out.println(rsa.getPrivateKeyBase64());
        System.out.println(rsa.getPublicKeyBase64());


/*
        MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKiHuMTi+8jNfyBj8gidcAecPZ3hKUbvLnp/rUDoneOMpIuWMFHtqZA7+vV8x1+qh/DXVTWeyerTVSYIMFD82pSJtyOun7Xnba3EDNtnvRR6kw7oUVNiYORmSXk8aB5IwU6E166LjCYccfbG5MI+gIISpSCG7vKIJvl2pHyT1l0lAgMBAAECgYADpTE5RI65p6EiwY3JHCQBwpQP8mP0nmLrM9dXTE04pa7N5wWE/JbOHd4DjVSAAB+eZxu+R501Im5qnp+c64JTQA32GRu1OvmrOldAP8Wh/7Z6J1NVCtlyNIo1Q3i4ZAqKZ2v9mu33kd9AiED/DHaoCJO81bHjfJTwyNcU0bfVyQJBAOBUfdnbtXlKSb3P9TtLPL1gQCmNpA0v2NS0v2v2xPxCTv/oAl5tRXsfTLGbEuAEwH8AOoFJ7SqEAWIc6jEUEe8CQQDAUpFKjAl/O4Nn1PjPhWNa3sF/SeYo9WEsDa2Xf7NgmCkaPC3BfANfhOBZoxBYMiUpViVfP5hxlczHDz0AcEYrAkA7PbTago4DEN42dSOi2/SXYkKvdos7PEZ7rZvbjBhrMauhIwj1HNA7eoGLaZNre3XGVC4fFIHAN4oR+ebSvSUbAkA5TOBo+4wg0lR2bSesfKt7eX7hM91VOra272RXDP8euncUj/V5/j7rCKXnTwQhot8bj8UWudXrkZwvesTMCqd9AkAQojuV3N6OhfuiPKEjRMyMIin9xUCzwi/NFQDjMfPvbH7fE9ftvdaB0WrKhCTEr38/Nmfwk8GHfPsTRH5aEs78
        MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoh7jE4vvIzX8gY/IInXAHnD2d4SlG7y56f61A6J3jjKSLljBR7amQO/r1fMdfqofw11U1nsnq01UmCDBQ/NqUibcjrp+1522txAzbZ70UepMO6FFTYmDkZkl5PGgeSMFOhNeui4wmHHH2xuTCPoCCEqUghu7yiCb5dqR8k9ZdJQIDAQAB
*/





    }
}
