package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Cell;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<Integer> newGame(@RequestBody NewGameRequest newGameRequest) {
        System.out.println("New game request: " + newGameRequest);
        Integer roomId = gameService.newGame(newGameRequest);
        if(roomId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/play")
    public ResponseEntity<PlayResponse> play(@RequestBody PlayRequest playRequest) {
        System.out.println("Play request: " + playRequest);
        PlayResponse playResponse = gameService.play(playRequest);
        if(playResponse == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(playResponse);
    }

    @PostMapping("/flag")
    public ResponseEntity<PlayResponse.CellResponse> flag(@RequestBody PlayRequest playRequest) {
        System.out.println("Flag request: " + playRequest);
        PlayResponse.CellResponse flag = gameService.flag(playRequest);
        if(flag == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(flag);
    }

    @GetMapping()
    public ResponseEntity<List<PlayResponse.CellResponse>> getGrid(@RequestBody Long roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        List<PlayResponse.CellResponse> cellResponses = gameService.getGrid(roomId);
        if(cellResponses == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(cellResponses);
    }
}
