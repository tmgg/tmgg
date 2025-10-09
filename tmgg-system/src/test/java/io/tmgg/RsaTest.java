package io.tmgg;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.nio.charset.StandardCharsets;

public class RsaTest {
    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJOxhk/V+ze0hw4kgSTFZRvJ4Jkx4unBHPKMLZjepCOG1HDFLTg9WUbGLXIh/mVe1Zv8Z5g1TjYXDNsqrZT1yeUxqRCdtyqygWtU9FQzE9NXLUij5YoO5WLQqoMXoEd1nRUJaA/EnUHcMJ/+D9TJeDMaK0lrIuB3Cl/DDxUo5h4bAgMBAAECgYAisfPm4lIrEl4drOanoDVx0zlPU6BGIGbv9Y0qmba+X8idGzzVtte3oAuCAclUv/P3OlKNt4wBeeW7wKlZUmoUcXv2Q4OcS1AesfBb4tBQJbpKc+pI6IcDv9nXf4J1EIwCEGPhvifIZmOaU8kx9o+zYoARAdwJJCs0bS7viBaSFQJBAMLtoprMvYAdOmAMMXyW34lrXZofXxUf8s1x3gS6ZV3lgoR3RKs3lL/FLpnmMD0ORaGSHGajNqw+QPfvabDhEm8CQQDB92MUJyoUB5alSjY7RQHe3FQ92YrumS1JJDgz8oT3VRb7RJ00ZIcnejjKZIcLtWEPFa38xi7/YN3YbbgbG5UVAkEAmg7eOKIgEyUGgeqhIerL+PvWGn041KVPbHfWFL6MZs2BfdgVeYyQD5Bz6EY5Ucf52Fx1/dh42WDKUCnzykvYGQJAM+Z+HGZeW/2r0Y6J9FICrh9Ga0ZKZo2WEHwgjcXWzld2TsWNSvebMnUBvSYo/HkL/31NkNV2vd6tfbveB2LaAQJAX3CxDY60GbD7iJjrLTaOWvcJM+ZFuOlJs+6OzdnViYGytE+KnfRlmsv4J188K3pFHcfp6H5HTXN7EH5bhEw0LA==";
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTsYZP1fs3tIcOJIEkxWUbyeCZMeLpwRzyjC2Y3qQjhtRwxS04PVlGxi1yIf5lXtWb/GeYNU42FwzbKq2U9cnlMakQnbcqsoFrVPRUMxPTVy1Io+WKDuVi0KqDF6BHdZ0VCWgPxJ1B3DCf/g/UyXgzGitJayLgdwpfww8VKOYeGwIDAQAB";
    public static void main(String[] args) {

        RSA rsa = SecureUtil.rsa(privateKey,publicKey);
        System.out.println(rsa.getPrivateKeyBase64());
        System.out.println(rsa.getPublicKeyBase64());

        System.out.println(rsa.decryptStr("jlK9S+9xPC/G8m4zgxg5O7lcsVJZHWKUyFnTOINx8P8L35Zg96kOu9vVCHXDE0frFX0kpsG7o6Vlk/j/rdCXFCgipz2pW5yYA0JTrN9cK1IzvUjmyyWGa+XQqj6RwhehRi0zI0XsI79uQzsDQAEtekDr1M6t7ZByOpB1eylkVhU=", KeyType.PrivateKey));
    }
}
