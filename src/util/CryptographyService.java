package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public final class CryptographyService {
	
	private CryptographyService() { }
	
	private static final String PKI_DIRECTORY = System.getProperty("user.dir")+ File.separator + "src" + File.separator + "util" + File.separator + "pki" + File.separator;
	
	private static final String PrivateKeyPath = PKI_DIRECTORY + "Private.key";
	private static final String PublicKeyPath = PKI_DIRECTORY + "Public.key";
	private static final String CA1PrivateKeyPath = PKI_DIRECTORY + "CA1Private.key";
	private static final String CA2PrivateKeyPath = PKI_DIRECTORY + "CA2Private.key";
	
	private static final String RootCertificatePath = PKI_DIRECTORY + "root.crt";
	private static final String CA1CertificatePath = PKI_DIRECTORY + "CA1Certificate.crt";
	private static final String CA2CertificatePath = PKI_DIRECTORY + "CA2Certificate.crt";
	
	private static final String CRLCA1 = PKI_DIRECTORY + "CA1List.crl";
	private static final String CRLCA2 = PKI_DIRECTORY + "CA2List.crl";
	
	private static final String CACertificateTurn = PKI_DIRECTORY + "certificate.txt";
	private static final String SERIAL = PKI_DIRECTORY + "serial.txt";
	
	// users 
	private static final String USER_DIRECTORY = System.getProperty("user.dir") + File.separator + "src" + File.separator + "users" + File.separator;
	private static final String USER_CERTIFICATE = USER_DIRECTORY + "certificates" + File.separator;
	
	public static final String resultsFilePath = USER_DIRECTORY + "PResults.txt";
	public static final String resultsDataFilePath = USER_DIRECTORY + "IResults.txt";
	private static final String usersFilePath =  USER_DIRECTORY + "PList.txt";
	private static final String usersDataFilePath = USER_DIRECTORY + "IList.txt";
	
	private static JcaX509v3CertificateBuilder certificateBuilder;
	private static X509v2CRLBuilder CRLBuilder;
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	// ASYMMETRIC ALGORITHM
	private static PrivateKey getPrivateKey(String filename) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
		
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}
	
	private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(Paths.get(PublicKeyPath));
		
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}
	
	// Root keys usage for digital envelope
	private static String encryptWithAsymmetricAlgorithm(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
		
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return new String(Base64.getEncoder().encode(encryptedBytes));
	}
	
	private static String decryptWithAsymmetricAlgorithm(byte[] encryptedData) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, IOException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(PrivateKeyPath));
		byte[] encryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(encryptedBytes);
	}
	
	// SYMMETRIC ALGORITHMS
	private static String generateSessionKey(String algorithm, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		keyGenerator.init(keySize);
		SecretKey secretKey = keyGenerator.generateKey();
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}
	
	private static void saveEncryptionData(String sessionKey, String algorithmKey, String encryptionDataPath) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException, IllegalBlockSizeException, BadPaddingException {
		String data = algorithmKey + "#" + sessionKey;
		String encryptedData = encryptWithAsymmetricAlgorithm(data);
		
		PrintWriter pw = new PrintWriter(new FileWriter(new File(encryptionDataPath), false));
		pw.print(encryptedData);
		pw.flush();
		pw.close();
	}
	
	private static String encryptWithSymmetricAlgorithm(String plaintext, String sessionKey, String algorithmName, String algorithmKey, int initializationVectorSize, int keySize) throws Exception {
		
		byte[] plainTextData = plaintext.getBytes();
		byte[] initializationVector = new byte[initializationVectorSize];
		
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(initializationVector);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(sessionKey.getBytes(StandardCharsets.UTF_8));
		
		byte[] keyBytes = new byte[keySize];
		System.arraycopy(messageDigest.digest(), 0, keyBytes, 0, keyBytes.length);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, algorithmKey);
		
		Cipher cipher = Cipher.getInstance(algorithmName);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		
		byte[] encrypted = cipher.doFinal(plainTextData);
		
		byte[] encryptedIVAndText = new byte[initializationVectorSize + encrypted.length];
		System.arraycopy(initializationVector, 0, encryptedIVAndText, 0, initializationVectorSize);
		System.arraycopy(encrypted, 0, encryptedIVAndText, initializationVectorSize, encrypted.length);
		
		return new String(Base64.getEncoder().encode(encryptedIVAndText));
	}
	
	private static String decryptWithSymmetricAlgorithm(byte[] encryptedText, String sessionKey, String algorithmName, String algorithmKey, int initializationVectorSize, int keySize) throws Exception{
		
		byte[] dataBytes = Base64.getDecoder().decode(encryptedText);
		
		byte[] initializationVector = new byte[initializationVectorSize];
		System.arraycopy(dataBytes, 0, initializationVector, 0, initializationVector.length);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		
		int encryptedBytesSize = dataBytes.length - initializationVectorSize;
		byte[] encryptedBytes = new byte[encryptedBytesSize];
		System.arraycopy(dataBytes, initializationVectorSize, encryptedBytes, 0, encryptedBytesSize);

		byte[] keyBytes = new byte[keySize];
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(sessionKey.trim().getBytes(StandardCharsets.UTF_8));
		System.arraycopy(messageDigest.digest(), 0, keyBytes, 0, keyBytes.length);

		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, algorithmKey);

		Cipher cipher = Cipher.getInstance(algorithmName);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decrypted = cipher.doFinal(encryptedBytes);
		return new String(decrypted);
	}
	
	public static void encryptData(String plainText, String encryptionTextPath, String encryptionDataPath) throws Exception {
		boolean algorithm = (new Random()).nextBoolean();
		String sessionKey;
		String encrypted;
		
		if(algorithm) {
			sessionKey = generateSessionKey("AES", 256);
			encrypted = encryptWithSymmetricAlgorithm(plainText, sessionKey, "AES/CBC/PKCS5Padding", "AES", 16, 16);
			saveEncryptionData(sessionKey, "AES", encryptionDataPath);
		} else {
			sessionKey = generateSessionKey("DES", 56);
			encrypted = encryptWithSymmetricAlgorithm(plainText, sessionKey, "DES/CBC/PKCS5Padding", "DES", 8, 8);
			saveEncryptionData(sessionKey, "DES", encryptionDataPath);
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(new File(encryptionTextPath), false));
		pw.print(encrypted);
		pw.flush();
		pw.close();
	}
	
	public static String decryptData(byte[] encryptedData, String encryptionDataPath) throws Exception {
		byte[] encryptionData = Files.readAllBytes(Paths.get(encryptionDataPath));
		String encryptioDataPlainText = decryptWithAsymmetricAlgorithm(encryptionData);
		
		String[] data = encryptioDataPlainText.split("#");
		String plainText;
		
		if(data[0].equals("AES")) {
			plainText = decryptWithSymmetricAlgorithm(encryptedData, data[1], "AES/CBC/PKCS5Padding", "AES", 16, 16);
		} else {
			plainText = decryptWithSymmetricAlgorithm(encryptedData, data[1], "DES/CBC/PKCS5Padding", "DES", 8, 8);
		}
		
		return plainText;
	}
	
	public static List<String> encryptDataList(List<String> plainTextData, String encryptionDataPath) throws Exception{
		String sessionKey = generateSessionKey("AES", 256);
		List<String> encryptedList = new ArrayList<>();
		
		for(String data : plainTextData) {
			encryptedList.add(encryptWithSymmetricAlgorithm(data, sessionKey, "AES/CBC/PKCS5Padding", "AES", 16, 16));
		}
		
		saveEncryptionData(sessionKey, "AES", encryptionDataPath);
		
		return encryptedList;
	}
	
	
	// CERTIFICATES
	private static String getHashValue(String password, byte[] salt) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(salt);
		byte[] bytes = messageDigest.digest(password.getBytes());
		return new String(Base64.getEncoder().encode(bytes));
	}
	
	private static X509Certificate loadCertificate(String filename) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		FileInputStream stream = new FileInputStream(filename);
		X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(stream);
		
		return certificate;
	}
	
	private static PKCS12Store getCACertificate() throws Exception {
		// check which certificate is next used
		byte[] serialBytes = Files.readAllBytes(Paths.get(CACertificateTurn));
		int turn = Integer.valueOf((new String(serialBytes)));
		
		String certificatePath;
		String privateKeyPath;
		if(turn % 2 == 0) {
			certificatePath = CA1CertificatePath;
			privateKeyPath = CA1PrivateKeyPath;
		} else {
			certificatePath = CA2CertificatePath;
			privateKeyPath = CA2PrivateKeyPath;
		}
		
		// loading right certificate
		X509Certificate certificate = loadCertificate(certificatePath);
		PrivateKey key = getPrivateKey(privateKeyPath);
		
		// write new value in file
		++turn;
		PrintWriter pw = new PrintWriter(new FileWriter(new File(CACertificateTurn), false));
		pw.print(turn);
		pw.flush();
		pw.close();
		
		return new PKCS12Store(certificate, key);
	}
	
	private static KeyPair generateKeys() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		return keyPair;
	}
	
	private static String createUserCertificate(String name, String surname, String username, String password) throws Exception {
		// load serial number
		byte[] serialBytes = Files.readAllBytes(Paths.get(SERIAL));
		long number = Long.valueOf((new String(serialBytes)));

		BigInteger serialNumber = BigInteger.valueOf(number);
		// load CA
		PKCS12Store CACertificatePair = getCACertificate();
		// generate user key
		KeyPair keys = generateKeys();
		
		// determine duration of the certificate
		long now = System.currentTimeMillis();
		Date startDate = new Date(now);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, 3);
		Date endDate = calendar.getTime();
		
		// store common name
		X500Name CNName = new X500Name("CN=" + name + " " + surname);
		
		// generate certificate
		certificateBuilder = new JcaX509v3CertificateBuilder(CACertificatePair.certificate, serialNumber, startDate, endDate, CNName, keys.getPublic());
		certificateBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
		certificateBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		
		X509Certificate userCertificate = new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(new JcaContentSignerBuilder("SHA512withRSA").setProvider("BC").build(CACertificatePair.key)));
		
		// PKCS#12
		// get root certificate
		X509Certificate rootCertificate = loadCertificate(RootCertificatePath);
		// create chain
		Certificate[] chain = { userCertificate,  CACertificatePair.certificate, rootCertificate};
		KeyStore outStore = KeyStore.getInstance("PKCS12");
		outStore.load(null, password.toCharArray());
		// create entry in pkcs12 file
		outStore.setKeyEntry(username, keys.getPrivate(), password.toCharArray(), chain);
		
		String userCertificatePath = USER_CERTIFICATE + username + ".p12";
		
		// write PKCS12
		FileOutputStream out = new FileOutputStream(userCertificatePath);
		outStore.store(out, password.toCharArray());
		out.flush();
		out.close();
		
		// write new value in serial file
		++number;
		PrintWriter pw = new PrintWriter(new FileWriter(new File(SERIAL), false));
		pw.print(number);
		pw.flush();
		pw.close();
		
		return userCertificatePath;
	}
	
	public static String generateCertificate(String name, String surname, String username, String password) throws Exception {
		if(checkIfUsernameIsUnique(username)) {
			// generate PKCS#12
			String path = createUserCertificate(name, surname, username, password);
			// calculate hashed password
			String hashedPassword  = getHashValue(password, username.getBytes());
			// write in list new user with 0 number of tries
			String newUser = username + "," + hashedPassword + "," + path + ",0";
			//System.out.println(newUser);
			// append in users file
			byte[] listOfUsers = Files.readAllBytes(Paths.get(usersFilePath));
			String users;
			if(listOfUsers.length != 0) {
				users = decryptData(listOfUsers, usersDataFilePath);
				//System.out.println(users);
				users += ("#" + newUser);
			} else {
				users = newUser;
			}
			// encrypt data
			encryptData(users, usersFilePath, usersDataFilePath);
			
			return path;
		} else {
			throw new Exception("Korisnicko ime mora da bude jedinstveno!");
		}
	}
	
	private static String getUsersList() throws Exception {
		byte[] encryptedListOfUsers = Files.readAllBytes(Paths.get(usersFilePath));
		
		if(encryptedListOfUsers.length != 0) {
			return decryptData(encryptedListOfUsers, usersDataFilePath);
		} else {
			return null;
		}
	}
	
	private static String[] getUserInfo(String username) throws Exception {
		String listOfUsers = getUsersList();
		if(listOfUsers != null) {
			String[] users = listOfUsers.split("#");
			for(String user : users) {
				String[] info = user.split(",");
				if(username.equals(info[0])) {
					return info;
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	private static boolean checkIfUsernameIsUnique(String username) throws Exception {
		String[] userInfo = getUserInfo(username);
		if(userInfo == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean validateUserParameters(String username, String password, String certificatePath) throws Exception {
		String[] userInfo = getUserInfo(username);
		if(userInfo != null) {
			if(userInfo[1].equals(getHashValue(password, username.getBytes())) && userInfo[2].equals(certificatePath)) {
				// check if revoked
				X509Certificate certificate = getUserCertificate(username, password, certificatePath);
				return !isRevoked(certificate);
			} else {
				throw new Exception("Lozinka ili sertifikat ne odgovaraju korisnickom imenu!");
			}
		} else {
			throw new Exception("Niste se prijavili!");
		}
	}
	
	private static X509Certificate getUserCertificate(String username, String password, String certificatePath) throws Exception {
		KeyStore inStore = KeyStore.getInstance("PKCS12");
		inStore.load(new FileInputStream(certificatePath), password.toCharArray());
		Certificate[] inChain = inStore.getCertificateChain(username);
		X509Certificate certificate = (X509Certificate)inChain[0];
		
		return certificate;
	}
	
	private static void revoke(String CRLPath, PrivateKey CAPrivateKey, BigInteger certificateSerialNumber) throws Exception {
		DataInputStream inStream = new DataInputStream(new FileInputStream(CRLPath));
		
		X509CRLHolder holder = new X509CRLHolder(inStream);
		CRLBuilder = new X509v2CRLBuilder(holder);
		CRLBuilder.setNextUpdate(holder.getNextUpdate());
		CRLBuilder.addCRLEntry(certificateSerialNumber, new Date(), 5); // 5 - cesstionOfOperation
		
		JcaContentSignerBuilder contentSinger = new JcaContentSignerBuilder("SHA512WithRSA");
		contentSinger.setProvider("BC");
		holder = CRLBuilder.build(contentSinger.build(CAPrivateKey));
		JcaX509CRLConverter converter = new JcaX509CRLConverter();
		converter.setProvider("BC");
		X509CRL CRL = converter.getCRL(holder);
		
		DataOutputStream outStream = new DataOutputStream(new FileOutputStream(CRLPath));
		outStream.write(CRL.getEncoded());
		outStream.flush();
		outStream.close();
	}
	
	private static boolean isRevoked(X509Certificate certificate) throws Exception {
		BigInteger serialNumber = certificate.getSerialNumber();
		String issuer = certificate.getIssuerX500Principal().getName();
		
		String CRLPath = null;
		if(issuer.contains("1")) {
			CRLPath = CRLCA1;
		} else {
			CRLPath = CRLCA2;
		}
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		DataInputStream inStream = new DataInputStream(new FileInputStream(CRLPath));
		X509CRL CRL = (X509CRL)certificateFactory.generateCRL(inStream);
		X509CRLEntry certificateEntry = CRL.getRevokedCertificate(serialNumber);
		
		return (certificateEntry != null);
	}
	
	public static void revokeCertificate(User user) {
		try {
			X509Certificate certificate = getUserCertificate(user.getUsername(), user.getPassword(), user.getCertificatePath());
			BigInteger serialNumber = certificate.getSerialNumber();
			String issuerName = certificate.getIssuerX500Principal().getName();
			
			PrivateKey CAKey = null;
			String CRLPath = null;
			
			if(issuerName.contains("1")) {
				CAKey = getPrivateKey(CA1PrivateKeyPath);
				CRLPath = CRLCA1;
			} else {
				CAKey = getPrivateKey(CA2PrivateKeyPath);
				CRLPath = CRLCA2;
			}
			
			revoke(CRLPath, CAKey, serialNumber);
			
			System.out.println("Certificate revoked!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static int incrementNumberOfPlays(String username) {
		try {
			String usersList = getUsersList();
			if(usersList != null) {
				String[] users = usersList.split("#");
				int toReturn = -1;
				String newUsersList = "";
				
				for(int i = 0; i < users.length; ++i) {
					String[] info = users[i].split(",");
					if(username.equals(info[0])) {
						int numberOfPlays = Integer.valueOf(info[3]);
						++numberOfPlays;
						info[3] = String.valueOf(numberOfPlays);
						System.out.println(numberOfPlays);
						toReturn = numberOfPlays;
						if(i < users.length - 1) {
							newUsersList += (info[0] + "," + info[1] + "," + info[2] + "," + info[3] + "#");
						} else {
							newUsersList += (info[0] + "," + info[1] + "," + info[2] + "," + info[3]);
						}
					} else {
						if(i < users.length - 1) {
							newUsersList += (users[i] + "#");
						} else {
							newUsersList += users[i];
						}
					}
				}
				
				encryptData(newUsersList, usersFilePath, usersDataFilePath);
				return toReturn;
			} else {
				return -1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}

class PKCS12Store {
	
	public X509Certificate certificate;
	public PrivateKey key;
	
	public PKCS12Store(X509Certificate certificate, PrivateKey key) {
		this.certificate = certificate;
		this.key = key;
	}
}
