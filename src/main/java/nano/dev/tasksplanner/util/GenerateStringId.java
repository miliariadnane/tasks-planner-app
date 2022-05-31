package nano.dev.tasksplanner.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class GenerateStringId {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "[a-zA-Z0-9._]^+$dailyAPZDS584tasks89797021boudnb56456ERR64CD585";

    public String generateStringId(int lenght) {
        StringBuilder returnValue = new StringBuilder(lenght);

        for(int i=0; i< lenght; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
}
