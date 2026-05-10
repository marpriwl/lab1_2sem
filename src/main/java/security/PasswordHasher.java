package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {  //класс, который отвечает за хеширование и проверку паролей

    public String hash(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");  //создаем объект, который умеет считать SHA-256
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);  //переводим пароль из строки в массив байтов
            byte[] hashBytes = digest.digest(passwordBytes);  //считаем хеш

            StringBuilder result = new StringBuilder();  //объект для сборки итоговой строки

            for (byte hashByte : hashBytes) {  //для каждого hashByte из массива hashBytes
                result.append(String.format("%02x", hashByte));  //переводим байт в hex-вид и добавляем к итоговой строк
            }

            return result.toString(); //возвращаем хеш как строку
        } catch (NoSuchAlgorithmException exception) {  //ловим ошибку
            throw new RuntimeException("SHA-256 algorithm is not available", exception);
        }
    }

    public boolean matches(String password, String passwordHash) {
        String hashedPassword = hash(password);  //переводим пароль в хеш
        return hashedPassword.equals(passwordHash);  //сравниваем хеши

    }
}
