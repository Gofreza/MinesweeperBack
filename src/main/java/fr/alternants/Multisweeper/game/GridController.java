package fr.alternants.Multisweeper.game;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GridController {
    private final GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<NewGameResponse> newGame(@RequestBody NewGameRequest newGameRequest) {
        System.out.println("New game request: " + newGameRequest);
        return ResponseEntity.ok(gameService.newGame(newGameRequest));
    }

    @GetMapping()
    public ResponseEntity<PlayResponse> getGrid(@RequestBody Integer roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        List<PlayResponse.CellResponse> cellResponses = gameService.getGrid(roomId);
        return ResponseEntity.ok(new PlayResponse(cellResponses, gameService.soloGames.get(roomId).isGameEnded(), gameService.soloGames.get(roomId).isGameWin(), roomId, "", "getGrid"));
    }
    @DeleteMapping()
    public String deleteRoom(@RequestBody Integer roomId) {
        System.out.println("Delete grid, roomId: " + roomId);
        gameService.deleteRoom(roomId);
        return "Delete successful";
    }
}
