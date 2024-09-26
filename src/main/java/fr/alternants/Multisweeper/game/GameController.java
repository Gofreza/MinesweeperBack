package fr.alternants.Multisweeper.game;

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
    public ResponseEntity<PlayResponse> flag(@RequestBody PlayRequest playRequest) {
        System.out.println("Flag request: " + playRequest);
        PlayResponse playResponse = gameService.flag(playRequest);
        if(playResponse == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(playResponse);
    }

    @PostMapping("/get")
    public ResponseEntity<PlayResponse> getGrid(@RequestBody PlayRequest playRequest) {
        System.out.println("Get grid, roomId: " + playRequest.getRoomId());
        List<PlayResponse.CellResponse> cellResponses = gameService.getGrid(playRequest.getRoomId());
        if(cellResponses == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(new PlayResponse(cellResponses, false, false));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteRoom(@RequestBody PlayRequest playRequest) {
        System.out.println("Delete room, roomId: " + playRequest.getRoomId());
        gameService.deleteRoom(playRequest.getRoomId());
        return ResponseEntity.ok("Delete successful");
    }
}
