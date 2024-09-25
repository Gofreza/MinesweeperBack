package fr.alternants.Multisweeper;

import fr.alternants.Multisweeper.game.core.Difficulty;
import fr.alternants.Multisweeper.game.core.Multisweeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultisweeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultisweeperApplication.class, args);
		Multisweeper multisweeper = new Multisweeper(4, 4, false, Difficulty.HARD);
		System.out.println(multisweeper);
		multisweeper.play(0, 0);
		System.out.println(multisweeper);
	}

}
