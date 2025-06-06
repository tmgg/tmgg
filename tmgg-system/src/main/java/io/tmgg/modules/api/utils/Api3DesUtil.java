package io.tmgg.modules.api.utils;


import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
@UtilityClass
public class Api3DesUtil {

	/**
	 * 转换成十六进制字符串
	 * @param
	 * @return
	 */
	public static byte[] hex(String key) {
		String f = DigestUtils.md5Hex(key);
		byte[] bkeys = new String(f).getBytes();
		byte[] enk = new byte[24];
		for (int i = 0; i < 24; i++) {
			enk[i] = bkeys[i];
		}
		return enk;
	}

	/**
	 * 3DES加密
	 * @param key 密钥
	 * @param srcStr 将加密的字符串
	 * @return
	 */
	public static String encode3Des(String key, String srcStr) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(srcStr)) {
			return null;
		}
		byte[] keybyte = hex(key);
		byte[] src = srcStr.getBytes();
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 加密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			String pwd = Base64.encodeBase64String(c1.doFinal(src));
			// return c1.doFinal(src);//在单一方面的加密或解密
			return pwd;
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 3DES解密
	 * @param key 加密密钥
	 * @param desStr 解密后的字符串
	 * @return
	 */
	public static String decode3Des(String key, String desStr) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(desStr)) {
			return null;
		}
		Base64 base64 = new Base64();
		byte[] keybyte = hex(key);
		byte[] src = base64.decode(desStr);
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 解密
			Cipher c1 = Cipher.getInstance("DESede");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			String pwd = new String(c1.doFinal(src));
			return pwd;
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

/*	public static void main(String args[]) {
		String key = "2f08d474c4e94bcaa4a9a61a182af854";
		String substring = key.substring(0, 24);
		System.out.println("密钥：" + substring.len());
		String idcard = "130682199606071234sdfs";
		String encode = Api3DesUtil.encode3Des(key, idcard);
		System.out.println("原串：" + idcard);
		System.out.println("加密后的串：" + encode);
		System.out.println("解密后的串：" + Api3DesUtil.decode3Des(key, encode));
	}*/
}

