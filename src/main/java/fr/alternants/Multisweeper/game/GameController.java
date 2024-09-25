package fr.alternants.Multisweeper.game;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<Long> newGame(@RequestBody NewGameRequest newGameRequest) {
        return gameService.newGame(newGameRequest);
    }

    @PostMapping("/play")
    public ResponseEntity<PlayResponse> play(@RequestBody PlayRequest playRequest) {
        return gameService.play(playRequest);
    }
}
